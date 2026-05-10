package com.example.coursereviewplanner.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.coursereviewplanner.MainActivity
import com.example.coursereviewplanner.R

class ReviewReminderAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val reminderId = intent.getLongExtra(ReviewReminderScheduler.EXTRA_REMINDER_ID, -1L)
        val title =
            intent.getStringExtra(ReviewReminderScheduler.EXTRA_TITLE) ?: "复习提醒"
        val content =
            intent.getStringExtra(ReviewReminderScheduler.EXTRA_CONTENT) ?: "该开始复习啦～"

        createChannelIfNeeded(context)

        // Android 13+ 需要动态申请通知权限，否则会抛出 SecurityException 导致崩溃。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.POST_NOTIFICATIONS
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            if (!granted) {
                // 没有权限就直接返回，不弹通知，以免应用崩溃
                return
            }
        }

        val clickIntent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            reminderId.toInt(),
            clickIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(reminderId.toInt(), notification)

        // 额外：若应用当前在前台，弹出一个对话框样式的提醒页面
        val alertIntent = Intent(context, com.example.coursereviewplanner.ReviewReminderAlertActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("title", title)
            putExtra("content", content)
        }
        context.startActivity(alertIntent)
    }

    private fun createChannelIfNeeded(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val existing = manager.getNotificationChannel(CHANNEL_ID)
            if (existing == null) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "复习提醒",
                    NotificationManager.IMPORTANCE_HIGH
                ).apply {
                    description = "用于课程复习提醒的通知"
                }
                manager.createNotificationChannel(channel)
            }
        }
    }

    companion object {
        const val CHANNEL_ID = "review_reminder_channel"
    }
}


