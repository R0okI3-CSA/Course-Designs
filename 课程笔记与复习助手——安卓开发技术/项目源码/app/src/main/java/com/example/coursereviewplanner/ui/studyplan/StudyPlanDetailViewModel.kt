package com.example.coursereviewplanner.ui.studyplan

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.ImportedCustomEvent
import com.example.coursereviewplanner.data.ImportedSlot
import com.example.coursereviewplanner.data.ReviewReminderRepositoryProvider
import com.example.coursereviewplanner.data.StudyPlanRepositoryProvider
import com.example.coursereviewplanner.data.UserRepositoryProvider
import com.example.coursereviewplanner.data.local.StudyPlanCustomEventEntity
import com.example.coursereviewplanner.data.local.StudyPlanSlotEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class ScheduleMode {
    TIMETABLE,
    CUSTOM
}

data class CellContent(
    val title: String = "",
    val location: String = "",
    val content: String = ""
) {
    val isEmpty: Boolean get() = title.isBlank() && location.isBlank() && content.isBlank()
}

data class StudyPlanDetailUiState(
    val mode: ScheduleMode = ScheduleMode.TIMETABLE,
    val cells: Map<Pair<Int, Int>, CellContent> = emptyMap(), // key: (row, col)
    val isMultiSelectMode: Boolean = false,
    val selectedCells: Set<Pair<Int, Int>> = emptySet(),
    val actionCell: Pair<Int, Int>? = null,
    val showCellActionDialog: Boolean = false,
    val showEditDialog: Boolean = false,
    val editTitle: String = "",
    val editLocation: String = "",
    val editContent: String = "",
    val errorMessage: String? = null,
    // 自定义模式：日期段与事件
    val customStartDay: Long? = null, // LocalDate.toEpochDay()
    val customEndDay: Long? = null,
    val customEvents: List<StudyPlanCustomEventEntity> = emptyList(),
    val showCustomEventDialog: Boolean = false,
    val customEditingEventId: Long? = null,
    val customEventDateEpochDay: Long? = null,
    val customEventStartMinutes: Int = 0,
    val customEventEndMinutes: Int = 60,
    val customEventTitle: String = "",
    val customEventContent: String = ""
)

class StudyPlanDetailViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val studyPlanRepository = StudyPlanRepositoryProvider.get(application)
    private val reviewReminderRepository = ReviewReminderRepositoryProvider.get(application)

    private var planId: Long = -1L

    var uiState = androidx.compose.runtime.mutableStateOf(StudyPlanDetailUiState())
        private set

    fun initialize(planId: Long) {
        if (this.planId == planId && this.planId > 0) return
        this.planId = planId
        if (planId <= 0) {
            uiState.value = uiState.value.copy(errorMessage = "计划 ID 无效")
        } else {
            loadSlots()
            decideInitialMode()
        }
    }

    private fun loadSlots() {
        if (planId <= 0) return
        viewModelScope.launch {
            val slots: List<StudyPlanSlotEntity> = studyPlanRepository.getSlotsForPlan(planId)
            val map = slots.associate { slot ->
                (slot.rowIndex to slot.colIndex) to CellContent(
                    title = slot.title.orEmpty(),
                    location = slot.location.orEmpty(),
                    content = slot.content.orEmpty()
                )
            }
            uiState.value = uiState.value.copy(cells = map)
        }
    }

    fun onModeChange(mode: ScheduleMode) {
        uiState.value = uiState.value.copy(mode = mode)
        if (mode == ScheduleMode.CUSTOM) {
            // 默认日期段：今天这一天
            if (uiState.value.customStartDay == null || uiState.value.customEndDay == null) {
                val today = java.time.LocalDate.now().toEpochDay()
                uiState.value = uiState.value.copy(
                    customStartDay = today,
                    customEndDay = today
                )
            }
            loadCustomEvents()
        }
    }

    fun onCellClick(row: Int, col: Int) {
        val state = uiState.value
        val key = row to col
        if (state.isMultiSelectMode) {
            val newSet = state.selectedCells.toMutableSet()
            if (newSet.contains(key)) newSet.remove(key) else newSet.add(key)
            uiState.value = state.copy(selectedCells = newSet)
        } else {
            uiState.value = state.copy(
                actionCell = key,
                showCellActionDialog = true
            )
        }
    }

    fun onCellActionDialogDismiss() {
        uiState.value = uiState.value.copy(
            showCellActionDialog = false,
            actionCell = null
        )
    }

    fun onEditSingleCell() {
        val key = uiState.value.actionCell ?: return
        val content = uiState.value.cells[key] ?: CellContent()
        uiState.value = uiState.value.copy(
            showCellActionDialog = false,
            showEditDialog = true,
            editTitle = content.title,
            editLocation = content.location,
            editContent = content.content
        )
    }

    fun onClearSingleCell() {
        val key = uiState.value.actionCell ?: return
        viewModelScope.launch {
            studyPlanRepository.clearSlot(planId, key.first, key.second)
            val newMap = uiState.value.cells.toMutableMap()
            newMap.remove(key)
            uiState.value = uiState.value.copy(
                cells = newMap,
                showCellActionDialog = false,
                actionCell = null
            )
        }
    }

    fun onEnterMultiSelectMode() {
        val key = uiState.value.actionCell ?: return
        uiState.value = uiState.value.copy(
            isMultiSelectMode = true,
            selectedCells = setOf(key),
            showCellActionDialog = false,
            actionCell = null
        )
    }

    fun onExitMultiSelectMode() {
        uiState.value = uiState.value.copy(
            isMultiSelectMode = false,
            selectedCells = emptySet()
        )
    }

    fun onOpenMultiEditForSelected() {
        if (uiState.value.selectedCells.isEmpty()) return
        uiState.value = uiState.value.copy(
            showEditDialog = true,
            editTitle = "",
            editLocation = "",
            editContent = ""
        )
    }

    fun onEditFieldsChange(title: String? = null, location: String? = null, content: String? = null) {
        val state = uiState.value
        uiState.value = state.copy(
            editTitle = title ?: state.editTitle,
            editLocation = location ?: state.editLocation,
            editContent = content ?: state.editContent
        )
    }

    fun onEditDialogDismiss() {
        uiState.value = uiState.value.copy(
            showEditDialog = false,
            editTitle = "",
            editLocation = "",
            editContent = ""
        )
    }

    fun onConfirmEdit(singleCellOnly: Boolean) {
        viewModelScope.launch {
            if (planId <= 0) return@launch
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            val title = uiState.value.editTitle
            val location = uiState.value.editLocation
            val content = uiState.value.editContent
            if (singleCellOnly) {
                val key = uiState.value.actionCell ?: return@launch
                studyPlanRepository.setSlot(userId, planId, key.first, key.second, title, location, content)
            } else {
                val cells = uiState.value.selectedCells.toList()
                studyPlanRepository.setSlotsBulk(userId, planId, cells, title, location, content)
            }
            onEditDialogDismiss()
            onExitMultiSelectMode()
            loadSlots()
        }
    }

    /**
     * 从当前选中的单元格生成一条独立的复习提醒，返回新提醒的 ID。
     */
    suspend fun createReminderFromCurrentCell(): Long {
        val key = uiState.value.actionCell ?: return -1L
        val content = uiState.value.cells[key] ?: CellContent()
        if (content.isEmpty) return -1L
        val userId = userRepository.currentUserIdFlow.first() ?: return -1L
        // 计算提醒时间：根据当前周几和行号，推算最近一次该周几的日期 + 开始时间
        val row = key.first
        val col = key.second
        val cal = java.util.Calendar.getInstance()
        val todayDow = cal.get(java.util.Calendar.DAY_OF_WEEK) // 1=Sunday
        val targetDow = when (col) {
            0 -> java.util.Calendar.MONDAY
            1 -> java.util.Calendar.TUESDAY
            2 -> java.util.Calendar.WEDNESDAY
            3 -> java.util.Calendar.THURSDAY
            4 -> java.util.Calendar.FRIDAY
            5 -> java.util.Calendar.SATURDAY
            else -> java.util.Calendar.SUNDAY
        }
        var diff = targetDow - todayDow
        if (diff < 0) diff += 7
        cal.add(java.util.Calendar.DAY_OF_MONTH, diff)
        // 行号映射到开始时间
        val startTime = when (row) {
            0 -> 8 to 0
            1 -> 8 to 55
            2 -> 10 to 0
            3 -> 10 to 55
            4 -> 14 to 0
            5 -> 14 to 50
            6 -> 15 to 55
            7 -> 16 to 50
            8 -> 19 to 0
            9 -> 19 to 55
            10 -> 20 to 50
            11 -> 21 to 45
            else -> 8 to 0
        }
        cal.set(java.util.Calendar.HOUR_OF_DAY, startTime.first)
        cal.set(java.util.Calendar.MINUTE, startTime.second)
        cal.set(java.util.Calendar.SECOND, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val triggerAt = cal.timeInMillis

        val title = content.title.ifBlank { "未命名课程" }
        val detailContent = buildString {
            if (content.location.isNotBlank()) {
                append("地点：${content.location}\n")
            }
            if (content.content.isNotBlank()) {
                append("内容：${content.content}")
            }
        }.ifBlank { "请按计划复习本课程。" }

        val reminder = reviewReminderRepository.createReminder(
            userId = userId,
            tagId = null,
            title = title
        )
        // 保存时间与内容（不直接启用，交给用户在详情页确认）
        reviewReminderRepository.updateReminderDetail(
            userId = userId,
            reminderId = reminder.id,
            newTitle = title,
            newContent = detailContent,
            newTimeMillis = triggerAt,
            isEnabled = false
        )
        return reminder.id
    }

    /**
     * 从当前正在编辑的自定义事件生成一条独立复习提醒，返回新提醒的 ID。
     * 标题/内容直接沿用当前弹窗中的输入；
     * 日期取事件所在列的日期，时间取事件的开始时间。
     */
    suspend fun createReminderFromCurrentCustomEvent(): Long {
        val dateEpochDay = uiState.value.customEventDateEpochDay ?: return -1L
        val startMinutes = uiState.value.customEventStartMinutes
        val rawTitle = uiState.value.customEventTitle.trim()
        val rawContent = uiState.value.customEventContent.trim()
        if (rawTitle.isEmpty()) return -1L

        val userId = userRepository.currentUserIdFlow.first() ?: return -1L

        // 将 LocalDate + 开始时间换算为系统时区下的毫秒时间戳
        val localDate = java.time.LocalDate.ofEpochDay(dateEpochDay)
        val hour = startMinutes / 60
        val minute = startMinutes % 60
        val triggerAt = localDate
            .atTime(hour, minute)
            .atZone(java.time.ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val reminder = reviewReminderRepository.createReminder(
            userId = userId,
            tagId = null,
            title = rawTitle
        )
        reviewReminderRepository.updateReminderDetail(
            userId = userId,
            reminderId = reminder.id,
            newTitle = rawTitle,
            newContent = rawContent,
            newTimeMillis = triggerAt,
            isEnabled = false
        )
        return reminder.id
    }

     // --------- 自定义模式事件相关 ----------

    /**
     * 初始化时根据是否存在自定义事件，决定默认进入哪种模式。
     */
    private fun decideInitialMode() {
        if (planId <= 0) return
        viewModelScope.launch {
            val range = studyPlanRepository.getCustomEventDateRange(planId)
            if (range != null) {
                val (start, end) = range
                uiState.value = uiState.value.copy(
                    mode = ScheduleMode.CUSTOM,
                    customStartDay = start,
                    customEndDay = end
                )
                loadCustomEvents()
            }
        }
    }

    private fun loadCustomEvents() {
        val start = uiState.value.customStartDay ?: return
        val end = uiState.value.customEndDay ?: start
        if (planId <= 0) return
        viewModelScope.launch {
            val events = studyPlanRepository.getCustomEventsForRange(planId, start, end)
            uiState.value = uiState.value.copy(customEvents = events)
        }
    }

    fun onCustomDateRangeChange(startDay: Long, endDay: Long) {
        uiState.value = uiState.value.copy(
            customStartDay = startDay,
            customEndDay = endDay
        )
        loadCustomEvents()
    }

    fun onOpenCustomEventDialog(dateEpochDay: Long, event: StudyPlanCustomEventEntity? = null) {
        uiState.value = uiState.value.copy(
            showCustomEventDialog = true,
            customEditingEventId = event?.id,
            customEventDateEpochDay = dateEpochDay,
            customEventStartMinutes = event?.startMinutes ?: 0,
            customEventEndMinutes = event?.endMinutes ?: 60,
            customEventTitle = event?.title ?: "",
            customEventContent = event?.content ?: ""
        )
    }

    fun onCustomEventTimeChange(startMinutes: Int? = null, endMinutes: Int? = null) {
        val s = uiState.value
        uiState.value = s.copy(
            customEventStartMinutes = startMinutes ?: s.customEventStartMinutes,
            customEventEndMinutes = endMinutes ?: s.customEventEndMinutes
        )
    }

    fun onCustomEventTitleChange(value: String) {
        uiState.value = uiState.value.copy(customEventTitle = value)
    }

    fun onCustomEventContentChange(value: String) {
        uiState.value = uiState.value.copy(customEventContent = value)
    }

    fun onDismissCustomEventDialog() {
        uiState.value = uiState.value.copy(
            showCustomEventDialog = false,
            customEditingEventId = null,
            customEventDateEpochDay = null,
            customEventTitle = "",
            customEventContent = ""
        )
    }

    fun onConfirmCustomEvent() {
        val date = uiState.value.customEventDateEpochDay ?: return
        val start = uiState.value.customEventStartMinutes
        val end = uiState.value.customEventEndMinutes
        val title = uiState.value.customEventTitle.trim()
        val content = uiState.value.customEventContent.trim()
        if (title.isEmpty() || end <= start) return
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            studyPlanRepository.saveCustomEvent(
                userId = userId,
                planId = planId,
                eventId = uiState.value.customEditingEventId,
                dateEpochDay = date,
                startMinutes = start,
                endMinutes = end,
                title = title,
                content = content
            )
            onDismissCustomEventDialog()
            loadCustomEvents()
        }
    }

    fun onDeleteCustomEvent(eventId: Long) {
        viewModelScope.launch {
            studyPlanRepository.deleteCustomEvent(eventId)
            loadCustomEvents()
        }
    }

    /**
     * 将从服务器获取到的课表 JSON 导入到当前计划中，覆盖原有课程表。
     * timetableJson 结构示例：
     * {
     *   "planTitle": "...",
     *   "cells": [
     *     { "rowIndex": 0, "colIndex": 1, "title": "...", "location": "...", "content": "..." },
     *     ...
     *   ]
     * }
     */
    fun importTimetableFromJson(timetableJson: JSONObject, onComplete: (Boolean) -> Unit) {
        if (planId <= 0) {
            onComplete(false)
            return
        }
        val cellsArray: JSONArray = timetableJson.optJSONArray("cells") ?: JSONArray()
        if (cellsArray.length() == 0) {
            onComplete(false)
            return
        }

        val imported = mutableListOf<ImportedSlot>()
        for (i in 0 until cellsArray.length()) {
            val obj = cellsArray.optJSONObject(i) ?: continue
            val row = obj.optInt("rowIndex", -1)
            val col = obj.optInt("colIndex", -1)
            if (row < 0 || col < 0) continue
            val title = obj.optString("title", "")
            val location = obj.optString("location", "")
            val content = obj.optString("content", "")
            imported.add(
                ImportedSlot(
                    rowIndex = row,
                    colIndex = col,
                    title = title,
                    location = location,
                    content = content
                )
            )
        }

        if (imported.isEmpty()) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first()
            if (userId == null) {
                onComplete(false)
                return@launch
            }
            try {
                studyPlanRepository.overwriteSlotsForPlan(userId, planId, imported)
                loadSlots()
                onComplete(true)
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    errorMessage = "导入课表失败：${e.message}"
                )
                onComplete(false)
            }
        }
    }

    /**
     * 将从服务器获取到的自定义计划 JSON 导入到当前计划中，覆盖原有自定义事件。
     * 结构示例：
     * {
     *   "planTitle": "...",
     *   "startDayEpoch": 12345,
     *   "endDayEpoch": 12349,
     *   "events": [
     *     { "dateEpochDay": 12345, "startMinutes": 480, "endMinutes": 540, "title": "...", "content": "..." },
     *     ...
     *   ]
     * }
     */
    fun importCustomScheduleFromJson(customJson: JSONObject, onComplete: (Boolean) -> Unit) {
        if (planId <= 0) {
            onComplete(false)
            return
        }
        val eventsArray: JSONArray = customJson.optJSONArray("events") ?: JSONArray()
        if (eventsArray.length() == 0) {
            onComplete(false)
            return
        }

        val imported = mutableListOf<ImportedCustomEvent>()
        var minDay: Long? = null
        var maxDay: Long? = null

        for (i in 0 until eventsArray.length()) {
            val obj = eventsArray.optJSONObject(i) ?: continue
            val day = obj.optLong("dateEpochDay", Long.MIN_VALUE)
            val start = obj.optInt("startMinutes", -1)
            val end = obj.optInt("endMinutes", -1)
            if (day == Long.MIN_VALUE || start < 0 || end <= start) continue
            val title = obj.optString("title", "")
            val content = obj.optString("content", "")
            imported.add(
                ImportedCustomEvent(
                    dateEpochDay = day,
                    startMinutes = start,
                    endMinutes = end,
                    title = title,
                    content = content
                )
            )
            if (minDay == null || day < minDay!!) minDay = day
            if (maxDay == null || day > maxDay!!) maxDay = day
        }

        if (imported.isEmpty()) {
            onComplete(false)
            return
        }

        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first()
            if (userId == null) {
                onComplete(false)
                return@launch
            }
            try {
                studyPlanRepository.overwriteCustomEventsForPlan(userId, planId, imported)
                // 更新当前日期范围为导入数据的范围
                if (minDay != null && maxDay != null) {
                    uiState.value = uiState.value.copy(
                        mode = ScheduleMode.CUSTOM,
                        customStartDay = minDay,
                        customEndDay = maxDay
                    )
                }
                loadCustomEvents()
                onComplete(true)
            } catch (e: Exception) {
                uiState.value = uiState.value.copy(
                    errorMessage = "导入自定义计划失败：${e.message}"
                )
                onComplete(false)
            }
        }
    }
}


