package com.example.coursereviewplanner.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent

object ReviewReminderScheduler {

    const val EXTRA_REMINDER_ID = "extra_reminder_id"
    const val EXTRA_TITLE = "extra_title"
    const val EXTRA_CONTENT = "extra_content"

    fun schedule(
        context: Context,
        reminderId: Long,
        triggerAtMillis: Long,
        title: String,
        content: String
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReviewReminderAlarmReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_ID, reminderId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_CONTENT, content)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // 为了兼容性与简化权限（避免 SCHEDULE_EXACT_ALARM），这里使用普通 set 定时，
        // 精度略有误差但足够满足课程大作业需求。
        alarmManager.set(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    fun cancel(context: Context, reminderId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReviewReminderAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            reminderId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}


