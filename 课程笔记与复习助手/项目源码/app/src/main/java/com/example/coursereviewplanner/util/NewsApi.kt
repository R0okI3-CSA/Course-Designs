package com.example.coursereviewplanner.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class NewsArticle(
    val title: String,
    val url: String?
)

/**
 * 使用新闻数据接口拉取当天新闻列表。
 *
 * 这里以 NewsData.io 为示例，你需要到官网申请一个免费的 API Key，
 * 然后把 key 传进来。接口返回结构大致为：
 * {
 *   "results": [
 *     { "title": "...", "link": "..." },
 *     ...
 *   ]
 * }
 */
suspend fun fetchTodayNewsFromApi(
    apiKey: String,
    country: String = "cn",
    language: String = "zh"
): List<NewsArticle> = withContext(Dispatchers.IO) {
    if (apiKey.isBlank()) return@withContext emptyList()
    val urlStr =
        "https://newsdata.io/api/1/news?apikey=$apiKey&country=$country&language=$language"
    val url = URL(urlStr)
    val conn = (url.openConnection() as HttpURLConnection).apply {
        connectTimeout = 5000
        readTimeout = 5000
        requestMethod = "GET"
    }
    try {
        val body = conn.inputStream.bufferedReader().use { it.readText() }
        val root = JSONObject(body)
        val results: JSONArray = root.optJSONArray("results") ?: JSONArray()
        val list = mutableListOf<NewsArticle>()
        for (i in 0 until results.length()) {
            val obj = results.optJSONObject(i) ?: continue
            val title = obj.optString("title").trim()
            if (title.isBlank()) continue
            val link = obj.optString("link", null)
            list.add(NewsArticle(title = title, url = link))
        }
        list
    } catch (_: Exception) {
        emptyList()
    } finally {
        conn.disconnect()
    }
}


