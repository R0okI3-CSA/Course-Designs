@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursereviewplanner.ui.studyplan.CellContent
import com.example.coursereviewplanner.ui.studyplan.ScheduleMode
import com.example.coursereviewplanner.ui.studyplan.StudyPlanDetailUiState
import com.example.coursereviewplanner.ui.studyplan.StudyPlanDetailViewModel
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
private const val SERVER_BASE_URL_FOR_TIMETABLE = "http://192.168.43.201:3000"

class StudyPlanDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val planId = intent.getLongExtra("planId", -1L)
        val planTitle = intent.getStringExtra("planTitle") ?: "固定时间段计划"
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: StudyPlanDetailViewModel = viewModel()
                LaunchedEffect(planId) {
                    vm.initialize(planId)
                }
                StudyPlanDetailScreen(
                    viewModel = vm,
                    title = planTitle,
                    planId = planId
                )
            }
        }
    }
}

private val timeSlots = listOf(
    "8:00\n8:45",
    "8:55\n9:40",
    "10:00\n10:45",
    "10:55\n11:40",
    "14:00\n14:45",
    "14:50\n15:35",
    "15:55\n16:40",
    "16:50\n17:35",
    "19:00\n19:45",
    "19:55\n20:40",
    "20:50\n21:35",
    "21:45\n22:30"
)

private val weekdays = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

