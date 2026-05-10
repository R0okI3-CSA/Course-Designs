package com.example.coursereviewplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

data class TagWithPlans(
    val tag: StudyPlanTagEntity,
    val plans: List<StudyPlanEntity>
)

@Dao
interface StudyPlanDao {

    // 标签相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: StudyPlanTagEntity): Long

    @Update
    suspend fun updateTag(tag: StudyPlanTagEntity)

    @Delete
    suspend fun deleteTag(tag: StudyPlanTagEntity)

    @Query("SELECT * FROM study_plan_tags WHERE userId = :userId ORDER BY sortOrder, createdAt")
    suspend fun getTagsByUser(userId: Long): List<StudyPlanTagEntity>

    @Query("SELECT * FROM study_plan_tags WHERE id = :tagId AND userId = :userId LIMIT 1")
    suspend fun getTagById(userId: Long, tagId: Long): StudyPlanTagEntity?

    // 计划相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlan(plan: StudyPlanEntity): Long

    @Update
    suspend fun updatePlan(plan: StudyPlanEntity)

    @Delete
    suspend fun deletePlan(plan: StudyPlanEntity)

    @Query("SELECT * FROM study_plans WHERE userId = :userId AND tagId = :tagId ORDER BY createdAt")
    suspend fun getPlansByTag(userId: Long, tagId: Long?): List<StudyPlanEntity>

    @Query("SELECT * FROM study_plans WHERE userId = :userId AND id = :planId LIMIT 1")
    suspend fun getPlanById(userId: Long, planId: Long): StudyPlanEntity?

    // 将标签与其计划一起返回，方便界面展示
    @Transaction
    suspend fun getTagsWithPlansForUser(userId: Long): List<TagWithPlans> {
        val tags = getTagsByUser(userId)
        return tags.map { tag ->
            val plans = getPlansByTag(userId, tag.id)
            TagWithPlans(tag = tag, plans = plans)
        }
    }

    // 课程表格子相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSlot(slot: StudyPlanSlotEntity)

    @Query("DELETE FROM study_plan_slots WHERE planId = :planId")
    suspend fun clearAllSlotsForPlan(planId: Long)

    @Query("DELETE FROM study_plan_slots WHERE planId = :planId AND rowIndex = :rowIndex AND colIndex = :colIndex")
    suspend fun clearSlot(planId: Long, rowIndex: Int, colIndex: Int)

    @Query("SELECT * FROM study_plan_slots WHERE planId = :planId")
    suspend fun getSlotsForPlan(planId: Long): List<StudyPlanSlotEntity>

    @Query("SELECT * FROM study_plans WHERE userId = :userId AND tagId IS NULL ORDER BY createdAt")
    suspend fun getUngroupedPlans(userId: Long): List<StudyPlanEntity>

    // 自定义模式事件相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCustomEvent(event: StudyPlanCustomEventEntity): Long

    @Query("DELETE FROM study_plan_custom_events WHERE planId = :planId")
    suspend fun clearCustomEventsForPlan(planId: Long)

    @Delete
    suspend fun deleteCustomEvent(event: StudyPlanCustomEventEntity)

    @Query("SELECT * FROM study_plan_custom_events WHERE planId = :planId AND dateEpochDay BETWEEN :start AND :end ORDER BY dateEpochDay, startMinutes")
    suspend fun getCustomEventsForRange(planId: Long, start: Long, end: Long): List<StudyPlanCustomEventEntity>

    @Query("SELECT * FROM study_plan_custom_events WHERE id = :id LIMIT 1")
    suspend fun getCustomEventById(id: Long): StudyPlanCustomEventEntity?

    // 查询某个计划的自定义事件日期范围（用于决定是否默认进入自定义模式）
    @Query("SELECT dateEpochDay FROM study_plan_custom_events WHERE planId = :planId ORDER BY dateEpochDay ASC LIMIT 1")
    suspend fun getFirstCustomEventDay(planId: Long): Long?

    @Query("SELECT dateEpochDay FROM study_plan_custom_events WHERE planId = :planId ORDER BY dateEpochDay DESC LIMIT 1")
    suspend fun getLastCustomEventDay(planId: Long): Long?
}


