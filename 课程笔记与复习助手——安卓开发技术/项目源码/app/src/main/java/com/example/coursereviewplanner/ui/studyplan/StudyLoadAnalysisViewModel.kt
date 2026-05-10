package com.example.coursereviewplanner.ui.studyplan

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.StudyPlanRepositoryProvider
import kotlinx.coroutines.launch

data class DailyLoad(
    val label: String,
    val tasks: Int,
    val minutes: Int
)

data class StudyLoadAnalysisUiState(
    val loading: Boolean = true,
    val days: List<DailyLoad> = emptyList(),
    val totalMinutes: Int = 0,
    val averageMinutesPerDay: Float = 0f,
    val pressureScore: Int = 0,
    val errorMessage: String? = null
)

class StudyLoadAnalysisViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val studyPlanRepository = StudyPlanRepositoryProvider.get(application)

    var uiState = mutableStateOf(StudyLoadAnalysisUiState())
        private set

    // 每个课程表格子的时长（分钟），与 StudyPlanDetailActivity 中的 timeSlots 对应
    private val timetableSlotMinutes = intArrayOf(
        45, 45, 45, 45,
        45, 45, 45, 45,
        45, 45, 45, 45
    )

    private val weekdayLabels = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

    /**
     * 计算学习压力：
     * - 课程表模式：始终统计整周（周一~周日）的格子；
     * - 自定义模式：若提供 customStartDay/customEndDay，则只统计当前自定义表格范围内的事件；
     *   否则退回为统计该计划下所有自定义事件的整体情况。
     */
    fun loadForPlan(
        planId: Long,
        customStartDay: Long? = null,
        customEndDay: Long? = null
    ) {
        if (planId <= 0) {
            uiState.value = StudyLoadAnalysisUiState(
                loading = false,
                errorMessage = "计划 ID 无效"
            )
            return
        }
        viewModelScope.launch {
            try {
                val slots = studyPlanRepository.getSlotsForPlan(planId)

                // 1. 先按“每周七天”统计课程表模式下的任务和时长
                val timetableTasksPerWeekday = IntArray(7)
                val timetableMinutesPerWeekday = IntArray(7)
                slots.forEach { slot ->
                    val row = slot.rowIndex.coerceIn(0, timetableSlotMinutes.lastIndex)
                    val col = slot.colIndex.coerceIn(0, 6)
                    timetableTasksPerWeekday[col] += 1
                    timetableMinutesPerWeekday[col] += timetableSlotMinutes[row]
                }

                // 2. 自定义事件：先按“具体日期”统计，再视情况转换
                val effectiveRange = when {
                    customStartDay != null && customEndDay != null &&
                        customStartDay > 0 && customEndDay > 0 -> {
                        customStartDay to customEndDay
                    }
                    else -> studyPlanRepository.getCustomEventDateRange(planId)
                }

                val customByDate = mutableMapOf<Long, Pair<Int, Int>>() // dateEpochDay -> (tasks, minutes)
                if (effectiveRange != null) {
                    val (start, end) = effectiveRange
                    val events = studyPlanRepository.getCustomEventsForRange(planId, start, end)
                    events.forEach { ev ->
                        val key = ev.dateEpochDay
                        val prev = customByDate[key] ?: 0 to 0
                        val minutes = (ev.endMinutes - ev.startMinutes).coerceAtLeast(0)
                        customByDate[key] = (prev.first + 1) to (prev.second + minutes)
                    }
                }

                val days = mutableListOf<DailyLoad>()
                var totalMinutes = 0

                if (customStartDay != null && customEndDay != null &&
                    customStartDay > 0 && customEndDay > 0
                ) {
                    // 自定义模式下：只统计当前表格里的日期，有几天就生成几个统计项
                    val start = customStartDay
                    val end = customEndDay
                    val dayCount = (end - start + 1).toInt().coerceAtLeast(1)
                    for (i in 0 until dayCount) {
                        val epoch = start + i
                        val date = java.time.LocalDate.ofEpochDay(epoch)
                        val weekdayIndex = (date.dayOfWeek.value - 1).coerceIn(0, 6)
                        val timetableTasks = timetableTasksPerWeekday[weekdayIndex]
                        val timetableMinutes = timetableMinutesPerWeekday[weekdayIndex]
                        val (customTasks, customMinutes) = customByDate[epoch] ?: (0 to 0)
                        val tasks = timetableTasks + customTasks
                        val minutes = timetableMinutes + customMinutes
                        days.add(
                            DailyLoad(
                                label = "${date.monthValue}/${date.dayOfMonth}",
                                tasks = tasks,
                                minutes = minutes
                            )
                        )
                        totalMinutes += minutes
                    }
                } else {
                    // 非自定义模式：仍按“周一~周日”展示一周整体情况
                    val tasksPerWeekday = IntArray(7) { timetableTasksPerWeekday[it] }
                    val minutesPerWeekday = IntArray(7) { timetableMinutesPerWeekday[it] }

                    // 把自定义事件按星期几汇总进来
                    customByDate.forEach { (epoch, pair) ->
                        val date = java.time.LocalDate.ofEpochDay(epoch)
                        val idx = (date.dayOfWeek.value - 1).coerceIn(0, 6)
                        tasksPerWeekday[idx] += pair.first
                        minutesPerWeekday[idx] += pair.second
                    }

                    for (i in 0 until 7) {
                        days.add(
                            DailyLoad(
                                label = weekdayLabels[i],
                                tasks = tasksPerWeekday[i],
                                minutes = minutesPerWeekday[i]
                            )
                        )
                        totalMinutes += minutesPerWeekday[i]
                    }
                }

                val dayCount = days.size
                val avgPerDay =
                    if (dayCount > 0) totalMinutes.toFloat() / dayCount.toFloat() else 0f

                // 简单压力模型：以每天 6 小时学习为“100 压力”，其下线性缩放
                val maxMinutesPerDay = 6 * 60f
                val dailyPressureScores = days.map { d ->
                    if (d.minutes <= 0) 0f
                    else (d.minutes.toFloat() / maxMinutesPerDay * 100f).coerceAtMost(120f)
                }
                val overallPressure =
                    if (dailyPressureScores.isNotEmpty()) dailyPressureScores.average().toInt()
                    else 0

                uiState.value = StudyLoadAnalysisUiState(
                    loading = false,
                    days = days,
                    totalMinutes = totalMinutes,
                    averageMinutesPerDay = avgPerDay,
                    pressureScore = overallPressure.coerceIn(0, 120),
                    errorMessage = null
                )
            } catch (e: Exception) {
                uiState.value = StudyLoadAnalysisUiState(
                    loading = false,
                    errorMessage = "分析失败：${e.message}"
                )
            }
        }
    }
}


