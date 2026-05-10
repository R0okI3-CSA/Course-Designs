package com.example.coursereviewplanner.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * 应用主数据库：
 * - 当前包含：用户表、学习计划标签表、学习计划表、课程表格子表、笔记与笔记标签表
 */
@Database(
    entities = [
        UserEntity::class,
        StudyPlanTagEntity::class,
        StudyPlanEntity::class,
        StudyPlanSlotEntity::class,
        StudyPlanCustomEventEntity::class,
        NoteTagEntity::class,
        NoteEntity::class,
        KnowledgePointEntity::class,
        NoteAnnotationEntity::class,
        ReviewReminderTagEntity::class,
        ReviewReminderEntity::class,
    ],
    version = 11,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun studyPlanDao(): StudyPlanDao
    abstract fun noteDao(): NoteDao
    abstract fun reviewReminderDao(): ReviewReminderDao
}


