@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

/**
 * 当复习提醒触发且应用在前台时，用于弹出全局提示的 Activity。
 * 采用对话框样式，点击“我知道了”后关闭。
 */
class ReviewReminderAlertActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val title = intent.getStringExtra("title") ?: "复习提醒"
        val content = intent.getStringExtra("content") ?: "该开始复习啦～"

        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                AlertScreen(
                    title = title,
                    content = content,
                    onDismiss = { finish() }
                )
            }
        }
    }
}

@Composable
private fun AlertScreen(
    title: String,
    content: String,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = title, style = MaterialTheme.typography.titleLarge)
            },
            text = {
                Text(text = content, style = MaterialTheme.typography.bodyMedium)
            },
            confirmButton = {
                TextButton(onClick = onDismiss) {
                    Text("我知道了")
                }
            }
        )
    }
}


