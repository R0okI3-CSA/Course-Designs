package com.example.coursereviewplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

data class ReviewReminderTagWithReminders(
    val tag: ReviewReminderTagEntity,
    val reminders: List<ReviewReminderEntity>
)

@Dao
interface ReviewReminderDao {

    // 标签相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: ReviewReminderTagEntity): Long

    @Update
    suspend fun updateTag(tag: ReviewReminderTagEntity)

    @Delete
    suspend fun deleteTag(tag: ReviewReminderTagEntity)

    @Query("SELECT * FROM review_reminder_tags WHERE userId = :userId ORDER BY sortOrder, createdAt")
    suspend fun getTagsByUser(userId: Long): List<ReviewReminderTagEntity>

    @Query("SELECT * FROM review_reminder_tags WHERE id = :tagId AND userId = :userId LIMIT 1")
    suspend fun getTagById(userId: Long, tagId: Long): ReviewReminderTagEntity?

    // 提醒相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: ReviewReminderEntity): Long

    @Update
    suspend fun updateReminder(reminder: ReviewReminderEntity)

    @Delete
    suspend fun deleteReminder(reminder: ReviewReminderEntity)

    @Query("SELECT * FROM review_reminders WHERE userId = :userId AND tagId = :tagId ORDER BY createdAt DESC")
    suspend fun getRemindersByTag(userId: Long, tagId: Long?): List<ReviewReminderEntity>

    @Query("SELECT * FROM review_reminders WHERE userId = :userId AND id = :reminderId LIMIT 1")
    suspend fun getReminderById(userId: Long, reminderId: Long): ReviewReminderEntity?

    @Query("SELECT * FROM review_reminders WHERE userId = :userId AND tagId IS NULL ORDER BY createdAt DESC")
    suspend fun getUngroupedReminders(userId: Long): List<ReviewReminderEntity>

    suspend fun getTagsWithRemindersForUser(userId: Long): List<ReviewReminderTagWithReminders> {
        val tags = getTagsByUser(userId)
        return tags.map { tag ->
            val reminders = getRemindersByTag(userId, tag.id)
            ReviewReminderTagWithReminders(tag = tag, reminders = reminders)
        }
    }
}


