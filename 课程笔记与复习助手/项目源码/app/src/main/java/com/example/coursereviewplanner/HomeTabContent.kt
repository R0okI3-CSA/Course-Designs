package com.example.coursereviewplanner

import android.content.Intent
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun HomeTabContent(
    innerPadding: androidx.compose.foundation.layout.PaddingValues,
    todayPlanSummary: TodayPlanSummary?,
    remainingMinutesToday: Int?,
    lastNoteId: Long?,
    lastNoteTitle: String?,
    isLoadingNews: Boolean,
    newsList: List<com.example.coursereviewplanner.util.NewsArticle>,
    currentNewsIndex: Int,
    newsError: String?,
    onStudyPlanClick: () -> Unit,
    onNoteManagementClick: () -> Unit,
    onReviewReminderClick: () -> Unit,
    onShowHelp: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(innerPadding)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 今日新闻
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "今日新闻：",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                when {
                    isLoadingNews -> {
                        Text(
                            text = "加载中…",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                    newsList.isNotEmpty() -> {
                        val current = newsList[currentNewsIndex]
                        Text(
                            text = current.title,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    newsError != null -> {
                        Text(
                            text = newsError,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                    else -> {
                        Text(
                            text = "暂无新闻",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 今日计划卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(96.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "今日计划",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (todayPlanSummary == null) {
                    Text(
                        text = "今天暂时没有学习计划",
                        style = MaterialTheme.typography.bodySmall
                    )
                } else {
                    val hours = todayPlanSummary.totalMinutes / 60
                    val mins = todayPlanSummary.totalMinutes % 60
                    Text(
                        text = "共 ${todayPlanSummary.planCount} 项，约 ${hours} 小时 ${mins} 分",
                        style = MaterialTheme.typography.bodySmall
                    )
                    if (todayPlanSummary.titles.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = todayPlanSummary.titles.joinToString("、"),
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 今日剩余学习时间 + 上次打开的笔记
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 今日剩余学习时间
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "今日剩余学习时间",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val remain = remainingMinutesToday
                    if (remain == null || todayPlanSummary == null) {
                        Text(
                            text = "暂无学习安排",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        val h = remain / 60
                        val m = remain % 60
                        Text(
                            text = if (remain <= 0) "今日学习已完成或接近完成"
                            else "约 $h 小时 $m 分",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }

            // 上次打开的笔记
            Card(
                modifier = Modifier
                    .weight(1f)
                    .height(96.dp)
                    .clickable(enabled = lastNoteId != null) {
                        lastNoteId?.let { id ->
                            context.startActivity(
                                Intent(context, NoteDetailActivity::class.java).apply {
                                    putExtra("noteId", id)
                                }
                            )
                        }
                    },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "上次打开的笔记",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    if (lastNoteId == null || lastNoteTitle.isNullOrBlank()) {
                        Text(
                            text = "暂无记录",
                            style = MaterialTheme.typography.bodySmall
                        )
                    } else {
                        Text(
                            text = lastNoteTitle,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 四个主功能入口
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureSquareButton(
                label = "学习计划",
                icon = Icons.Filled.CalendarMonth,
                onClick = onStudyPlanClick
            )
            FeatureSquareButton(
                label = "笔记管理",
                icon = Icons.Filled.StickyNote2,
                onClick = onNoteManagementClick
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            FeatureSquareButton(
                label = "复习提醒",
                icon = Icons.Filled.Alarm,
                onClick = onReviewReminderClick
            )
            FeatureSquareButton(
                label = "使用说明",
                icon = Icons.Filled.MenuBook,
                onClick = onShowHelp
            )
        }
    }
}


