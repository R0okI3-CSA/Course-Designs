package com.example.coursereviewplanner.data

import android.content.Context
import androidx.room.Room
import com.example.coursereviewplanner.data.local.AppDatabase
import com.example.coursereviewplanner.data.local.ReviewReminderDao
import com.example.coursereviewplanner.data.local.ReviewReminderEntity
import com.example.coursereviewplanner.data.local.ReviewReminderTagEntity
import com.example.coursereviewplanner.data.local.ReviewReminderTagWithReminders
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReviewReminderRepository(
    private val dao: ReviewReminderDao
) {

    suspend fun loadTagsWithReminders(userId: Long): List<ReviewReminderTagWithReminders> =
        withContext(Dispatchers.IO) {
            dao.getTagsWithRemindersForUser(userId)
        }

    suspend fun loadUngroupedReminders(userId: Long): List<ReviewReminderEntity> =
        withContext(Dispatchers.IO) {
            dao.getUngroupedReminders(userId)
        }

    suspend fun createTag(userId: Long, name: String, color: Long): ReviewReminderTagEntity =
        withContext(Dispatchers.IO) {
            val tag = ReviewReminderTagEntity(
                userId = userId,
                name = name,
                color = color
            )
            val id = dao.insertTag(tag)
            tag.copy(id = id)
        }

    suspend fun renameTag(userId: Long, tagId: Long, newName: String) =
        withContext(Dispatchers.IO) {
            val existing = dao.getTagById(userId, tagId) ?: return@withContext
            dao.updateTag(existing.copy(name = newName))
        }

    suspend fun deleteTag(userId: Long, tagId: Long) =
        withContext(Dispatchers.IO) {
            val existing = dao.getTagById(userId, tagId) ?: return@withContext
            dao.deleteTag(existing)
        }

    suspend fun createReminder(
        userId: Long,
        tagId: Long?,
        title: String
    ): ReviewReminderEntity = withContext(Dispatchers.IO) {
        val now = System.currentTimeMillis()
        val reminder = ReviewReminderEntity(
            userId = userId,
            tagId = tagId,
            title = title,
            createdAt = now,
            updatedAt = now
        )
        val id = dao.insertReminder(reminder)
        reminder.copy(id = id)
    }

    suspend fun renameReminder(userId: Long, reminderId: Long, newTitle: String) =
        withContext(Dispatchers.IO) {
            val existing = dao.getReminderById(userId, reminderId) ?: return@withContext
            dao.updateReminder(
                existing.copy(
                    title = newTitle,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

    suspend fun deleteReminder(userId: Long, reminderId: Long) =
        withContext(Dispatchers.IO) {
            val existing = dao.getReminderById(userId, reminderId) ?: return@withContext
            dao.deleteReminder(existing)
        }

    suspend fun getReminder(userId: Long, reminderId: Long): ReviewReminderEntity? =
        withContext(Dispatchers.IO) {
            dao.getReminderById(userId, reminderId)
        }

    suspend fun updateReminderDetail(
        userId: Long,
        reminderId: Long,
        newTitle: String,
        newContent: String,
        newTimeMillis: Long?,
        isEnabled: Boolean
    ) = withContext(Dispatchers.IO) {
        val existing = dao.getReminderById(userId, reminderId) ?: return@withContext
        dao.updateReminder(
            existing.copy(
                title = newTitle,
                content = newContent,
                targetTime = newTimeMillis,
                isEnabled = isEnabled,
                updatedAt = System.currentTimeMillis()
            )
        )
    }

    /**
     * 获取当前用户下的所有复习提醒（包含有标签与未分组的提醒）。
     * 主要用于云备份场景。
     */
    suspend fun getAllRemindersForUser(userId: Long): List<ReviewReminderEntity> =
        withContext(Dispatchers.IO) {
            val grouped = dao.getTagsWithRemindersForUser(userId)
                .flatMap { it.reminders }
            val ungrouped = dao.getUngroupedReminders(userId)
            (grouped + ungrouped).sortedBy { it.createdAt }
        }
}

object ReviewReminderRepositoryProvider {

    @Volatile
    private var repository: ReviewReminderRepository? = null

    fun get(context: Context): ReviewReminderRepository {
        val appContext = context.applicationContext
        return repository ?: synchronized(this) {
            repository ?: run {
                val db = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "course_review_planner.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                ReviewReminderRepository(db.reviewReminderDao()).also { created ->
                    repository = created
                }
            }
        }
    }
}


