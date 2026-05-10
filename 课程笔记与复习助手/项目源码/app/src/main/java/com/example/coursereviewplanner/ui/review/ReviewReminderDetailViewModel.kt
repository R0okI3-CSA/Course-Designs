package com.example.coursereviewplanner.ui.review

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.ReviewReminderRepositoryProvider
import com.example.coursereviewplanner.data.UserRepositoryProvider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

data class ReviewReminderDetailUiState(
    val reminderId: Long = -1L,
    val title: String = "",
    val content: String = "",
    val year: Int? = null,
    val month: Int? = null, // 1-12
    val day: Int? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val isEnabled: Boolean = false,
    val isLoading: Boolean = true,
    val errorMessage: String? = null
) {
    val hasDateTime: Boolean
        get() = year != null && month != null && day != null && hour != null && minute != null
}

class ReviewReminderDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val repository = ReviewReminderRepositoryProvider.get(application)

    var uiState by mutableStateOf(ReviewReminderDetailUiState())
        private set

    fun initialize(reminderId: Long) {
        if (uiState.reminderId == reminderId && uiState.reminderId > 0) return
        if (reminderId <= 0) {
            uiState = uiState.copy(
                reminderId = reminderId,
                isLoading = false,
                errorMessage = "无效的提醒 ID"
            )
            return
        }
        uiState = uiState.copy(reminderId = reminderId, isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first()
            if (userId == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "当前未登录，请重新登录后再试"
                )
                return@launch
            }
            val reminder = repository.getReminder(userId, reminderId)
            if (reminder == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "提醒不存在或已被删除"
                )
            } else {
                val cal = Calendar.getInstance()
                var y: Int? = null
                var m: Int? = null
                var d: Int? = null
                var h: Int? = null
                var min: Int? = null
                reminder.targetTime?.let { millis ->
                    cal.timeInMillis = millis
                    y = cal.get(Calendar.YEAR)
                    m = cal.get(Calendar.MONTH) + 1
                    d = cal.get(Calendar.DAY_OF_MONTH)
                    h = cal.get(Calendar.HOUR_OF_DAY)
                    min = cal.get(Calendar.MINUTE)
                }
                uiState = uiState.copy(
                    isLoading = false,
                    title = reminder.title,
                    content = reminder.content,
                    year = y,
                    month = m,
                    day = d,
                    hour = h,
                    minute = min,
                    isEnabled = reminder.isEnabled
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        uiState = uiState.copy(title = value)
    }

    fun onContentChange(value: String) {
        uiState = uiState.copy(content = value)
    }

    fun onDateSelected(year: Int, month: Int, day: Int) {
        uiState = uiState.copy(year = year, month = month, day = day)
    }

    fun onTimeSelected(hour: Int, minute: Int) {
        uiState = uiState.copy(hour = hour, minute = minute)
    }

    fun onEnabledChange(enabled: Boolean) {
        uiState = uiState.copy(isEnabled = enabled)
    }

    fun buildTargetTimeMillis(): Long? {
        val y = uiState.year
        val m = uiState.month
        val d = uiState.day
        val h = uiState.hour
        val min = uiState.minute
        if (y == null || m == null || d == null || h == null || min == null) return null
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, y)
            set(Calendar.MONTH, m - 1)
            set(Calendar.DAY_OF_MONTH, d)
            set(Calendar.HOUR_OF_DAY, h)
            set(Calendar.MINUTE, min)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    fun saveReminder(onFinished: (Long, Long?, Boolean) -> Unit) {
        val reminderId = uiState.reminderId
        if (reminderId <= 0) return
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            val targetMillis = buildTargetTimeMillis()
            // 若选择的时间早于当前时间，则视为未设置有效时间，不启用提醒，避免立刻触发或被系统丢弃
            val now = System.currentTimeMillis()
            val safeTarget = if (targetMillis != null && targetMillis > now + 5_000L) {
                targetMillis
            } else {
                null
            }
            repository.updateReminderDetail(
                userId = userId,
                reminderId = reminderId,
                newTitle = uiState.title,
                newContent = uiState.content,
                newTimeMillis = safeTarget,
                isEnabled = uiState.isEnabled && safeTarget != null
            )
            onFinished(reminderId, safeTarget, uiState.isEnabled && safeTarget != null)
        }
    }
}