@Composable
fun StudyPlanDetailScreen(
    viewModel: StudyPlanDetailViewModel,
    title: String,
    planId: Long
) {
    val state by viewModel.uiState
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var showShareDialog by remember { mutableStateOf(false) }
    var shareLink by remember { mutableStateOf("") }
    var shareError by remember { mutableStateOf<String?>(null) }
    var isGeneratingLink by remember { mutableStateOf(false) }
    var importLinkText by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    if (state.isMultiSelectMode) {
                        TextButton(onClick = { viewModel.onOpenMultiEditForSelected() }) {
                            Text(text = "完成")
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(8.dp)
        ) {
            // 模式切换（课程表 / 自定义）——放在顶部，统一两个模式切换的位置
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                ModeChip(
                    text = "课程表模式",
                    selected = state.mode == ScheduleMode.TIMETABLE,
                    onClick = { viewModel.onModeChange(ScheduleMode.TIMETABLE) }
                )
                Spacer(modifier = Modifier.width(8.dp))
                ModeChip(
                    text = "自定义模式",
                    selected = state.mode == ScheduleMode.CUSTOM,
                    onClick = { viewModel.onModeChange(ScheduleMode.CUSTOM) }
                )
            }

            // 学习压力分析按钮（两种模式下通用）
            androidx.compose.material3.Button(
                onClick = {
                    // 若当前为自定义模式，则将当前表格的日期范围一并传给分析界面
                    val customStart = if (state.mode == ScheduleMode.CUSTOM) state.customStartDay else null
                    val customEnd = if (state.mode == ScheduleMode.CUSTOM) state.customEndDay else null
                    context.startActivity(
                        Intent(context, StudyLoadAnalysisActivity::class.java).apply {
                            putExtra("planId", planId)
                            putExtra("planTitle", title)
                            customStart?.let { putExtra("customStartDay", it) }
                            customEnd?.let { putExtra("customEndDay", it) }
                        }
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(text = "学习压力分析")
            }

            // 仅在课程表模式下显示“分享课表链接”按钮
            if (state.mode == ScheduleMode.TIMETABLE) {
                Spacer(modifier = Modifier.height(4.dp))
                androidx.compose.material3.Button(
                    onClick = {
                        showShareDialog = true
                        shareLink = ""
                        shareError = null
                        importLinkText = ""
                        scope.launch {
                            isGeneratingLink = true
                            val (url, error) =
                                generateTimetableShareLinkForCurrentPlan(
                                    planTitle = title,
                                    state = state
                                )
                            isGeneratingLink = false
                            if (url != null) {
                                shareLink = url
                            } else {
                                shareError = error ?: "生成链接失败，请稍后重试"
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Text(text = "分享课程表链接")
                }
            }

            if (state.mode == ScheduleMode.CUSTOM) {
                CustomScheduleSection(viewModel = viewModel, title = title)
            } else {
                TimetableGrid(
                    state = state,
                    onCellClick = { r, c -> viewModel.onCellClick(r, c) }
                )
            }
        }
    }

    if (state.showCellActionDialog) {
        val key = state.actionCell
        val currentContent = key?.let { k -> state.cells[k] } ?: CellContent()
        AlertDialog(
            onDismissRequest = { viewModel.onCellActionDialogDismiss() },
            title = { Text(text = "选择操作") },
            text = {
                Column {
                    Text(
                        text = "当前课程信息：",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "课程名称：${currentContent.title.ifBlank { "（空）" }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "地点：${currentContent.location.ifBlank { "（空）" }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "内容：${currentContent.content.ifBlank { "（空）" }}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "请选择对该时间段的操作：",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { viewModel.onEditSingleCell() }) {
                        Text("编辑")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = { viewModel.onClearSingleCell() }) {
                        Text("清空")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    TextButton(onClick = { viewModel.onEnterMultiSelectMode() }) {
                        Text("多选")
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    val context = LocalContext.current
                    TextButton(
                        onClick = {
                            scope.launch {
                                val reminderId = viewModel.createReminderFromCurrentCell()
                                viewModel.onCellActionDialogDismiss()
                                if (reminderId > 0) {
                                    context.startActivity(
                                        Intent(
                                            context,
                                            ReviewReminderDetailActivity::class.java
                                        ).putExtra("reminderId", reminderId)
                                    )
                                }
                            }
                        }
                    ) {
                        Text("导入提醒")
                    }
                }
            },
            dismissButton = {}
        )
    }

    if (state.showEditDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEditDialogDismiss() },
            title = { Text(text = "编辑课程信息") },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.editTitle,
                        onValueChange = { viewModel.onEditFieldsChange(title = it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("课程名称") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.editLocation,
                        onValueChange = { viewModel.onEditFieldsChange(location = it) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("地点") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.editContent,
                        onValueChange = { viewModel.onEditFieldsChange(content = it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("内容") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val single = !state.isMultiSelectMode
                        viewModel.onConfirmEdit(singleCellOnly = single)
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEditDialogDismiss() }) {
                    Text("取消")
                }
            }
        )
    }

    // 分享 / 导入课表弹窗
    if (showShareDialog) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text(text = "分享 / 导入课表") },
            text = {
                Column {
                    Text(
                        text = "当前课表分享链接（公开，可复制给同学导入）：",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = if (isGeneratingLink) "正在生成链接…" else shareLink,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        label = { Text("分享链接") }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            enabled = shareLink.isNotBlank() && !isGeneratingLink,
                            onClick = {
                                if (shareLink.isNotBlank()) {
                                    val clip = ClipData.newPlainText("timetable_share_link", shareLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(text = "复制链接")
                        }
                    }
                    if (shareError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = shareError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "从链接导入课表（会覆盖当前课表）：",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = importLinkText,
                        onValueChange = { importLinkText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        label = { Text("粘贴分享链接或分享代码") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val input = importLinkText.trim()
                        if (input.isEmpty()) {
                            Toast.makeText(context, "请输入要导入的链接", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        scope.launch {
                            val (timetableJson, error) = fetchTimetableFromLink(input)
                            if (timetableJson != null) {
                                viewModel.importTimetableFromJson(timetableJson) { success ->
                                    if (success) {
                                        Toast.makeText(context, "导入成功，当前课表已更新", Toast.LENGTH_SHORT)
                                            .show()
                                        showShareDialog = false
                                    } else {
                                        Toast.makeText(context, "导入失败，请稍后重试", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    error ?: "无法从链接获取课表，请检查后重试",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text(text = "确定导入")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text(text = "关闭")
                }
            }
        )
    }
}

/**
 * 根据当前界面状态生成可分享的课表 JSON，并调用自建服务器生成公开链接。
 * 返回：Pair(shareUrl, errorMessage)
 */
private suspend fun generateTimetableShareLinkForCurrentPlan(
    planTitle: String,
    state: StudyPlanDetailUiState
): Pair<String?, String?> = withContext(Dispatchers.IO) {
    try {
        val cells = state.cells
        if (cells.isEmpty()) {
            return@withContext null to "当前课表为空，无法生成链接"
        }

        val cellsArray = JSONArray()
        cells.forEach { (key, content) ->
            if (!content.isEmpty) {
                val (row, col) = key
                val obj = JSONObject()
                obj.put("rowIndex", row)
                obj.put("colIndex", col)
                obj.put("title", content.title)
                obj.put("location", content.location)
                obj.put("content", content.content)
                cellsArray.put(obj)
            }
        }

        if (cellsArray.length() == 0) {
            return@withContext null to "当前课表没有课程，无法生成链接"
        }

        val timetable = JSONObject().apply {
            put("planTitle", planTitle)
            put("cells", cellsArray)
        }
        val root = JSONObject().apply {
            put("timetable", timetable)
        }

        val url = java.net.URL("$SERVER_BASE_URL_FOR_TIMETABLE/api/timetable/share")
        val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        conn.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(root.toString()) }

        val resp = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        conn.disconnect()

        val json = JSONObject(resp)
        if (!json.optBoolean("success", false)) {
            val msg = json.optString("message").ifBlank { "服务器返回失败" }
            return@withContext null to msg
        }

        val shareUrl = json.optString("shareUrl", "")
        if (shareUrl.isBlank()) {
            return@withContext null to "服务器未返回有效链接"
        }
        shareUrl to null
    } catch (e: Exception) {
        null to ("生成链接失败：" + (e.message ?: "未知错误"))
    }
}

/**
 * 从用户输入的链接或分享代码中获取课表 JSON。
 * 返回：Pair(timetableJson, errorMessage)
 */
private suspend fun fetchTimetableFromLink(input: String): Pair<JSONObject?, String?> =
    withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return@withContext null to "链接不能为空"
        }

        return@withContext try {
            val urlStr = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                trimmed
            } else {
                val code = trimmed.substringAfterLast('/').trim()
                if (code.isEmpty()) {
                    return@withContext null to "链接格式不正确"
                }
                "$SERVER_BASE_URL_FOR_TIMETABLE/api/timetable/$code"
            }

            val url = java.net.URL(urlStr)
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }

            val resp = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            conn.disconnect()

            val json = JSONObject(resp)
            if (!json.optBoolean("success", false)) {
                val msg = json.optString("message").ifBlank { "服务器返回失败" }
                null to msg
            } else {
                val timetable = json.optJSONObject("timetable")
                if (timetable == null) {
                    null to "服务器未返回课表数据"
                } else {
                    timetable to null
                }
            }
        } catch (e: Exception) {
            null to ("获取课表失败：" + (e.message ?: "未知错误"))
        }
    }

/**
 * 根据当前自定义计划状态生成可分享的 JSON，并调用自建服务器生成公开链接。
 * 返回：Pair(shareUrl, errorMessage)
 */
private suspend fun generateCustomScheduleShareLinkForCurrentPlan(
    planTitle: String,
    state: StudyPlanDetailUiState
): Pair<String?, String?> = withContext(Dispatchers.IO) {
    try {
        val events = state.customEvents
        if (events.isEmpty()) {
            return@withContext null to "当前自定义计划为空，无法生成链接"
        }

        val startDay = state.customStartDay ?: events.minOf { it.dateEpochDay }
        val endDay = state.customEndDay ?: events.maxOf { it.dateEpochDay }

        val eventsArray = JSONArray()
        events.forEach { ev ->
            val obj = JSONObject()
            obj.put("dateEpochDay", ev.dateEpochDay)
            obj.put("startMinutes", ev.startMinutes)
            obj.put("endMinutes", ev.endMinutes)
            obj.put("title", ev.title)
            obj.put("content", ev.content)
            eventsArray.put(obj)
        }

        val custom = JSONObject().apply {
            put("planTitle", planTitle)
            put("startDayEpoch", startDay)
            put("endDayEpoch", endDay)
            put("events", eventsArray)
        }
        val root = JSONObject().apply {
            put("customSchedule", custom)
        }

        val url = java.net.URL("$SERVER_BASE_URL_FOR_TIMETABLE/api/customSchedule/share")
        val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
            connectTimeout = 5000
            readTimeout = 5000
            requestMethod = "POST"
            doOutput = true
            setRequestProperty("Content-Type", "application/json; charset=utf-8")
        }

        conn.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(root.toString()) }

        val resp = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        conn.disconnect()

        val json = JSONObject(resp)
        if (!json.optBoolean("success", false)) {
            val msg = json.optString("message").ifBlank { "服务器返回失败" }
            return@withContext null to msg
        }

        val shareUrl = json.optString("shareUrl", "")
        if (shareUrl.isBlank()) {
            return@withContext null to "服务器未返回有效链接"
        }
        shareUrl to null
    } catch (e: Exception) {
        null to ("生成链接失败：" + (e.message ?: "未知错误"))
    }
}

/**
 * 从用户输入的链接或分享代码中获取自定义计划 JSON。
 * 返回：Pair(customScheduleJson, errorMessage)
 */
private suspend fun fetchCustomScheduleFromLink(input: String): Pair<JSONObject?, String?> =
    withContext(Dispatchers.IO) {
        val trimmed = input.trim()
        if (trimmed.isEmpty()) {
            return@withContext null to "链接不能为空"
        }

        return@withContext try {
            val urlStr = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
                trimmed
            } else {
                val code = trimmed.substringAfterLast('/').trim()
                if (code.isEmpty()) {
                    return@withContext null to "链接格式不正确"
                }
                "$SERVER_BASE_URL_FOR_TIMETABLE/api/customSchedule/$code"
            }

            val url = java.net.URL(urlStr)
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }

            val resp = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            conn.disconnect()

            val json = JSONObject(resp)
            if (!json.optBoolean("success", false)) {
                val msg = json.optString("message").ifBlank { "服务器返回失败" }
                null to msg
            } else {
                val custom = json.optJSONObject("customSchedule")
                if (custom == null) {
                    null to "服务器未返回自定义计划数据"
                } else {
                    custom to null
                }
            }
        } catch (e: Exception) {
            null to ("获取自定义计划失败：" + (e.message ?: "未知错误"))
        }
    }
@Composable
private fun CustomScheduleSection(
    viewModel: StudyPlanDetailViewModel,
    title: String
) {
    val state by viewModel.uiState
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showShareDialog by remember { mutableStateOf(false) }
    var shareLink by remember { mutableStateOf("") }
    var shareError by remember { mutableStateOf<String?>(null) }
    var isGeneratingLink by remember { mutableStateOf(false) }
    var importLinkText by remember { mutableStateOf("") }
    val hourHeight = 48.dp
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()
    val startDay = state.customStartDay ?: java.time.LocalDate.now().toEpochDay()
    val endDay = state.customEndDay ?: startDay
    val days = generateSequence(startDay) { it + 1 }
        .take((endDay - startDay + 1).toInt().coerceAtLeast(1))
        .toList()

    Column(modifier = Modifier.fillMaxSize()) {
        // 分享自定义计划按钮
        androidx.compose.material3.Button(
            onClick = {
                showShareDialog = true
                shareLink = ""
                shareError = null
                importLinkText = ""
                scope.launch {
                    isGeneratingLink = true
                    val (url, error) =
                        generateCustomScheduleShareLinkForCurrentPlan(
                            planTitle = title,
                            state = state
                        )
                    isGeneratingLink = false
                    if (url != null) {
                        shareLink = url
                    } else {
                        shareError = error ?: "生成链接失败，请稍后重试"
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .padding(bottom = 4.dp)
        ) {
            Text(text = "分享自定义计划链接")
        }

        // 日期范围选择
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val startDate = java.time.LocalDate.ofEpochDay(startDay)
            val endDate = java.time.LocalDate.ofEpochDay(endDay)
            TextButton(onClick = {
                val now = java.time.LocalDate.now()
                val picker = android.app.DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val picked = java.time.LocalDate.of(y, m + 1, d)
                        val newStart = picked.toEpochDay()
                        viewModel.onCustomDateRangeChange(newStart, maxOf(newStart, endDay))
                    },
                    startDate.year,
                    startDate.monthValue - 1,
                    startDate.dayOfMonth
                )
                picker.show()
            }) {
                Text(text = "开始：${startDate}")
            }
            Text("~")
            TextButton(onClick = {
                val picker = android.app.DatePickerDialog(
                    context,
                    { _, y, m, d ->
                        val picked = java.time.LocalDate.of(y, m + 1, d)
                        val newEnd = picked.toEpochDay()
                        viewModel.onCustomDateRangeChange(startDay, maxOf(startDay, newEnd))
                    },
                    endDate.year,
                    endDate.monthValue - 1,
                    endDate.dayOfMonth
                )
                picker.show()
            }) {
                Text(text = "结束：${endDate}")
            }
        }

        // 顶部日期标题行（与时间轴分离，便于 0:00 与表格主体对齐）
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 左侧时间轴占位，让日期标题与右侧列对齐
            Spacer(modifier = Modifier.width(60.dp))
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState),
                horizontalArrangement = Arrangement.Start
            ) {
                days.forEach { dayEpoch ->
                    val date = java.time.LocalDate.ofEpochDay(dayEpoch)
                    Row(
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${date.monthValue}/${date.dayOfMonth}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        TextButton(onClick = {
                            viewModel.onOpenCustomEventDialog(dayEpoch)
                        }) {
                            Text("+")
                        }
                    }
                }
            }
        }

        // 时间轴 + 表格主体
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(verticalScrollState)
        ) {
            // 左侧时间轴
            Column(
                modifier = Modifier
                    .width(60.dp)
            ) {
                for (h in 0..24) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(hourHeight),
                        contentAlignment = Alignment.TopStart
                    ) {
                        Text(text = "${h}:00", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }

            // 右侧日期列（仅表格主体）
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(horizontalScrollState)
            ) {
                days.forEach { dayEpoch ->
                    val eventsForDay = state.customEvents.filter { ev -> ev.dateEpochDay == dayEpoch }
                    Box(
                        modifier = Modifier
                            .width(120.dp)
                            .padding(horizontal = 4.dp)
                            .height(hourHeight * 24)
                            .background(Color(0xFFEFEFF4))
                    ) {
                        // 画事件块
                        val minuteHeight = hourHeight / 60f
                        eventsForDay.forEach { ev ->
                            val top = minuteHeight * ev.startMinutes
                            val height = minuteHeight * (ev.endMinutes - ev.startMinutes)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(height)
                                    .offset(y = top)
                                    .background(
                                        color = Color(0xFFBBDEFB),
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                                    )
                                    .clickable {
                                        viewModel.onOpenCustomEventDialog(dayEpoch, ev)
                                    }
                            ) {
                                Column(modifier = Modifier.padding(4.dp)) {
                                    Text(
                                        text = ev.title,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFF0D47A1)
                                    )
                                    Text(
                                        text = "${toTimeString(ev.startMinutes)} - ${toTimeString(ev.endMinutes)}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showCustomEventDialog) {
        val startMinutes = state.customEventStartMinutes
        val endMinutes = state.customEventEndMinutes
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onDismissCustomEventDialog() },
            title = { Text(if (state.customEditingEventId == null) "新建计划" else "编辑计划") },
            text = {
                Column {
                    Text(text = "时间段：")
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                val h = startMinutes / 60
                                val m = startMinutes % 60
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        viewModel.onCustomEventTimeChange(
                                            startMinutes = hour * 60 + minute
                                        )
                                    },
                                    h,
                                    m,
                                    true
                                ).show()
                            }
                        ) {
                            Text("开始：${toTimeString(startMinutes)}")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(
                            onClick = {
                                val h = endMinutes / 60
                                val m = endMinutes % 60
                                android.app.TimePickerDialog(
                                    context,
                                    { _, hour, minute ->
                                        viewModel.onCustomEventTimeChange(
                                            endMinutes = hour * 60 + minute
                                        )
                                    },
                                    h,
                                    m,
                                    true
                                ).show()
                            }
                        ) {
                            Text("结束：${toTimeString(endMinutes)}")
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.customEventTitle,
                        onValueChange = { viewModel.onCustomEventTitleChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("标题") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = state.customEventContent,
                        onValueChange = { viewModel.onCustomEventContentChange(it) },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("内容") }
                    )
                }
            },
            confirmButton = {
                Row {
                    state.customEditingEventId?.let { id ->
                        TextButton(onClick = { viewModel.onDeleteCustomEvent(id) }) {
                            Text("删除")
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(
                        onClick = {
                            scope.launch {
                                val reminderId = viewModel.createReminderFromCurrentCustomEvent()
                                if (reminderId > 0) {
                                    viewModel.onDismissCustomEventDialog()
                                    context.startActivity(
                                        Intent(
                                            context,
                                            ReviewReminderDetailActivity::class.java
                                        ).putExtra("reminderId", reminderId)
                                    )
                                }
                            }
                        }
                    ) {
                        Text("导入提醒")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = { viewModel.onConfirmCustomEvent() }) {
                        Text("保存")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDismissCustomEventDialog() }) {
                    Text("取消")
                }
            }
        )
    }

    // 分享 / 导入自定义计划弹窗
    if (showShareDialog) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        AlertDialog(
            onDismissRequest = { showShareDialog = false },
            title = { Text(text = "分享 / 导入自定义计划") },
            text = {
                Column {
                    Text(
                        text = "当前自定义计划分享链接（公开，可复制给同学导入）：",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = if (isGeneratingLink) "正在生成链接…" else shareLink,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        label = { Text("分享链接") }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            enabled = shareLink.isNotBlank() && !isGeneratingLink,
                            onClick = {
                                if (shareLink.isNotBlank()) {
                                    val clip =
                                        ClipData.newPlainText("custom_schedule_share_link", shareLink)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "链接已复制到剪贴板", Toast.LENGTH_SHORT).show()
                                }
                            }
                        ) {
                            Text(text = "复制链接")
                        }
                    }
                    if (shareError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = shareError ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "从链接导入自定义计划（会覆盖当前自定义计划）：",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = importLinkText,
                        onValueChange = { importLinkText = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = false,
                        maxLines = 3,
                        label = { Text("粘贴分享链接或分享代码") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val input = importLinkText.trim()
                        if (input.isEmpty()) {
                            Toast.makeText(context, "请输入要导入的链接", Toast.LENGTH_SHORT).show()
                            return@TextButton
                        }
                        scope.launch {
                            val (customJson, error) = fetchCustomScheduleFromLink(input)
                            if (customJson != null) {
                                viewModel.importCustomScheduleFromJson(customJson) { success ->
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "导入成功，当前自定义计划已更新",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        showShareDialog = false
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "导入失败，请稍后重试",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } else {
                                Toast.makeText(
                                    context,
                                    error ?: "无法从链接获取自定义计划，请检查后重试",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                ) {
                    Text(text = "确定导入")
                }
            },
            dismissButton = {
                TextButton(onClick = { showShareDialog = false }) {
                    Text(text = "关闭")
                }
            }
        )
    }
}

private fun toTimeString(mins: Int): String {
    val h = mins / 60
    val m = mins % 60
    return "%02d:%02d".format(h, m)
}

@Composable
private fun ModeChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier,
        color = if (selected) MaterialTheme.colorScheme.primary else Color.Transparent,
        shape = MaterialTheme.shapes.small
    ) {
        TextButton(onClick = onClick) {
            Text(
                text = text,
                color = if (selected) Color.White else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun TimetableGrid(
    state: StudyPlanDetailUiState,
    onCellClick: (Int, Int) -> Unit
) {
    var scale by remember { mutableFloatStateOf(1f) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    var offsetY by remember { mutableFloatStateOf(0f) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        val newScale = (scale * zoomChange).coerceIn(0.8f, 2.0f)
        scale = newScale
        // 放宽平移范围，保证可以将最上/最下几行完全拖入视野
        offsetX = (offsetX + panChange.x).coerceIn(-1200f, 1200f)
        offsetY = (offsetY + panChange.y).coerceIn(-1200f, 1200f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(840.dp) // 再次放大整体表格高度，单元格更大
                .graphicsLayer(
                    scaleX = scale,
                    scaleY = scale,
                    translationX = offsetX,
                    translationY = offsetY
                )
                .transformable(transformState)
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            ) {
                // 顶部星期标题行
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .width(88.dp)
                            .height(48.dp)
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.outlineVariant
                            )
                    )
                    weekdays.forEach { day ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(32.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = day, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // 时间行 + 单元格
                timeSlots.forEachIndexed { rowIndex, timeText ->
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .width(88.dp)
                                .height(72.dp)
                                .border(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.outlineVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = timeText, style = MaterialTheme.typography.titleSmall)
                        }
                        for (colIndex in 0 until 7) {
                            val key = rowIndex to colIndex
                            val content = state.cells[key] ?: CellContent()
                            val isSelected = state.selectedCells.contains(key)
                            val hasData = !content.isEmpty
                            val bgColor = when {
                                isSelected -> MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                                hasData -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                                else -> Color.White
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(72.dp)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outlineVariant
                                    )
                                    .padding(1.dp)
                                    .background(bgColor)
                                    .clickable { onCellClick(rowIndex, colIndex) },
                                contentAlignment = Alignment.Center
                            ) {
                                if (hasData) {
                                    Text(
                                        text = content.title,
                                        style = MaterialTheme.typography.titleSmall,
                                        maxLines = 4
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 额外预留一些底部空间，保证最下方几行也可以完全滚动到视野中
        Spacer(modifier = Modifier.height(216.dp)) // 大约等于 3 个表项高度
    }
}

@Preview(showBackground = true)
@Composable
fun StudyPlanDetailPreview() {
    CourseReviewPlannerTheme {
        StudyPlanDetailScreen(
            viewModel = StudyPlanDetailViewModel(Application()),
            title = "第12周学习计划",
            planId = 1L
        )
    }
}


