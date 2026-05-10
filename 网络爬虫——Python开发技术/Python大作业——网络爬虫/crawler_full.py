"""
第一次全量爬取脚本（通用版）：
- 运行时让用户手动输入“起始网址”（如某个网站首页或文章列表页）
- 在该网站域名下爬取网页中的链接
- 将访问过的页面链接写入 links.txt
- 将每个页面解析为文章内容并保存到本地“文章”文件夹
"""

import os
from collections import deque
from urllib.parse import urljoin, urlparse
import time
from typing import Set, Optional, Callable
import threading

import requests
from bs4 import BeautifulSoup

from config import LINKS_DB_FILE, ARTICLE_DIR
from utils_crawler import get_random_headers, get_article_to_word


EXCLUDE_EXTENSIONS = (
    ".jpg",
    ".jpeg",
    ".png",
    ".gif",
    ".bmp",
    ".svg",
    ".ico",
    ".css",
    ".js",
    ".pdf",
    ".zip",
    ".rar",
    ".7z",
    ".mp4",
    ".mp3",
)


def load_existing_links() -> Set[str]:
    """读取已存在的 links.txt，返回已记录链接集合。若文件不存在则返回空集合。"""
    try:
        with open(LINKS_DB_FILE, "r", encoding="utf-8") as f:
            return {line.strip() for line in f if line.strip()}
    except FileNotFoundError:
        return set()


def append_link_to_db(link: str) -> None:
    """将单个链接追加写入 links.txt。"""
    with open(LINKS_DB_FILE, "a", encoding="utf-8") as f:
        f.write(link + "\n")


def choose_save_folder(default_name: str) -> str:
    """
    让用户选择将本次文章保存到“文章”目录下已有的子文件夹，
    或者新建一个子文件夹。
    """
    if not os.path.exists(ARTICLE_DIR):
        os.makedirs(ARTICLE_DIR)

    subfolders = [
        name
        for name in os.listdir(ARTICLE_DIR)
        if os.path.isdir(os.path.join(ARTICLE_DIR, name))
    ]

    if subfolders:
        print("检测到以下已有保存文件夹：")
        for idx, name in enumerate(subfolders, start=1):
            print(f"{idx}. {name}")
        print(f"{len(subfolders) + 1}. 新建一个文件夹")

        choice = input("请选择保存位置的序号（直接回车默认新建）：").strip()
        try:
            idx = int(choice)
        except ValueError:
            idx = len(subfolders) + 1

        if 1 <= idx <= len(subfolders):
            return subfolders[idx - 1]

    folder_name = input("请输入本次爬取保存的文件夹名称（将在“文章”目录下创建）：").strip()
    if not folder_name:
        folder_name = default_name
    return folder_name


def is_valid_url(url: str, domain: str) -> bool:
    """判断链接是否为同一域名下的 http/https 页面，且排除常见静态资源。"""
    parsed = urlparse(url)
    if parsed.scheme not in ("http", "https"):
        return False
    if parsed.netloc and parsed.netloc != domain:
        return False
    lower = url.lower()
    if any(lower.endswith(ext) for ext in EXCLUDE_EXTENSIONS):
        return False
    return True


