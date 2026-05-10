"""
后续增量更新爬取脚本（通用版）：
- 运行时让用户手动输入“起始网址”（建议与第一次全量爬取时相同）
- 复用 crawler_full 中的 crawl_site 逻辑
- 利用 links.txt 去重：只对 links.txt 中不存在的新链接进行记录和下载

说明：
- 因为我们现在是通用 HTML 网站爬虫，无法像之前示例那样直接调用 JSON 接口获取“最新 N 篇文章”；
- 所以“增量更新”的实现方式就是：
  - 再次从首页/列表页开始 BFS 遍历同一域名下的页面；
  - 对每个页面检查是否在 links.txt 中；
  - 只有不在 links.txt 的页面才会被视为“新文章”并下载。
"""

from crawler_full import crawl_site, choose_save_folder


def main() -> None:
    print("==== 通用网站文章爬虫（增量更新版）====")
    start_url = input("请输入要刷新检查的目标网址（建议与全量爬取时相同）：\n").strip()
    if not start_url:
        print("未输入网址，程序退出。")
        return

    max_pages_input = input("本次最多检查的页面数量（建议 30~200，留空默认 80）：").strip()
    try:
        max_pages = int(max_pages_input) if max_pages_input else 80
    except ValueError:
        max_pages = 80

    chapter_choice = input(
        "是否只爬取小说章节格式标题的文章（如“第1章…、第一章…、001章、正文1、第001章”等）？(y/N)："
    ).strip().lower()
    only_chapter = chapter_choice == "y"

    folder_name = choose_save_folder(default_name="增量批次")

    try:
        crawl_site(start_url, max_pages=max_pages, only_chapter=only_chapter, sub_folder=folder_name)
    except Exception as e:  # noqa: BLE001
        print(f"运行过程中出现错误：{e}")

    print("增量检查完成，新文章已保存到“文章”文件夹（如有）。")
    input("按回车键退出程序...")


if __name__ == "__main__":
    main()


