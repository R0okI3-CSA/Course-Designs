"""
小说文本分析模块：
- 从“文章/子文件夹”中读取所有章节文本
- 高频词统计与词云生成
- 角色出场统计与可视化
"""

import os
from collections import Counter
from typing import List, Tuple

import jieba
from wordcloud import WordCloud
import matplotlib.pyplot as plt
from matplotlib.figure import Figure
import numpy as np
import docx

from config import ARTICLE_DIR


# 配置中文字体（用于 Matplotlib 和 WordCloud）
FONT_CANDIDATES = [
    "msyh.ttc",  # Windows 微软雅黑
    "SimHei.ttf",
    "SimHei",
]


def _get_font_path() -> str | None:
    for path in FONT_CANDIDATES:
        if os.path.isfile(path):
            return path
        # 尝试 Windows 字体目录
        win_font = os.path.join("C:\\Windows\\Fonts", path)
        if os.path.isfile(win_font):
            return win_font
    return None


ZH_FONT_PATH = _get_font_path()

if ZH_FONT_PATH:
    plt.rcParams["font.sans-serif"] = ["SimHei"]
    plt.rcParams["axes.unicode_minus"] = False


DEFAULT_STOPWORDS = {
    "的",
    "了",
    "和",
    "是",
    "在",
    "就",
    "也",
    "都",
    "又",
    "与",
    "及",
    "着",
    "而",
    "但",
    "你",
    "我",
    "他",
    "她",
    "它",
    "我们",
    "他们",
    "她们",
    "这个",
    "那个",
    "这些",
    "那些",
    "一个",
    "不会",
    "没有",
    "这样",
    "已经",
    "只是",
    "然后",
    "因为",
    "所以",
    "如果",
    "还有",
    "第",
    "章",
}


def _read_all_texts_from_folder(sub_folder: str) -> str:
    """读取指定子文件夹下所有 .docx 和 .txt 文件的文本内容。"""
    target_dir = os.path.join(ARTICLE_DIR, sub_folder)
    if not os.path.isdir(target_dir):
        raise FileNotFoundError(f"未找到要分析的文件夹：{target_dir}")

    texts: list[str] = []
    for root, _, files in os.walk(target_dir):
        for name in files:
            path = os.path.join(root, name)
            if name.lower().endswith(".txt"):
                try:
                    with open(path, "r", encoding="utf-8", errors="ignore") as f:
                        texts.append(f.read())
                except Exception:
                    continue
            elif name.lower().endswith(".docx"):
                try:
                    doc = docx.Document(path)
                    content = "\n".join(p.text for p in doc.paragraphs)
                    texts.append(content)
                except Exception:
                    continue
    return "\n".join(texts)


def analyze_high_freq_words(
    sub_folder: str,
    top_n: int = 10,
    min_len: int = 2,
) -> List[Tuple[str, int]]:
    """对指定子文件夹中的小说文本进行高频词统计，返回前 top_n 个词及其频次。"""
    text = _read_all_texts_from_folder(sub_folder)
    if not text.strip():
        return []

    # 兼容环境中 jieba 版本可能不支持 lcut 的情况，统一使用 cut 转列表
    words = list(jieba.cut(text))
    counter: Counter[str] = Counter()
    for w in words:
        w = w.strip()
        if len(w) < min_len:
            continue
        if w in DEFAULT_STOPWORDS:
            continue
        counter[w] += 1

    return counter.most_common(top_n)


def analyze_roles_frequency(sub_folder: str, roles: List[str]) -> List[Tuple[str, int]]:
    """统计指定角色在子文件夹小说中的总出现次数。"""
    text = _read_all_texts_from_folder(sub_folder)
    if not text.strip():
        return []

    result: list[tuple[str, int]] = []
    for name in roles:
        name = name.strip()
        if not name:
            continue
        count = text.count(name)
        result.append((name, count))
    return result


