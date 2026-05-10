@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursereviewplanner.ui.review.ReviewReminderDetailUiState
import com.example.coursereviewplanner.ui.review.ReviewReminderDetailViewModel
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

/**
 * 复习提醒详情界面：当前版本先留空，仅做占位。
 */
class ReviewReminderDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reminderId = intent.getLongExtra("reminderId", -1L)
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: ReviewReminderDetailViewModel = viewModel()
                LaunchedEffect(reminderId) {
                    vm.initialize(reminderId)
                }
                ReviewReminderDetailScreen(
                    state = vm.uiState,
                    onTitleChange = vm::onTitleChange,
                    onContentChange = vm::onContentChange,
                    onPickDate = { year, month, day -> vm.onDateSelected(year, month, day) },
                    onPickTime = { hour, minute -> vm.onTimeSelected(hour, minute) },
                    onEnabledChange = vm::onEnabledChange,
                    onSave = {
                        vm.saveReminder { rid, targetMillis, isOn ->
                            try {
                                if (targetMillis != null && isOn) {
                                    com.example.coursereviewplanner.util.ReviewReminderScheduler.schedule(
                                        this,
                                        rid,
                                        targetMillis,
                                        vm.uiState.title,
                                        vm.uiState.content
                                    )
                                } else {
                                    com.example.coursereviewplanner.util.ReviewReminderScheduler.cancel(
                                        this,
                                        rid
                                    )
                                }
                            } catch (_: Exception) {
                                // 避免由于闹钟/通知异常导致应用崩溃，后续可在此处增加日志或 Toast
                            }
                            finish()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ReviewReminderDetailScreen(
    state: ReviewReminderDetailUiState,
    onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit,
    onPickDate: (Int, Int, Int) -> Unit,
    onPickTime: (Int, Int) -> Unit,
    onEnabledChange: (Boolean) -> Unit,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "复习提醒详情",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = onTitleChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                label = { Text("标题（提醒名称）") }
            )

            OutlinedTextField(
                value = state.content,
                onValueChange = onContentChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                label = { Text("内容") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            val dateLabel = if (state.year != null && state.month != null && state.day != null) {
                "${state.year}-${"%02d".format(state.month)}-${"%02d".format(state.day)}"
            } else {
                "选择日期"
            }
            val timeLabel = if (state.hour != null && state.minute != null) {
                "%02d:%02d".format(state.hour, state.minute)
            } else {
                "选择时间"
            }

            TextButton(
                onClick = {
                    val cal = java.util.Calendar.getInstance()
                    val y = state.year ?: cal.get(java.util.Calendar.YEAR)
                    val m = (state.month ?: (cal.get(java.util.Calendar.MONTH) + 1)) - 1
                    val d = state.day ?: cal.get(java.util.Calendar.DAY_OF_MONTH)
                    DatePickerDialog(
                        (context as ComponentActivity),
                        { _, year, month, dayOfMonth ->
                            onPickDate(year, month + 1, dayOfMonth)
                        },
                        y,
                        m,
                        d
                    ).show()
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = dateLabel)
            }

            TextButton(
                onClick = {
                    val cal = java.util.Calendar.getInstance()
                    val h = state.hour ?: cal.get(java.util.Calendar.HOUR_OF_DAY)
                    val min = state.minute ?: cal.get(java.util.Calendar.MINUTE)
                    TimePickerDialog(
                        (context as ComponentActivity),
                        { _, hourOfDay, minute ->
                            onPickTime(hourOfDay, minute)
                        },
                        h,
                        min,
                        true
                    ).show()
                },
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(text = timeLabel)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "激活提醒")
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = state.isEnabled,
                    onCheckedChange = onEnabledChange
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = {
                    onSave()
                },
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .align(Alignment.End)
            ) {
                Text(text = "保存")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ReviewReminderDetailPreview() {
    CourseReviewPlannerTheme {
        ReviewReminderDetailScreen(
            state = ReviewReminderDetailUiState(),
            onTitleChange = {},
            onContentChange = {},
            onPickDate = { _, _, _ -> },
            onPickTime = { _, _ -> },
            onEnabledChange = {},
            onSave = {}
        )
    }
}


