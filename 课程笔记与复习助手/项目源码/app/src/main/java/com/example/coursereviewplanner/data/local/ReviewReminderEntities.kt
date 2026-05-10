package com.example.coursereviewplanner.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 复习提醒标签，用于对提醒进行分组（例如：课程名称、考试名称）。
 */
@Entity(
    tableName = "review_reminder_tags",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"])
    ]
)
data class ReviewReminderTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val name: String,
    val color: Long,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 复习提醒实体：可以隶属于某个标签，也可以是未分组提醒（tagId = null）。
 * 为后续扩展预留时间字段。
 */
@Entity(
    tableName = "review_reminders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ReviewReminderTagEntity::class,
            parentColumns = ["id"],
            childColumns = ["tagId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["tagId"])
    ]
)
data class ReviewReminderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val tagId: Long?, // null 表示未分组
    val title: String,
    // 提醒内容/备注
    val content: String = "",
    // 提醒时间（毫秒时间戳），为 null 表示尚未设置具体时间
    val targetTime: Long? = null,
    // 是否激活提醒（开关）
    val isEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)


