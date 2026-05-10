package com.example.coursereviewplanner.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 学习计划标签（类似“主题/文件夹”），用于对计划进行分组。
 */
@Entity(
    tableName = "study_plan_tags",
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
data class StudyPlanTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val name: String,
    val color: Long, // ARGB 颜色值
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 学习计划实体：属于某个标签（可为空，表示“未分组”）。
 */
@Entity(
    tableName = "study_plans",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = StudyPlanTagEntity::class,
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
data class StudyPlanEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val tagId: Long?, // 可以为 null，表示未分配到任何标签
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 课程表模式下的单个格子（固定时间段）。
 * 使用 (planId, rowIndex, colIndex) 作为联合主键，一格最多一条记录。
 */
@Entity(
    tableName = "study_plan_slots",
    foreignKeys = [
        ForeignKey(
            entity = StudyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["planId"]),
        Index(value = ["userId"]),
        Index(value = ["planId", "rowIndex", "colIndex"], unique = true)
    ]
)
data class StudyPlanSlotEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val planId: Long,
    val userId: Long,
    val rowIndex: Int,
    val colIndex: Int,
    val title: String? = null,
    val location: String? = null,
    val content: String? = null
)

/**
 * 自定义时间段模式下的计划事件：
 * - 每条记录对应某一天（dateEpochDay）上的一个时间段 [startMinutes, endMinutes)。
 */
@Entity(
    tableName = "study_plan_custom_events",
    foreignKeys = [
        ForeignKey(
            entity = StudyPlanEntity::class,
            parentColumns = ["id"],
            childColumns = ["planId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["planId"]),
        Index(value = ["userId"]),
        Index(value = ["planId", "dateEpochDay"])
    ]
)
data class StudyPlanCustomEventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val planId: Long,
    val userId: Long,
    val dateEpochDay: Long, // LocalDate.toEpochDay()
    val startMinutes: Int,  // 从 0:00 开始的分钟数
    val endMinutes: Int,
    val title: String,
    val content: String
)



