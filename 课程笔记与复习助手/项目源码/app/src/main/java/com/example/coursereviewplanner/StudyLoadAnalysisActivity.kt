@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursereviewplanner.ui.studyplan.DailyLoad
import com.example.coursereviewplanner.ui.studyplan.StudyLoadAnalysisViewModel
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

class StudyLoadAnalysisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val planId = intent.getLongExtra("planId", -1L)
        val planTitle = intent.getStringExtra("planTitle") ?: "学习计划"
        val customStartDay =
            if (intent.hasExtra("customStartDay")) intent.getLongExtra("customStartDay", -1L) else -1L
        val customEndDay =
            if (intent.hasExtra("customEndDay")) intent.getLongExtra("customEndDay", -1L) else -1L
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: StudyLoadAnalysisViewModel = viewModel()
                androidx.compose.runtime.LaunchedEffect(planId, customStartDay, customEndDay) {
                    val start = if (customStartDay > 0L) customStartDay else null
                    val end = if (customEndDay > 0L) customEndDay else null
                    vm.loadForPlan(planId, start, end)
                }
                StudyLoadAnalysisScreen(
                    title = "$planTitle 的学习压力分析",
                    viewModel = vm,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
private fun StudyLoadAnalysisScreen(
    title: String,
    viewModel: StudyLoadAnalysisViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState

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
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                            contentDescription = "返回"
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
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error
                )
                return@Column
            }

            if (state.loading) {
                Text(text = "正在分析学习压力…")
                return@Column
            }

            // 总体压力卡片
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "综合压力指数",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "${state.pressureScore} / 100",
                        style = MaterialTheme.typography.displaySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "根据每天学习时长（以 6 小时为满负荷）估算，仅供参考，建议合理安排作息。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 每天学习时长柱状图
            Text(
                text = "一周内每天学习任务与时长分布",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            DailyLoadChart(days = state.days)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "总学习时长：约 ${state.totalMinutes / 60} 小时 ${state.totalMinutes % 60} 分；" +
                    "活跃天平均每天约 ${"%.1f".format(state.averageMinutesPerDay / 60f)} 小时。",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun DailyLoadChart(days: List<DailyLoad>) {
    val maxMinutes = remember(days) { days.maxOfOrNull { it.minutes } ?: 0 }
    // 增大柱状图高度，让差异更明显，同时给文字留更大空间
    val maxBarHeight = 220.dp

    if (days.isEmpty() || maxMinutes <= 0) {
        Text(
            text = "暂时没有可统计的学习计划，请先在课程表或自定义模式下添加一些学习安排。",
            style = MaterialTheme.typography.bodySmall
        )
        return
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxBarHeight + 56.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        days.forEach { day ->
            val ratio = if (maxMinutes > 0) day.minutes.toFloat() / maxMinutes.toFloat() else 0f
            val barHeight = maxBarHeight * ratio

            Column(
                modifier = Modifier.width(40.dp),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(24.dp)
                        .height(barHeight)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = day.label.replace("周", "周\n"),
                    style = MaterialTheme.typography.labelSmall
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${day.tasks}项",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
        }
    }
}


