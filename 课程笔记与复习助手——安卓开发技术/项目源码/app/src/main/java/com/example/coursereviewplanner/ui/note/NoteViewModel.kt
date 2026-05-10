package com.example.coursereviewplanner.ui.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.NoteRepositoryProvider
import com.example.coursereviewplanner.data.UserRepositoryProvider
import com.example.coursereviewplanner.data.local.NoteTagWithNotes
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class NoteUiItem(
    val tagId: Long,
    val tagName: String,
    val color: Long,
    val isExpanded: Boolean,
    val notes: List<NoteItem>
)

data class NoteItem(
    val id: Long,
    val title: String
)

data class NoteUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<NoteUiItem> = emptyList(),
    val ungroupedNotes: List<NoteItem> = emptyList(),
    val editingName: String = "",
    val targetTagId: Long? = null,
    val targetNoteId: Long? = null,
    val dialogMode: NoteDialogMode = NoteDialogMode.NONE
)

enum class NoteDialogMode {
    NONE,
    CREATE_TAG,
    RENAME_TAG,
    CREATE_NOTE,
    RENAME_NOTE
}

class NoteViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val noteRepository = NoteRepositoryProvider.get(application)

    var uiState by mutableStateOf(NoteUiState())
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

            val tagWithNotes: List<NoteTagWithNotes> =
                noteRepository.loadTagsWithNotes(userId)
            val ungrouped = noteRepository.loadUngroupedNotes(userId)

            val items = tagWithNotes.map { group ->
                NoteUiItem(
                    tagId = group.tag.id,
                    tagName = group.tag.name,
                    color = group.tag.color,
                    isExpanded = true,
                    notes = group.notes.map { n ->
                        NoteItem(
                            id = n.id,
                            title = n.title
                        )
                    }
                )
            }

            uiState = uiState.copy(
                isLoading = false,
                items = items,
                ungroupedNotes = ungrouped.map { n ->
                    NoteItem(id = n.id, title = n.title)
                }
            )
        }
    }

    fun onAddTagClick() {
        uiState = uiState.copy(
            dialogMode = NoteDialogMode.CREATE_TAG,
            editingName = "",
            targetTagId = null,
            targetNoteId = null
        )
    }

    fun onAddNoteForTag(tagId: Long) {
        uiState = uiState.copy(
            dialogMode = NoteDialogMode.CREATE_NOTE,
            editingName = "",
            targetTagId = tagId,
            targetNoteId = null
        )
    }

    /**
     * 从顶部“＋”菜单新建独立笔记：不隶属任何标签。
     */
    fun onAddNoteFromTop() {
        uiState = uiState.copy(
            dialogMode = NoteDialogMode.CREATE_NOTE,
            editingName = "",
            targetTagId = null,
            targetNoteId = null
        )
    }

    fun onRenameTag(tagId: Long, currentName: String) {
        uiState = uiState.copy(
            dialogMode = NoteDialogMode.RENAME_TAG,
            editingName = currentName,
            targetTagId = tagId,
            targetNoteId = null
        )
    }

    fun onRenameNote(tagId: Long?, noteId: Long, currentTitle: String) {
        uiState = uiState.copy(
            dialogMode = NoteDialogMode.RENAME_NOTE,
            editingName = currentTitle,
            targetTagId = tagId,
            targetNoteId = noteId
        )
    }

    fun onDialogNameChange(value: String) {
        uiState = uiState.copy(editingName = value)
    }

    fun onDialogDismiss() {
        uiState = uiState.copy(
            dialogMode = NoteDialogMode.NONE,
            editingName = "",
            targetTagId = null,
            targetNoteId = null
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
                NoteDialogMode.CREATE_TAG -> {
                    val color = 0xFF4CAF50 // 绿色
                    noteRepository.createTag(userId, name, color)
                }

                NoteDialogMode.RENAME_TAG -> {
                    val tagId = uiState.targetTagId ?: return@launch
                    noteRepository.renameTag(userId, tagId, name)
                }

                NoteDialogMode.CREATE_NOTE -> {
                    val tagId = uiState.targetTagId
                    noteRepository.createNote(userId, tagId, name)
                }

                NoteDialogMode.RENAME_NOTE -> {
                    val noteId = uiState.targetNoteId ?: return@launch
                    noteRepository.renameNote(userId, noteId, name)
                }

                NoteDialogMode.NONE -> Unit
            }
            onDialogDismiss()
            refresh()
        }
    }

    fun onDeleteTag(tagId: Long) {
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            noteRepository.deleteTag(userId, tagId)
            refresh()
        }
    }

    fun onDeleteNote(noteId: Long) {
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            noteRepository.deleteNote(userId, noteId)
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


