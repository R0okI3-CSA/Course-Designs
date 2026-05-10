"""
Tkinter 图形界面：
- 主界面：选择“全量爬取”或“增量爬取”
- 全量爬取界面：对应 crawler_full.crawl_site
- 增量爬取界面：对应 crawler_update / crawler_full.crawl_site

界面风格：白底 + 蓝绿清新搭配，字号和布局适当放大，整体简洁舒适。
"""

import threading
import tkinter as tk
from tkinter import ttk, messagebox
from typing import Optional

from matplotlib.backends.backend_tkagg import FigureCanvasTkAgg

from config import ARTICLE_DIR
from crawler_full import crawl_site
from utils_crawler import sanitize_filename
from text_analysis import (
    analyze_high_freq_words,
    analyze_roles_frequency,
    build_bar_figure,
    build_wordcloud_figure,
)

# 配色与字体（浅色蓝绿清新风格，参考网站/系统淡色主题）
BG_WHITE = "#F7FBFF"          # 整体背景：很浅的蓝白
PRIMARY_BLUE = "#42A5F5"      # 主色：柔和的亮蓝色
ACCENT_GREEN = "#66BB6A"      # 辅色：清新的嫩绿色
TEXT_DARK = "#1F2933"         # 深色文字：深灰蓝
SECTION_BG = "#E3F2FD"        # 区块背景：淡淡的蓝色卡片底

FONT_TITLE = ("Microsoft YaHei", 16, "bold")
FONT_LABEL = ("Microsoft YaHei", 11)
FONT_BUTTON = ("Microsoft YaHei", 11, "bold")
FONT_INPUT = ("Microsoft YaHei", 10)
FONT_LOG = ("Consolas", 9)

# 输入/选择控件统一白底（用户要求）
INPUT_BG = "#FFFFFF"


def list_article_subfolders() -> list[str]:
    """列出“文章”目录下已有的子文件夹名称。"""
    import os

    if not os.path.exists(ARTICLE_DIR):
        return []
    return [
        name
        for name in os.listdir(ARTICLE_DIR)
        if os.path.isdir(os.path.join(ARTICLE_DIR, name))
    ]


def run_crawl_with_ui(
    start_url: str,
    max_pages: int,
    only_chapter: bool,
    sub_folder: str,
    log_widget: tk.Text,
    stop_event: threading.Event,
    progress_bar: ttk.Progressbar,
    stats_label: tk.Label,
    worker_count: int,
    force_full: bool = False,
) -> None:
    """在当前界面中运行爬虫（在子线程中执行，日志输出到 Text 控件）。"""

    def logger(msg: str) -> None:
        log_widget.insert(tk.END, msg + "\n")
        log_widget.see(tk.END)
        log_widget.update_idletasks()

    def on_progress(visited: int, downloaded: int, skipped: int) -> None:
        # 更新进度条
        progress_bar["maximum"] = max_pages
        progress_bar["value"] = min(visited, max_pages)
        # 更新统计信息
        stats_label.config(
            text=f"已访问：{visited}/{max_pages}    新下载：{downloaded}    跳过：{skipped}"
        )

    try:
        logger(f"开始爬取：{start_url}")
        crawl_site(
            start_url=start_url,
            max_pages=max_pages,
            only_chapter=only_chapter,
            sub_folder=sanitize_filename(sub_folder),
            stop_flag=stop_event.is_set,
            progress_callback=on_progress,
            worker_count=worker_count,
            force_full=force_full,
        )
        if stop_event.is_set():
            logger("已停止爬取。")
        else:
            logger("爬取完成。")
            messagebox.showinfo("完成", "爬取完成，请到“文章”目录下查看结果。")
    except Exception as e:  # noqa: BLE001
        logger(f"运行出错：{e}")
        messagebox.showerror("错误", f"运行过程中出现错误：{e}")


