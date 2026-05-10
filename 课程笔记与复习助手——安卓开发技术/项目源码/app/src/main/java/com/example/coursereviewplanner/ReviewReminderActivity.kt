@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.unit.dp
import com.example.coursereviewplanner.ui.review.ReviewReminderDialogMode
import com.example.coursereviewplanner.ui.review.ReviewReminderItem
import com.example.coursereviewplanner.ui.review.ReviewReminderUiItem
import com.example.coursereviewplanner.ui.review.ReviewReminderViewModel
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

class ReviewReminderActivity : ComponentActivity() {

    private val vm: ReviewReminderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                ReviewReminderScreen(
                    viewModel = vm,
                    onOpenReminderDetail = { reminderId ->
                        startActivity(
                            Intent(
                                this,
                                ReviewReminderDetailActivity::class.java
                            ).putExtra("reminderId", reminderId)
                        )
                    }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // 每次从详情页返回时刷新列表，保证标题和启用状态是最新的
        vm.refresh()
    }
}

@Composable
fun ReviewReminderScreen(
    viewModel: ReviewReminderViewModel,
    onOpenReminderDetail: (Long) -> Unit
) {
    val state = viewModel.uiState
    var topMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "复习提醒管理",
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
                            contentDescription = "添加提醒或标签"
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
                            text = { Text("新建复习提醒") },
                            onClick = {
                                topMenuExpanded = false
                                viewModel.onAddReminderFromTop()
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
                text = "复习提醒",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 未分组提醒
                items(state.ungroupedReminders) { reminder ->
                    ReminderRow(
                        title = reminder.title,
                        isEnabled = reminder.isEnabled,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onOpenReminderDetail(reminder.id) },
                        onDelete = { viewModel.onDeleteReminder(reminder.id) }
                    )
                }

                if (state.ungroupedReminders.isNotEmpty() && state.items.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                // 带标签的提醒
                items(state.items) { item ->
                    TagWithRemindersRow(
                        item = item,
                        onToggleExpand = { viewModel.toggleTagExpanded(item.tagId) },
                        onAddReminder = { viewModel.onAddReminderForTag(item.tagId) },
                        onRenameTag = { viewModel.onRenameTag(item.tagId, item.tagName) },
                        onDeleteTag = { viewModel.onDeleteTag(item.tagId) },
                        onOpenReminderDetail = onOpenReminderDetail,
                        onDeleteReminder = { reminderId -> viewModel.onDeleteReminder(reminderId) }
                    )
                }
            }
        }
    }

    if (state.dialogMode != ReviewReminderDialogMode.NONE) {
        val title = when (state.dialogMode) {
            ReviewReminderDialogMode.CREATE_TAG -> "新建标签"
            ReviewReminderDialogMode.RENAME_TAG -> "重命名标签"
            ReviewReminderDialogMode.CREATE_REMINDER -> "新建复习提醒"
            ReviewReminderDialogMode.RENAME_REMINDER -> "重命名复习提醒"
            ReviewReminderDialogMode.NONE -> ""
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
private fun TagWithRemindersRow(
    item: ReviewReminderUiItem,
    onToggleExpand: () -> Unit,
    onAddReminder: () -> Unit,
    onRenameTag: () -> Unit,
    onDeleteTag: () -> Unit,
    onOpenReminderDetail: (Long) -> Unit,
    onDeleteReminder: (Long) -> Unit
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
                text = { Text("添加提醒") },
                onClick = {
                    tagMenuExpanded = false
                    onAddReminder()
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
        item.reminders.forEach { reminder ->
            ReminderRow(
                title = reminder.title,
                isEnabled = reminder.isEnabled,
                color = MaterialTheme.colorScheme.primary,
                onClick = { onOpenReminderDetail(reminder.id) },
                onDelete = { onDeleteReminder(reminder.id) }
            )
        }
    }
}

@Composable
private fun ReminderRow(
    title: String,
    isEnabled: Boolean,
    color: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧颜色条
        androidx.compose.foundation.Canvas(
            modifier = Modifier
                .height(24.dp)
                .width(4.dp)
        ) {
            drawRect(color = color)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = if (isEnabled) "已开启" else "未开启",
            style = MaterialTheme.typography.labelSmall,
            color = if (isEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
            modifier = Modifier.padding(end = 8.dp)
        )
        TextButton(onClick = onDelete) {
            Text(text = "删除")
        }
    }
}
