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
import androidx.compose.material3.AlertDialog
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
import com.example.coursereviewplanner.ui.studyplan.DialogMode
import com.example.coursereviewplanner.ui.studyplan.StudyPlanPlanItem
import com.example.coursereviewplanner.ui.studyplan.StudyPlanUiItem
import com.example.coursereviewplanner.ui.studyplan.StudyPlanViewModel
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

class StudyPlanActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: StudyPlanViewModel = viewModel()
                StudyPlanScreen(
                    viewModel = vm,
                    onOpenPlanDetail = { planId, title ->
                        startActivity(
                            Intent(
                                this,
                                StudyPlanDetailActivity::class.java
                            )
                                .putExtra("planId", planId)
                                .putExtra("planTitle", title)
                        )
                    }
                )
            }
        }
    }
}

@Composable
fun StudyPlanScreen(
    viewModel: StudyPlanViewModel,
    onOpenPlanDetail: (Long, String) -> Unit
) {
    val state = viewModel.uiState
    var topMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "学习计划管理",
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
                            contentDescription = "添加标签或计划"
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
                            text = { Text("新建学习计划") },
                            onClick = {
                                topMenuExpanded = false
                                viewModel.onAddPlanFromTop()
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
                text = "主题",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                // 未分组计划（无标签）
                items(state.ungroupedPlans) { plan ->
                    PlanRow(
                        title = plan.title,
                        color = MaterialTheme.colorScheme.primary,
                        onClick = { onOpenPlanDetail(plan.id, plan.title) },
                        onRename = {
                            viewModel.onRenamePlan(
                                tagId = 0L,
                                planId = plan.id,
                                currentTitle = plan.title
                            )
                        },
                        onDelete = { viewModel.onDeletePlan(plan.id) }
                    )
                }

                if (state.ungroupedPlans.isNotEmpty() && state.items.isNotEmpty()) {
                    item { Spacer(modifier = Modifier.height(12.dp)) }
                }

                items(state.items) { item ->
                    TagWithPlansRow(
                        item = item,
                        onToggleExpand = { viewModel.toggleTagExpanded(item.tagId) },
                        onAddPlan = { viewModel.onAddPlanForTag(item.tagId) },
                        onRenameTag = { viewModel.onRenameTag(item.tagId, item.tagName) },
                        onDeleteTag = { viewModel.onDeleteTag(item.tagId) },
                        onOpenPlanDetail = onOpenPlanDetail,
                        onRenamePlan = { planId, title ->
                            viewModel.onRenamePlan(item.tagId, planId, title)
                        },
                        onDeletePlan = { planId -> viewModel.onDeletePlan(planId) }
                    )
                }
            }
        }
    }

    if (state.dialogMode != DialogMode.NONE) {
        val title = when (state.dialogMode) {
            DialogMode.CREATE_TAG -> "新建标签"
            DialogMode.RENAME_TAG -> "重命名标签"
            DialogMode.CREATE_PLAN -> "新建学习计划"
            DialogMode.RENAME_PLAN -> "重命名学习计划"
            DialogMode.NONE -> ""
        }
        AlertDialog(
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
private fun TagWithPlansRow(
    item: StudyPlanUiItem,
    onToggleExpand: () -> Unit,
    onAddPlan: () -> Unit,
    onRenameTag: () -> Unit,
    onDeleteTag: () -> Unit,
    onOpenPlanDetail: (Long, String) -> Unit,
    onRenamePlan: (Long, String) -> Unit,
    onDeletePlan: (Long) -> Unit
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
                text = { Text("添加计划") },
                onClick = {
                    tagMenuExpanded = false
                    onAddPlan()
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

    // 计划列表
    if (item.isExpanded) {
        item.plans.forEach { plan ->
            PlanRow(
                title = plan.title,
                color = Color(item.color),
                onClick = { onOpenPlanDetail(plan.id, plan.title) },
                onRename = { onRenamePlan(plan.id, plan.title) },
                onDelete = { onDeletePlan(plan.id) }
            )
        }
    }
}

@Composable
private fun PlanRow(
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
            .padding(start = 32.dp, top = 4.dp, bottom = 4.dp),
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
fun StudyPlanPreview() {
    CourseReviewPlannerTheme {
        val dummyItem = StudyPlanUiItem(
            tagId = 1L,
            tagName = "11月学习计划",
            color = 0xFFFF9800,
            isExpanded = true,
            plans = listOf(
                StudyPlanPlanItem(
                    id = 1L,
                    title = "第12周"
                ),
                StudyPlanPlanItem(
                    id = 2L,
                    title = "第13周"
                )
            )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            TagWithPlansRow(
                item = dummyItem,
                onToggleExpand = {},
                onAddPlan = {},
                onRenameTag = {},
                onDeleteTag = {},
                onOpenPlanDetail = { _, _ -> },
                onRenamePlan = { _, _ -> },
                onDeletePlan = {}
            )
        }
    }
}