def show_bar_chart(freqs: List[Tuple[str, int]], title: str) -> None:
    """根据 (词, 频次) 列表绘制柱状图。"""
    if not freqs:
        return
    labels = [w for w, _ in freqs]
    values = [c for _, c in freqs]

    plt.figure(figsize=(8, 5))
    plt.bar(range(len(labels)), values, color="#42A5F5")
    plt.xticks(range(len(labels)), labels, rotation=30, ha="right")
    plt.ylabel("出现次数")
    plt.title(title)
    plt.tight_layout()
    plt.show()


def show_wordcloud(freqs: List[Tuple[str, int]], title: str) -> None:
    """根据 (词, 频次) 列表生成圆形词云。"""
    if not freqs:
        return

    freq_dict = {w: c for w, c in freqs}

    width, height = 800, 800
    # 创建圆形蒙版：圆内为 0（可绘制），圆外为 255（背景）
    x, y = np.ogrid[:height, :width]
    center_x, center_y = int(height / 2), int(width / 2)
    radius = min(center_x, center_y) - 10
    mask = (x - center_x) ** 2 + (y - center_y) ** 2 > radius**2
    circle = np.zeros((height, width), dtype=np.uint8)
    circle[mask] = 255

    font_path = ZH_FONT_PATH or "msyh.ttc"

    wc = WordCloud(
        font_path=font_path,
        background_color="white",
        width=width,
        height=height,
        mask=circle,
        colormap="viridis",
    ).generate_from_frequencies(freq_dict)

    plt.figure(figsize=(6, 6))
    plt.imshow(wc, interpolation="bilinear")
    plt.axis("off")
    plt.title(title)
    plt.tight_layout()
    plt.show()


def build_bar_figure(freqs: List[Tuple[str, int]], title: str) -> Figure:
    """构建柱状图 Figure，用于在 Tkinter 中嵌入显示。"""
    fig = Figure(figsize=(4, 3), dpi=100)
    ax = fig.add_subplot(111)
    if not freqs:
        ax.text(0.5, 0.5, "暂无数据", ha="center", va="center")
        return fig

    labels = [w for w, _ in freqs]
    values = [c for _, c in freqs]
    ax.bar(range(len(labels)), values, color="#42A5F5")
    ax.set_xticks(range(len(labels)))
    if ZH_FONT_PATH:
        for label in ax.get_xticklabels():
            label.set_fontfamily("SimHei")
        ax.set_xticklabels(labels, rotation=30, ha="right")
        ax.set_ylabel("出现次数", fontfamily="SimHei")
        ax.set_title(title, fontfamily="SimHei")
    else:
        ax.set_xticklabels(labels, rotation=30, ha="right")
        ax.set_ylabel("Count")
        ax.set_title(title)
    fig.tight_layout()
    return fig


def build_wordcloud_figure(freqs: List[Tuple[str, int]], title: str) -> Figure:
    """构建圆形词云 Figure，用于在 Tkinter 中嵌入显示。"""
    fig = Figure(figsize=(4, 4), dpi=100)
    ax = fig.add_subplot(111)
    ax.axis("off")
    if ZH_FONT_PATH:
        ax.set_title(title, fontfamily="SimHei")
    else:
        ax.set_title(title)

    if not freqs:
        ax.text(0.5, 0.5, "暂无数据", ha="center", va="center")
        return fig

    # freqs 是 (词, 频次) 的列表，这里要解包为 w, c
    freq_dict = {w: c for w, c in freqs}

    width, height = 400, 400
    # 创建圆形蒙版
    x, y = np.ogrid[:height, :width]
    center_x, center_y = height // 2, width // 2
    radius = min(center_x, center_y) - 10
    mask = (x - center_x) ** 2 + (y - center_y) ** 2 > radius**2
    circle = np.zeros((height, width), dtype=np.uint8)
    circle[mask] = 255

    font_path = ZH_FONT_PATH or "msyh.ttc"

    wc = WordCloud(
        font_path=font_path,
        background_color="white",
        width=width,
        height=height,
        mask=circle,
        colormap="viridis",
    ).generate_from_frequencies(freq_dict)

    ax.imshow(wc, interpolation="bilinear")
    fig.tight_layout()
    return fig