def create_full_tab(frame: tk.Frame) -> None:
    """全量爬取界面。"""
    frame.configure(bg=BG_WHITE, highlightbackground=PRIMARY_BLUE, highlightthickness=1)

    # 输入区域
    tk.Label(frame, text="起始网址：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=0, column=0, sticky="e", padx=10, pady=8
    )
    entry_url = tk.Entry(
        frame, width=70, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_url.grid(row=0, column=1, columnspan=3, sticky="w", padx=10, pady=8)

    tk.Label(frame, text="最多页面数：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=1, column=0, sticky="e", padx=10, pady=8
    )
    entry_max = tk.Entry(
        frame, width=12, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_max.insert(0, "100")
    entry_max.grid(row=1, column=1, sticky="w", padx=10, pady=8)

    # 多线程选项
    multi_thread_var = tk.BooleanVar(value=False)
    chk_multi = tk.Checkbutton(
        frame,
        text="启用多线程（最多 10 个线程）",
        variable=multi_thread_var,
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        anchor="w",
        selectcolor=BG_WHITE,
    )
    chk_multi.grid(row=1, column=2, sticky="w", padx=10, pady=8)

    tk.Label(frame, text="线程数：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=1, column=3, sticky="e", padx=5, pady=8
    )
    spin_threads = tk.Spinbox(
        frame,
        from_=1,
        to=10,
        width=4,
        font=FONT_INPUT,
        bg=INPUT_BG,
        fg=TEXT_DARK,
        insertbackground=TEXT_DARK,
        readonlybackground=INPUT_BG,
    )
    spin_threads.delete(0, tk.END)
    spin_threads.insert(0, "3")
    spin_threads.grid(row=1, column=4, sticky="w", padx=5, pady=8)

    only_chapter_var = tk.BooleanVar(value=False)
    chk_only = tk.Checkbutton(
        frame,
        text="只爬取小说章节标题文章（第1章/第一章/001章/正文1 等）",
        variable=only_chapter_var,
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        anchor="w",
        selectcolor=BG_WHITE,
    )
    chk_only.grid(row=2, column=0, columnspan=4, sticky="w", padx=10, pady=8)

    # 保存目录选择
    tk.Label(frame, text="保存到文件夹：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=3, column=0, sticky="ne", padx=10, pady=8
    )

    folder_mode = tk.StringVar(value="existing")
    radio_existing = tk.Radiobutton(
        frame,
        text="使用已有文件夹",
        variable=folder_mode,
        value="existing",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        selectcolor=BG_WHITE,
    )
    radio_new = tk.Radiobutton(
        frame,
        text="新建文件夹",
        variable=folder_mode,
        value="new",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        selectcolor=BG_WHITE,
    )
    radio_existing.grid(row=3, column=1, sticky="w", padx=10, pady=4)
    radio_new.grid(row=4, column=1, sticky="w", padx=10, pady=4)

    existing_folders = list_article_subfolders()
    combo_existing = ttk.Combobox(
        frame,
        values=existing_folders,
        state="readonly",
        width=32,
        font=FONT_INPUT,
        style="White.TCombobox",
    )
    if existing_folders:
        combo_existing.current(0)
    combo_existing.grid(row=3, column=2, sticky="w", padx=10, pady=4)

    # 刷新文件夹列表按钮
    def refresh_folders() -> None:
        nonlocal existing_folders
        existing_folders = list_article_subfolders()
        combo_existing["values"] = existing_folders
        if existing_folders:
            combo_existing.current(0)

    btn_refresh = tk.Button(
        frame,
        text="刷新",
        command=refresh_folders,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        relief="ridge",
        padx=8,
        pady=2,
    )
    btn_refresh.grid(row=3, column=3, sticky="w", padx=5, pady=4)

    entry_new_folder = tk.Entry(
        frame, width=32, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_new_folder.insert(0, "文件夹名称_默认批次")
    entry_new_folder.grid(row=4, column=2, sticky="w", padx=10, pady=4)

    # 日志窗口
    tk.Label(frame, text="运行日志：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=5, column=0, sticky="ne", padx=10, pady=8
    )
    txt_log = tk.Text(frame, width=82, height=18, font=FONT_LOG, bg=SECTION_BG, fg=TEXT_DARK, relief="solid", bd=1)
    txt_log.grid(row=5, column=1, columnspan=3, sticky="w", padx=10, pady=8)
    scroll = tk.Scrollbar(frame, command=txt_log.yview)
    scroll.grid(row=5, column=4, sticky="ns", pady=8)
    txt_log["yscrollcommand"] = scroll.set

    # 进度条和统计信息
    progress_bar = ttk.Progressbar(
        frame,
        orient="horizontal",
        mode="determinate",
        length=420,
        style="Blue.Horizontal.TProgressbar",
    )
    progress_bar.grid(row=6, column=1, columnspan=3, sticky="w", padx=10, pady=4)

    stats_label = tk.Label(
        frame,
        text="已访问：0/0    新下载：0    跳过：0",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        anchor="w",
    )
    stats_label.grid(row=7, column=1, columnspan=3, sticky="w", padx=10, pady=4)

    stop_event = threading.Event()
    crawl_thread: Optional[threading.Thread] = None

    def on_start() -> None:
        url = entry_url.get().strip()
        if not url:
            messagebox.showwarning("提示", "请先输入起始网址。")
            return
        max_txt = entry_max.get().strip() or "100"
        try:
            max_pages = int(max_txt)
        except ValueError:
            messagebox.showwarning("提示", "最多页面数必须是整数。")
            return

        # 线程数量
        if multi_thread_var.get():
            threads_txt = spin_threads.get().strip() or "3"
            try:
                worker_count = int(threads_txt)
            except ValueError:
                messagebox.showwarning("提示", "线程数必须是 1~10 之间的整数。")
                return
            if worker_count < 1 or worker_count > 10:
                messagebox.showwarning("提示", "线程数必须在 1 到 10 之间。")
                return
        else:
            worker_count = 1

        if folder_mode.get() == "existing" and existing_folders:
            sub_folder = combo_existing.get().strip() or "默认批次"
        else:
            sub_folder = entry_new_folder.get().strip() or "默认批次"

        txt_log.delete("1.0", tk.END)
        stop_event.clear()

        # 重置进度条和统计
        progress_bar["value"] = 0
        stats_label.config(text=f"已访问：0/{max_pages}    新下载：0    跳过：0")

        def worker() -> None:
            run_crawl_with_ui(
                start_url=url,
                max_pages=max_pages,
                only_chapter=only_chapter_var.get(),
                sub_folder=sub_folder,
                log_widget=txt_log,
                stop_event=stop_event,
                progress_bar=progress_bar,
                stats_label=stats_label,
                worker_count=worker_count,
                force_full=True,
            )
            btn_start.config(state=tk.NORMAL)
            btn_stop.config(state=tk.DISABLED)

        nonlocal crawl_thread
        crawl_thread = threading.Thread(target=worker, daemon=True)
        crawl_thread.start()
        btn_start.config(state=tk.DISABLED)
        btn_stop.config(state=tk.NORMAL)

    def on_stop() -> None:
        if stop_event and not stop_event.is_set():
            stop_event.set()
            txt_log.insert(tk.END, "收到停止指令，正在尝试停止爬取...\n")
            txt_log.see(tk.END)

    btn_start = tk.Button(
        frame,
        text="开始全量爬取",
        command=on_start,
        bg=PRIMARY_BLUE,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#1565C0",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_start.grid(row=8, column=1, sticky="w", padx=10, pady=12)

    btn_stop = tk.Button(
        frame,
        text="停止爬取",
        command=on_stop,
        state=tk.DISABLED,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#2E7D32",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_stop.grid(row=8, column=2, sticky="w", padx=10, pady=12)


def create_update_tab(frame: tk.Frame) -> None:
    """增量爬取界面。"""
    frame.configure(bg=BG_WHITE, highlightbackground=ACCENT_GREEN, highlightthickness=1)

    tk.Label(frame, text="起始网址：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=0, column=0, sticky="e", padx=10, pady=8
    )
    entry_url = tk.Entry(
        frame, width=70, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_url.grid(row=0, column=1, columnspan=3, sticky="w", padx=10, pady=8)

    tk.Label(frame, text="本次最多检查页面数：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=1, column=0, sticky="e", padx=10, pady=8
    )
    entry_max = tk.Entry(
        frame, width=12, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_max.insert(0, "80")
    entry_max.grid(row=1, column=1, sticky="w", padx=10, pady=8)

    # 多线程选项
    multi_thread_var = tk.BooleanVar(value=False)
    chk_multi = tk.Checkbutton(
        frame,
        text="启用多线程（最多 10 个线程）",
        variable=multi_thread_var,
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        anchor="w",
        selectcolor=BG_WHITE,
    )
    chk_multi.grid(row=1, column=2, sticky="w", padx=10, pady=8)

    tk.Label(frame, text="线程数：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=1, column=3, sticky="e", padx=5, pady=8
    )
    spin_threads = tk.Spinbox(
        frame,
        from_=1,
        to=10,
        width=4,
        font=FONT_INPUT,
        bg=INPUT_BG,
        fg=TEXT_DARK,
        insertbackground=TEXT_DARK,
        readonlybackground=INPUT_BG,
    )
    spin_threads.delete(0, tk.END)
    spin_threads.insert(0, "3")
    spin_threads.grid(row=1, column=4, sticky="w", padx=5, pady=8)

    only_chapter_var = tk.BooleanVar(value=False)
    chk_only = tk.Checkbutton(
        frame,
        text="只爬取小说章节标题文章（第1章/第一章/001章/正文1 等）",
        variable=only_chapter_var,
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        anchor="w",
        selectcolor=BG_WHITE,
    )
    chk_only.grid(row=2, column=0, columnspan=4, sticky="w", padx=10, pady=8)

    # 保存目录选择
    tk.Label(frame, text="保存到文件夹：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=3, column=0, sticky="ne", padx=10, pady=8
    )

    folder_mode = tk.StringVar(value="existing")
    radio_existing = tk.Radiobutton(
        frame,
        text="使用已有文件夹",
        variable=folder_mode,
        value="existing",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        selectcolor=BG_WHITE,
    )
    radio_new = tk.Radiobutton(
        frame,
        text="新建文件夹",
        variable=folder_mode,
        value="new",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        selectcolor=BG_WHITE,
    )
    radio_existing.grid(row=3, column=1, sticky="w", padx=10, pady=4)
    radio_new.grid(row=4, column=1, sticky="w", padx=10, pady=4)

    existing_folders = list_article_subfolders()
    combo_existing = ttk.Combobox(
        frame,
        values=existing_folders,
        state="readonly",
        width=32,
        font=FONT_INPUT,
        style="White.TCombobox",
    )
    if existing_folders:
        combo_existing.current(0)
    combo_existing.grid(row=3, column=2, sticky="w", padx=10, pady=4)

    # 刷新文件夹列表按钮
    def refresh_folders() -> None:
        nonlocal existing_folders
        existing_folders = list_article_subfolders()
        combo_existing["values"] = existing_folders
        if existing_folders:
            combo_existing.current(0)

    btn_refresh = tk.Button(
        frame,
        text="刷新",
        command=refresh_folders,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        relief="ridge",
        padx=8,
        pady=2,
    )
    btn_refresh.grid(row=3, column=3, sticky="w", padx=5, pady=4)

    entry_new_folder = tk.Entry(
        frame, width=32, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_new_folder.insert(0, "文件夹名称_增量批次")
    entry_new_folder.grid(row=4, column=2, sticky="w", padx=10, pady=4)

    # 日志窗口
    tk.Label(frame, text="运行日志：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=5, column=0, sticky="ne", padx=10, pady=8
    )
    txt_log = tk.Text(frame, width=82, height=18, font=FONT_LOG, bg=SECTION_BG, fg=TEXT_DARK, relief="solid", bd=1)
    txt_log.grid(row=5, column=1, columnspan=3, sticky="w", padx=10, pady=8)
    scroll = tk.Scrollbar(frame, command=txt_log.yview)
    scroll.grid(row=5, column=4, sticky="ns", pady=8)
    txt_log["yscrollcommand"] = scroll.set

    # 进度条和统计信息
    progress_bar = ttk.Progressbar(
        frame,
        orient="horizontal",
        mode="determinate",
        length=420,
        style="Blue.Horizontal.TProgressbar",
    )
    progress_bar.grid(row=6, column=1, columnspan=3, sticky="w", padx=10, pady=4)

    stats_label = tk.Label(
        frame,
        text="已访问：0/0    新下载：0    跳过：0",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        anchor="w",
    )
    stats_label.grid(row=7, column=1, columnspan=3, sticky="w", padx=10, pady=4)

    stop_event = threading.Event()
    crawl_thread: Optional[threading.Thread] = None

    def on_start() -> None:
        url = entry_url.get().strip()
        if not url:
            messagebox.showwarning("提示", "请先输入起始网址。")
            return
        max_txt = entry_max.get().strip() or "80"
        try:
            max_pages = int(max_txt)
        except ValueError:
            messagebox.showwarning("提示", "最多页面数必须是整数。")
            return

        # 线程数量
        if multi_thread_var.get():
            threads_txt = spin_threads.get().strip() or "3"
            try:
                worker_count = int(threads_txt)
            except ValueError:
                messagebox.showwarning("提示", "线程数必须是 1~10 之间的整数。")
                return
            if worker_count < 1 or worker_count > 10:
                messagebox.showwarning("提示", "线程数必须在 1 到 10 之间。")
                return
        else:
            worker_count = 1

        if folder_mode.get() == "existing" and existing_folders:
            sub_folder = combo_existing.get().strip() or "增量批次"
        else:
            sub_folder = entry_new_folder.get().strip() or "增量批次"

        txt_log.delete("1.0", tk.END)
        stop_event.clear()

        # 重置进度条和统计
        progress_bar["value"] = 0
        stats_label.config(text=f"已访问：0/{max_pages}    新下载：0    跳过：0")

        def worker() -> None:
            run_crawl_with_ui(
                start_url=url,
                max_pages=max_pages,
                only_chapter=only_chapter_var.get(),
                sub_folder=sub_folder,
                log_widget=txt_log,
                stop_event=stop_event,
                progress_bar=progress_bar,
                stats_label=stats_label,
                worker_count=worker_count,
                force_full=False,
            )
            btn_start.config(state=tk.NORMAL)
            btn_stop.config(state=tk.DISABLED)

        nonlocal crawl_thread
        crawl_thread = threading.Thread(target=worker, daemon=True)
        crawl_thread.start()
        btn_start.config(state=tk.DISABLED)
        btn_stop.config(state=tk.DISABLED if not stop_event else tk.NORMAL)

    def on_stop() -> None:
        if stop_event and not stop_event.is_set():
            stop_event.set()
            txt_log.insert(tk.END, "收到停止指令，正在尝试停止爬取...\n")
            txt_log.see(tk.END)

    btn_start = tk.Button(
        frame,
        text="开始增量爬取",
        command=on_start,
        bg=PRIMARY_BLUE,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#1565C0",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_start.grid(row=8, column=1, sticky="w", padx=10, pady=12)

    btn_stop = tk.Button(
        frame,
        text="停止爬取",
        command=on_stop,
        state=tk.DISABLED,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#2E7D32",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_stop.grid(row=8, column=2, sticky="w", padx=10, pady=12)


def create_analysis_tab(frame: tk.Frame) -> None:
    """小说分析界面：高频词分析 & 角色出场分析。"""
    frame.configure(bg=BG_WHITE, highlightbackground=PRIMARY_BLUE, highlightthickness=1)

    # 选择文件夹
    tk.Label(frame, text="选择小说文件夹：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=0, column=0, sticky="e", padx=10, pady=8
    )

    existing_folders = list_article_subfolders()
    combo_folder = ttk.Combobox(
        frame,
        values=existing_folders,
        state="readonly",
        width=32,
        font=FONT_INPUT,
        style="White.TCombobox",
    )
    if existing_folders:
        combo_folder.current(0)
    combo_folder.grid(row=0, column=1, sticky="w", padx=10, pady=8)

    def refresh_folders() -> None:
        nonlocal existing_folders
        existing_folders = list_article_subfolders()
        combo_folder["values"] = existing_folders
        if existing_folders:
            combo_folder.current(0)

    btn_refresh = tk.Button(
        frame,
        text="刷新",
        command=refresh_folders,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        relief="ridge",
        padx=8,
        pady=2,
    )
    btn_refresh.grid(row=0, column=2, sticky="w", padx=5, pady=8)

    # 分析类型
    tk.Label(frame, text="分析类型：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=1, column=0, sticky="e", padx=10, pady=8
    )

    mode_var = tk.StringVar(value="freq")
    radio_freq = tk.Radiobutton(
        frame,
        text="高频词分析",
        variable=mode_var,
        value="freq",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        selectcolor=BG_WHITE,
    )
    radio_roles = tk.Radiobutton(
        frame,
        text="角色出场分析",
        variable=mode_var,
        value="roles",
        bg=BG_WHITE,
        fg=TEXT_DARK,
        font=FONT_LABEL,
        selectcolor=BG_WHITE,
    )
    radio_freq.grid(row=1, column=1, sticky="w", padx=10, pady=4)
    radio_roles.grid(row=1, column=2, sticky="w", padx=10, pady=4)

    # 高频词参数
    label_topn = tk.Label(frame, text="高频词数量：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL)
    label_topn.grid(row=2, column=0, sticky="e", padx=10, pady=8)
    entry_topn = tk.Entry(
        frame, width=8, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_topn.insert(0, "10")
    entry_topn.grid(row=2, column=1, sticky="w", padx=10, pady=8)

    # 角色输入
    label_roles = tk.Label(frame, text="角色姓名（空格/逗号分隔）：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL)
    label_roles.grid(row=3, column=0, sticky="e", padx=10, pady=8)
    entry_roles = tk.Entry(
        frame, width=40, font=FONT_INPUT, bg=INPUT_BG, fg=TEXT_DARK, insertbackground=TEXT_DARK
    )
    entry_roles.insert(0, "顾白, 子车书白")
    entry_roles.grid(row=3, column=1, columnspan=2, sticky="w", padx=10, pady=8)

    def update_mode_visibility(*_: str) -> None:
        mode = mode_var.get()
        if mode == "freq":
            # 显示高频词参数，隐藏角色输入
            label_topn.grid()
            entry_topn.grid()
            label_roles.grid_remove()
            entry_roles.grid_remove()
        else:
            # 显示角色输入，隐藏高频词数量
            label_roles.grid()
            entry_roles.grid()
            label_topn.grid_remove()
            entry_topn.grid_remove()

    mode_var.trace_add("write", update_mode_visibility)
    # 初始化一次显示状态
    update_mode_visibility()

    # 输出按钮
    tk.Label(frame, text="输出方式：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=4, column=0, sticky="ne", padx=10, pady=8
    )

    # 结果显示区域：上方文本统计表，下方图像区域
    tk.Label(frame, text="分析结果：", bg=BG_WHITE, fg=TEXT_DARK, font=FONT_LABEL).grid(
        row=5, column=0, sticky="ne", padx=10, pady=8
    )
    result_frame = tk.Frame(frame, bg=BG_WHITE)
    result_frame.grid(row=5, column=1, columnspan=3, sticky="nsew", padx=10, pady=8)
    frame.grid_rowconfigure(5, weight=1)
    frame.grid_columnconfigure(1, weight=1)

    # 文本统计表
    txt_result = tk.Text(result_frame, width=82, height=10, font=FONT_LOG, bg=SECTION_BG, fg=TEXT_DARK, relief="solid", bd=1)
    txt_result.grid(row=0, column=0, columnspan=3, sticky="nsew", padx=0, pady=(0, 6))
    scroll = tk.Scrollbar(result_frame, command=txt_result.yview)
    scroll.grid(row=0, column=3, sticky="ns", pady=(0, 6))
    txt_result["yscrollcommand"] = scroll.set

    # 图像显示区域（柱状图 / 词云）
    plot_frame = tk.Frame(result_frame, bg=BG_WHITE, relief="groove", bd=1, height=220)
    plot_frame.grid(row=1, column=0, columnspan=4, sticky="nsew", padx=0, pady=(0, 0))
    result_frame.grid_rowconfigure(1, weight=1)
    result_frame.grid_columnconfigure(0, weight=1)
    current_canvas: dict[str, Optional[FigureCanvasTkAgg]] = {"canvas": None}

    def _analyze_internal() -> tuple[list[tuple[str, int]], str] | None:
        """执行一次分析，返回 (频次列表, 标题)。出错则弹窗并返回 None。"""
        folder = combo_folder.get().strip()
        if not folder:
            messagebox.showwarning("提示", "请先选择一个小说文件夹。")
            return None

        mode = mode_var.get()
        txt_result.delete("1.0", tk.END)

        if mode == "freq":
            topn_txt = entry_topn.get().strip() or "10"
            try:
                topn = int(topn_txt)
            except ValueError:
                messagebox.showwarning("提示", "高频词数量必须是整数。")
                return None
            if topn <= 0:
                messagebox.showwarning("提示", "高频词数量必须大于 0。")
                return None

            freqs = analyze_high_freq_words(folder, top_n=topn)
            if not freqs:
                messagebox.showinfo("提示", "未能从该文件夹中读取到有效的文本内容。")
                return None

            title = f"{folder} - 高频词前 {topn} 名"
            return freqs, title

            # 角色分析在下面处理
        else:
            roles_raw = entry_roles.get().strip()
            if not roles_raw:
                messagebox.showwarning("提示", "请先输入要统计的角色姓名。")
                return None
            # 按逗号、中文顿号、空格拆分
            for ch in [",", "，", "、"]:
                roles_raw = roles_raw.replace(ch, " ")
            roles = [r for r in roles_raw.split() if r.strip()]
            if not roles:
                messagebox.showwarning("提示", "未解析到有效的角色姓名，请检查输入格式。")
                return None

            freqs = analyze_roles_frequency(folder, roles)
            title = f"{folder} - 角色出场频次"
            return freqs, title

    def on_show_table() -> None:
        try:
            result = _analyze_internal()
            if result is None:
                return
            freqs, _ = result
            mode = mode_var.get()
            if mode == "freq":
                txt_result.insert(tk.END, "【高频词分析】结果：\n\n")
            else:
                txt_result.insert(tk.END, "【角色出场分析】结果：\n\n")
            for w, c in freqs:
                txt_result.insert(tk.END, f"{w}\t{c}\n")
        except Exception as e:  # noqa: BLE001
            messagebox.showerror("错误", f"分析过程中出现错误：{e}")

    def on_show_bar() -> None:
        try:
            result = _analyze_internal()
            if result is None:
                return
            freqs, title = result
            # 清理旧图像
            if current_canvas["canvas"] is not None:
                current_canvas["canvas"].get_tk_widget().destroy()
                current_canvas["canvas"] = None
            fig = build_bar_figure(freqs, title)
            canvas = FigureCanvasTkAgg(fig, master=plot_frame)
            canvas.draw()
            canvas.get_tk_widget().pack(fill="both", expand=True)
            current_canvas["canvas"] = canvas
        except Exception as e:  # noqa: BLE001
            messagebox.showerror("错误", f"绘制柱状图时出错：{e}")

    def on_show_wc() -> None:
        try:
            result = _analyze_internal()
            if result is None:
                return
            freqs, title = result
            if current_canvas["canvas"] is not None:
                current_canvas["canvas"].get_tk_widget().destroy()
                current_canvas["canvas"] = None
            fig = build_wordcloud_figure(freqs, title)
            canvas = FigureCanvasTkAgg(fig, master=plot_frame)
            canvas.draw()
            canvas.get_tk_widget().pack(fill="both", expand=True)
            current_canvas["canvas"] = canvas
        except Exception as e:  # noqa: BLE001
            messagebox.showerror("错误", f"生成词云时出错：{e}")

    # 输出方式按钮区域：三个按钮放在同一个小框中，使“柱状图”位于中间，整体更居中美观
    btn_frame = tk.Frame(frame, bg=BG_WHITE)
    btn_frame.grid(row=4, column=1, columnspan=3, pady=4, sticky="w")

    btn_table = tk.Button(
        btn_frame,
        text="统计表",
        command=on_show_table,
        bg=PRIMARY_BLUE,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#1565C0",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_table.pack(side="left", padx=12)

    btn_bar = tk.Button(
        btn_frame,
        text="柱状图",
        command=on_show_bar,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#2E7D32",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_bar.pack(side="left", padx=12)

    btn_wc = tk.Button(
        btn_frame,
        text="词云",
        command=on_show_wc,
        bg="#29B6F6",
        fg="white",
        font=FONT_BUTTON,
        activebackground="#039BE5",
        relief="ridge",
        padx=10,
        pady=4,
    )
    btn_wc.pack(side="left", padx=12)


def main() -> None:
    root = tk.Tk()
    root.title("小说网站爬虫（Python 大作业）")
    # 底层背景使用主色蓝：让白色界面“浮”在蓝色底层之上
    root.configure(bg=PRIMARY_BLUE)

    style = ttk.Style()
    try:
        style.theme_use("clam")
    except Exception:
        pass
    # Notebook 顶部标签栏（你圈出的那块空白）背景色：改成与边框一致的蓝色
    style.configure("TNotebook", background=PRIMARY_BLUE, borderwidth=0)
    # 标签页按钮（“主界面/全量爬取/增量爬取/小说分析”）改成白色底
    style.configure(
        "TNotebook.Tab",
        padding=(16, 8),
        font=FONT_LABEL,
        background=INPUT_BG,
        foreground=TEXT_DARK,
    )
    # 统一各状态的背景为白色（避免主题自带的灰色/渐变）
    style.map(
        "TNotebook.Tab",
        background=[
            ("selected", INPUT_BG),
            ("active", INPUT_BG),
            ("!disabled", INPUT_BG),
        ],
        foreground=[
            ("selected", TEXT_DARK),
            ("!disabled", TEXT_DARK),
        ],
    )
    # 选择组件（下拉框）统一白底
    style.configure(
        "White.TCombobox",
        fieldbackground=INPUT_BG,
        background=INPUT_BG,
        foreground=TEXT_DARK,
    )
    style.map(
        "White.TCombobox",
        fieldbackground=[("readonly", INPUT_BG), ("!disabled", INPUT_BG)],
        background=[("readonly", INPUT_BG), ("!disabled", INPUT_BG)],
        foreground=[("!disabled", TEXT_DARK)],
    )
    # 进度条样式：蓝色填充
    style.configure(
        "Blue.Horizontal.TProgressbar",
        troughcolor=SECTION_BG,
        bordercolor=PRIMARY_BLUE,
        background=PRIMARY_BLUE,
        lightcolor=PRIMARY_BLUE,
        darkcolor=PRIMARY_BLUE,
    )

    # 界面外围“粗蓝边框”：用蓝色底层 + 白色内容卡片的方式实现
    outer = tk.Frame(root, bg=PRIMARY_BLUE)
    outer.pack(fill="both", expand=True)

    # 粗边框厚度（蓝色可见区域宽度）
    border_pad = 18
    card = tk.Frame(outer, bg=BG_WHITE, highlightbackground=PRIMARY_BLUE, highlightthickness=1)
    card.pack(fill="both", expand=True, padx=border_pad, pady=border_pad)

    notebook = ttk.Notebook(card)
    notebook.pack(fill="both", expand=True)

    frame_main = tk.Frame(notebook, bg=BG_WHITE)
    frame_full = tk.Frame(notebook, bg=BG_WHITE)
    frame_update = tk.Frame(notebook, bg=BG_WHITE)
    frame_analysis = tk.Frame(notebook, bg=BG_WHITE)

    notebook.add(frame_main, text="主界面")
    notebook.add(frame_full, text="全量爬取")
    notebook.add(frame_update, text="增量爬取")
    notebook.add(frame_analysis, text="小说分析")

    # 主界面
    lbl = tk.Label(
        frame_main,
        text="Python 网络爬虫大作业\n请选择要执行的操作：",
        font=FONT_TITLE,
        justify="center",
        bg=BG_WHITE,
        fg=PRIMARY_BLUE,
    )
    lbl.pack(pady=30)

    # 简要功能说明
    desc = tk.Label(
        frame_main,
        text="全量爬取：第一次使用，从起始网址出发批量获取站内所有文章/章节。\n"
        "增量爬取：之后使用，只检查并下载相对于上次新增的文章/章节。",
        font=FONT_LABEL,
        justify="center",
        bg=BG_WHITE,
        fg=TEXT_DARK,
    )
    desc.pack(pady=5)

    def go_full() -> None:
        notebook.select(frame_full)

    def go_update() -> None:
        notebook.select(frame_update)

    def go_analysis() -> None:
        notebook.select(frame_analysis)

    btn_full = tk.Button(
        frame_main,
        text="全量爬取（第一次爬取）",
        width=30,
        command=go_full,
        bg=PRIMARY_BLUE,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#1565C0",
        relief="ridge",
        padx=10,
        pady=6,
    )
    btn_full.pack(pady=10)

    btn_update = tk.Button(
        frame_main,
        text="增量爬取（之后只爬新章节）",
        width=30,
        command=go_update,
        bg=ACCENT_GREEN,
        fg="white",
        font=FONT_BUTTON,
        activebackground="#2E7D32",
        relief="ridge",
        padx=10,
        pady=6,
    )
    btn_update.pack(pady=10)

    btn_analysis = tk.Button(
        frame_main,
        text="小说分析（高频词与角色出场）",
        width=30,
        command=go_analysis,
        bg="#29B6F6",
        fg="white",
        font=FONT_BUTTON,
        activebackground="#039BE5",
        relief="ridge",
        padx=10,
        pady=6,
    )
    btn_analysis.pack(pady=10)

    create_full_tab(frame_full)
    create_update_tab(frame_update)
    create_analysis_tab(frame_analysis)

    root.mainloop()


if __name__ == "__main__":
    main()


