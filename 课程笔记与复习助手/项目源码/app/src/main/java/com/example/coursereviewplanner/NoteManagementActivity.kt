@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursereviewplanner.ui.note.NoteDialogMode
import com.example.coursereviewplanner.ui.note.NoteItem
import com.example.coursereviewplanner.ui.note.NoteUiItem
import com.example.coursereviewplanner.ui.note.NoteViewModel
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

class NoteManagementActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: NoteViewModel = viewModel()
                NoteManagementScreen(
                    viewModel = vm,
                    onOpenNoteDetail = { noteId ->
                        startActivity(
                            Intent(
                                this,
                                NoteDetailActivity::class.java
                            ).putExtra("noteId", noteId)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun NoteManagementScreen(
    viewModel: NoteViewModel,
    onOpenNoteDetail: (Long) -> Unit
) {
    val state = viewModel.uiState
    var topMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "笔记管理",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "刷新",
                            tint = Color.Black
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { topMenuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "添加笔记或标签"
                        )
                    }
                    DropdownMenu(
                        expanded = topMenuExpanded,
                        onDismissRequest = { topMenuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("新建标签") },
                            onClick = {
                                topMenuExpanded = false
                                viewModel.onAddTagClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("新建笔记") },
                            onClick = {
                                topMenuExpanded = false
                                viewModel.onAddNoteFromTop()
                            }
                        )
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
                .padding(16.dp)
        ) {
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "笔记",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 未分组笔记
                items(state.ungroupedNotes) { note ->
                    NoteRow(
                        title = note.title,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onOpenNoteDetail(note.id) },
                        onRename = {
                            viewModel.onRenameNote(
                                tagId = null,
                                noteId = note.id,
                                currentTitle = note.title
                            )
                        },
                        onDelete = { viewModel.onDeleteNote(note.id) }
                    )
                }

                if (state.ungroupedNotes.isNotEmpty() && state.items.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                // 带标签的笔记
                items(state.items) { item ->
                    TagWithNotesRow(
                        item = item,
                        onToggleExpand = { viewModel.toggleTagExpanded(item.tagId) },
                        onAddNote = { viewModel.onAddNoteForTag(item.tagId) },
                        onRenameTag = { viewModel.onRenameTag(item.tagId, item.tagName) },
                        onDeleteTag = { viewModel.onDeleteTag(item.tagId) },
                        onOpenNoteDetail = onOpenNoteDetail,
                        onRenameNote = { noteId, title ->
                            viewModel.onRenameNote(item.tagId, noteId, title)
                        },
                        onDeleteNote = { noteId -> viewModel.onDeleteNote(noteId) }
                    )
                }
            }
        }
    }

    if (state.dialogMode != NoteDialogMode.NONE) {
        val title = when (state.dialogMode) {
            NoteDialogMode.CREATE_TAG -> "新建标签"
            NoteDialogMode.RENAME_TAG -> "重命名标签"
            NoteDialogMode.CREATE_NOTE -> "新建笔记"
            NoteDialogMode.RENAME_NOTE -> "重命名笔记"
            NoteDialogMode.NONE -> ""
        }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onDialogDismiss() },
            title = { Text(text = title) },
            text = {
                OutlinedTextField(
                    value = state.editingName,
                    onValueChange = viewModel::onDialogNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("名称") }
                )
            },
            confirmButton = {
                TextButton(onClick = { viewModel.onConfirmDialog() }) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onDialogDismiss() }) {
                    Text("取消")
                }
            }
        )
    }
}

@Composable
private fun TagWithNotesRow(
    item: NoteUiItem,
    onToggleExpand: () -> Unit,
    onAddNote: () -> Unit,
    onRenameTag: () -> Unit,
    onDeleteTag: () -> Unit,
    onOpenNoteDetail: (Long) -> Unit,
    onRenameNote: (Long, String) -> Unit,
    onDeleteNote: (Long) -> Unit
) {
    var tagMenuExpanded by remember { mutableStateOf(false) }

    // 标签行
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggleExpand() }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (item.isExpanded) "▾" else "▸",
            modifier = Modifier.padding(end = 8.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = item.tagName,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = { tagMenuExpanded = true }) {
            Text(text = "⋯")
        }
        DropdownMenu(
            expanded = tagMenuExpanded,
            onDismissRequest = { tagMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("添加笔记") },
                onClick = {
                    tagMenuExpanded = false
                    onAddNote()
                }
            )
            DropdownMenuItem(
                text = { Text("重命名标签") },
                onClick = {
                    tagMenuExpanded = false
                    onRenameTag()
                }
            )
            DropdownMenuItem(
                text = { Text("删除标签") },
                onClick = {
                    tagMenuExpanded = false
                    onDeleteTag()
                }
            )
        }
    }

    if (item.isExpanded) {
        item.notes.forEach { note ->
            NoteRow(
                title = note.title,
                color = Color(item.color),
                onClick = { onOpenNoteDetail(note.id) },
                onRename = { onRenameNote(note.id, note.title) },
                onDelete = { onDeleteNote(note.id) }
            )
        }
    }
}

@Composable
private fun NoteRow(
    title: String,
    color: Color,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(start = 8.dp, top = 4.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(
            modifier = Modifier
                .height(12.dp)
                .padding(end = 8.dp)
        ) {
            drawCircle(color = color, radius = size.minDimension / 2)
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = { expanded = true }) {
            Text(text = "⋯")
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("重命名") },
                onClick = {
                    expanded = false
                    onRename()
                }
            )
            DropdownMenuItem(
                text = { Text("删除") },
                onClick = {
                    expanded = false
                    onDelete()
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteManagementPreview() {
    CourseReviewPlannerTheme {
        NoteManagementScreen(
            viewModel = NoteViewModel(Application()),
            onOpenNoteDetail = {}
        )
    }
}