def crawl_site(
    start_url: str,
    max_pages: int,
    only_chapter: bool = False,
    sub_folder: Optional[str] = None,
    stop_flag: Optional[Callable[[], bool]] = None,
    progress_callback: Optional[Callable[[int, int, int], None]] = None,
    worker_count: int = 1,
    force_full: bool = False,
) -> None:
    """
    从起始网址开始，对同一域名下的页面进行广度优先爬取。
    访问到的每个页面：
    - 若未在 links.txt 中出现过，则视为新"文章"，下载并保存；
    - 从页面中继续提取超链接，加入待访问队列。
    
    参数:
        force_full: 如果为 True，忽略 links.txt 中的历史记录，强制重新下载所有链接（全量模式）。
                   如果为 False，读取 links.txt 只下载新链接（增量模式）。
    """
    parsed_start = urlparse(start_url)
    domain = parsed_start.netloc
    if not domain:
        raise ValueError("起始网址格式不正确，请包含 http:// 或 https://")

    # 全量模式：忽略历史记录；增量模式：加载历史记录
    if force_full:
        existing_links = set()  # 全量模式：忽略 links.txt，所有链接都视为新的
    else:
        existing_links = load_existing_links()  # 增量模式：加载 links.txt
    visited: Set[str] = set()
    queue: deque[str] = deque([start_url])

    downloaded_count = 0
    skipped_count = 0

    # 线程安全相关
    lock = threading.Lock()
    # 规范线程数量（1~10）
    if worker_count < 1:
        worker_count = 1
    if worker_count > 10:
        worker_count = 10

    print(f"开始爬取网站：{start_url}")
    print(f"同一域名限制：{domain}")
    print(f"工作线程数量：{worker_count}")

    def worker() -> None:
        nonlocal downloaded_count, skipped_count
        while True:
            with lock:
                if stop_flag and stop_flag():
                    break
                if not queue or len(visited) >= max_pages:
                    break
                current_url = queue.popleft()
                if current_url in visited:
                    # 已访问过，只更新进度
                    if progress_callback:
                        progress_callback(len(visited), downloaded_count, skipped_count)
                    continue
                visited.add(current_url)
                is_new = current_url not in existing_links

            saved = False

            # 把当前页面当作一篇“文章”处理（如果之前没记录过）
            if is_new:
                try:
                    saved = get_article_to_word(current_url, only_chapter=only_chapter, sub_folder=sub_folder)
                except Exception as e:  # noqa: BLE001
                    print(f"[下载失败] {current_url}，错误：{e}")

            # 再抓取当前页面中的所有链接，用于继续扩展爬取范围
            new_links: list[str] = []
            try:
                resp = requests.get(current_url, headers=get_random_headers(), timeout=10)
                resp.raise_for_status()
                soup = BeautifulSoup(resp.content, "lxml")
                for a in soup.find_all("a", href=True):
                    href = a["href"].strip()
                    if not href or href.startswith("#"):
                        continue
                    new_url = urljoin(current_url, href)
                    if is_valid_url(new_url, domain):
                        new_links.append(new_url)
            except Exception as e:  # noqa: BLE001
                print(f"[访问失败] {current_url}，错误：{e}")

            with lock:
                # 无论全量还是增量模式，都要将访问的链接写入 links.txt（如果还未写入）
                # 全量模式下，所有链接都是新的，都会写入
                # 增量模式下，只有新链接才会写入
                if current_url not in existing_links:
                    append_link_to_db(current_url)
                    existing_links.add(current_url)

                # 统计下载和跳过数量
                if saved:
                    downloaded_count += 1
                    print(f"[下载成功] {current_url}")
                elif is_new:
                    skipped_count += 1
                    print(f"[跳过（非章节标题，仅记录链接）] {current_url}")

                # 将新链接加入队列（全量模式下，所有链接都会加入；增量模式下，只加入新链接）
                for new_url in new_links:
                    if new_url not in visited and new_url not in existing_links:
                        queue.append(new_url)

                # 每次循环结束后更新一次进度
                if progress_callback:
                    progress_callback(len(visited), downloaded_count, skipped_count)

            time.sleep(0.3)  # 避免请求过快

    # 根据线程数选择执行方式
    if worker_count == 1:
        worker()
    else:
        threads = []
        for _ in range(worker_count):
            t = threading.Thread(target=worker, daemon=True)
            t.start()
            threads.append(t)
        for t in threads:
            t.join()

    print(f"爬取结束，本次共访问页面 {len(visited)} 个，新下载文章 {downloaded_count} 篇。")


def main() -> None:
    print("==== 通用网站文章爬虫（全量版）====")
    start_url = input("请输入要爬取的目标网址（例如某网站首页或文章列表页）：\n").strip()
    if not start_url:
        print("未输入网址，程序退出。")
        return

    max_pages_input = input("请输入最多爬取的页面数量（建议 50~500，留空默认 100）：").strip()
    try:
        max_pages = int(max_pages_input) if max_pages_input else 100
    except ValueError:
        max_pages = 100

    chapter_choice = input(
        "是否只爬取小说章节格式标题的文章（如“第1章…、第一章…、001章、正文1、第001章”等）？(y/N)："
    ).strip().lower()
    only_chapter = chapter_choice == "y"

    folder_name = choose_save_folder(default_name="默认批次")

    try:
        crawl_site(start_url, max_pages=max_pages, only_chapter=only_chapter, sub_folder=folder_name, force_full=True)
    except Exception as e:  # noqa: BLE001
        print(f"运行过程中出现错误：{e}")

    print("文章保存在当前项目下的“文章”文件夹中。")
    input("按回车键退出程序...")


if __name__ == "__main__":
    main()


