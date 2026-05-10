"""
配置文件：存放目标网站、接口参数以及本地保存路径等信息
后续若更换网站或调整保存路径，只需要修改本文件即可。
"""

import os

# 目标网站相关配置（示例基于参考资料中的 bicpa 站点接口）
BASE_URL = "http://www.bicpa.org.cn"
ARTICLE_API = f"{BASE_URL}/dtzj/zxgg/getArticles.action"

# 接口公共参数（根据目标网站实际情况可调整）
COMMON_FORM_DATA = {
    "_q": "Article.list",
    "siteId": "7e0b3b27-2622-4aa7-b6f8-abfe5c5df922",
    "catalogId": "34f92da3-d6d0-4e96-899f-d7f581c18162",
    "pub": "true",
}

# 第一次全量爬取时的 limit（历史文章总数足够大即可）
FULL_CRAWL_LIMIT = 10000

# 之后增量更新时，每次只检查最近的若干篇文章
UPDATE_CRAWL_LIMIT = 150

# User-Agent 列表，用于随机选择，降低被封概率
USER_AGENT_LIST = [
    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/67.0.3396.99 Safari/537.36",
    "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36",
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/62.0.3202.62 Safari/537.36",
    "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 "
    "(KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36",
    "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)",
    "Mozilla/5.0 (Macintosh; U; PPC Mac OS X 10.5; en-US; "
    "rv:1.9.2.15) Gecko/20110303 Firefox/3.6.15",
]

# 本地保存目录（默认在当前项目下的“文章”文件夹）
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
ARTICLE_DIR = os.path.join(BASE_DIR, "文章")

# 链接数据库文件（记录所有已处理过的文章链接）
LINKS_DB_FILE = os.path.join(BASE_DIR, "links.txt")

# 关键词过滤（例如只下载标题中含有该关键词的文章；为空字符串表示不过滤）
TITLE_KEYWORD = "委员会专家提示"


