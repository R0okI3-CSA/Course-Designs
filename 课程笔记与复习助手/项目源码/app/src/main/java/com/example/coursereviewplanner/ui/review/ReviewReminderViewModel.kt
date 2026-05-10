package com.example.coursereviewplanner.ui.review

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.ReviewReminderRepositoryProvider
import com.example.coursereviewplanner.data.UserRepositoryProvider
import com.example.coursereviewplanner.data.local.ReviewReminderTagWithReminders
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ReviewReminderUiItem(
    val tagId: Long,
    val tagName: String,
    val color: Long,
    val isExpanded: Boolean,
    val reminders: List<ReviewReminderItem>
)

data class ReviewReminderItem(
    val id: Long,
    val title: String,
    val isEnabled: Boolean
)

data class ReviewReminderUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<ReviewReminderUiItem> = emptyList(),
    val ungroupedReminders: List<ReviewReminderItem> = emptyList(),
    val editingName: String = "",
    val targetTagId: Long? = null,
    val targetReminderId: Long? = null,
    val dialogMode: ReviewReminderDialogMode = ReviewReminderDialogMode.NONE
)

enum class ReviewReminderDialogMode {
    NONE,
    CREATE_TAG,
    RENAME_TAG,
    CREATE_REMINDER,
    RENAME_REMINDER
}

class ReviewReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val repository = ReviewReminderRepositoryProvider.get(application)

    var uiState by mutableStateOf(ReviewReminderUiState())
        private set

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val userId = userRepository.currentUserIdFlow.first()
            if (userId == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "当前未登录，请重新登录后再试"
                )
                return@launch
            }

            val tagWithReminders: List<ReviewReminderTagWithReminders> =
                repository.loadTagsWithReminders(userId)
            val ungrouped = repository.loadUngroupedReminders(userId)

            val items = tagWithReminders.map { group ->
                ReviewReminderUiItem(
                    tagId = group.tag.id,
                    tagName = group.tag.name,
                    color = group.tag.color,
                    isExpanded = true,
                    reminders = group.reminders.map { r ->
                        ReviewReminderItem(
                            id = r.id,
                            title = r.title,
                            isEnabled = r.isEnabled
                        )
                    }
                )
            }

            uiState = uiState.copy(
                isLoading = false,
                items = items,
                ungroupedReminders = ungrouped.map { r ->
                    ReviewReminderItem(
                        id = r.id,
                        title = r.title,
                        isEnabled = r.isEnabled
                    )
                }
            )
        }
    }

    fun onAddTagClick() {
        uiState = uiState.copy(
            dialogMode = ReviewReminderDialogMode.CREATE_TAG,
            editingName = "",
            targetTagId = null,
            targetReminderId = null
        )
    }

    fun onAddReminderForTag(tagId: Long) {
        uiState = uiState.copy(
            dialogMode = ReviewReminderDialogMode.CREATE_REMINDER,
            editingName = "",
            targetTagId = tagId,
            targetReminderId = null
        )
    }

    /**
     * 从顶部“＋”菜单新建独立复习提醒：不隶属任何标签。
     */
    fun onAddReminderFromTop() {
        uiState = uiState.copy(
            dialogMode = ReviewReminderDialogMode.CREATE_REMINDER,
            editingName = "",
            targetTagId = null,
            targetReminderId = null
        )
    }

    fun onRenameTag(tagId: Long, currentName: String) {
        uiState = uiState.copy(
            dialogMode = ReviewReminderDialogMode.RENAME_TAG,
            editingName = currentName,
            targetTagId = tagId,
            targetReminderId = null
        )
    }

    fun onRenameReminder(tagId: Long?, reminderId: Long, currentTitle: String) {
        uiState = uiState.copy(
            dialogMode = ReviewReminderDialogMode.RENAME_REMINDER,
            editingName = currentTitle,
            targetTagId = tagId,
            targetReminderId = reminderId
        )
    }

    fun onDialogNameChange(value: String) {
        uiState = uiState.copy(editingName = value)
    }

    fun onDialogDismiss() {
        uiState = uiState.copy(
            dialogMode = ReviewReminderDialogMode.NONE,
            editingName = "",
            targetTagId = null,
            targetReminderId = null
        )
    }

    fun onConfirmDialog() {
        val name = uiState.editingName.trim()
        if (name.isEmpty()) {
            uiState = uiState.copy(errorMessage = "名称不能为空")
            return
        }
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            when (uiState.dialogMode) {
                ReviewReminderDialogMode.CREATE_TAG -> {
                    val color = 0xFF2196F3 // 蓝色
                    repository.createTag(userId, name, color)
                }

                ReviewReminderDialogMode.RENAME_TAG -> {
                    val tagId = uiState.targetTagId ?: return@launch
                    repository.renameTag(userId, tagId, name)
                }

                ReviewReminderDialogMode.CREATE_REMINDER -> {
                    val tagId = uiState.targetTagId
                    repository.createReminder(userId, tagId, name)
                }

                ReviewReminderDialogMode.RENAME_REMINDER -> {
                    val reminderId = uiState.targetReminderId ?: return@launch
                    repository.renameReminder(userId, reminderId, name)
                }

                ReviewReminderDialogMode.NONE -> Unit
            }
            onDialogDismiss()
            refresh()
        }
    }

    fun onDeleteTag(tagId: Long) {
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            repository.deleteTag(userId, tagId)
            refresh()
        }
    }

    fun onDeleteReminder(reminderId: Long) {
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            repository.deleteReminder(userId, reminderId)
            refresh()
        }
    }

    fun toggleTagExpanded(tagId: Long) {
        uiState = uiState.copy(
            items = uiState.items.map { item ->
                if (item.tagId == tagId) {
                    item.copy(isExpanded = !item.isExpanded)
                } else {
                    item
                }
            }
        )
    }
}


