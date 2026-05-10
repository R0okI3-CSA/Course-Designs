package com.example.coursereviewplanner.data

import android.content.Context
import androidx.room.Room
import com.example.coursereviewplanner.data.local.AppDatabase
import com.example.coursereviewplanner.data.local.StudyPlanDao
import com.example.coursereviewplanner.data.local.StudyPlanCustomEventEntity
import com.example.coursereviewplanner.data.local.StudyPlanEntity
import com.example.coursereviewplanner.data.local.StudyPlanSlotEntity
import com.example.coursereviewplanner.data.local.StudyPlanTagEntity
import com.example.coursereviewplanner.data.local.TagWithPlans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 学习计划仓库：封装标签与计划的增删改查逻辑。
 */
class StudyPlanRepository(
    private val studyPlanDao: StudyPlanDao
) {

    suspend fun loadTagsWithPlans(userId: Long): List<TagWithPlans> =
        withContext(Dispatchers.IO) {
            studyPlanDao.getTagsWithPlansForUser(userId)
        }

    suspend fun loadUngroupedPlans(userId: Long): List<StudyPlanEntity> =
        withContext(Dispatchers.IO) {
            studyPlanDao.getUngroupedPlans(userId)
        }

    suspend fun createTag(userId: Long, name: String, color: Long): StudyPlanTagEntity =
        withContext(Dispatchers.IO) {
            val tag = StudyPlanTagEntity(
                userId = userId,
                name = name,
                color = color
            )
            val id = studyPlanDao.insertTag(tag)
            tag.copy(id = id)
        }

    suspend fun renameTag(userId: Long, tagId: Long, newName: String) =
        withContext(Dispatchers.IO) {
            val existing = studyPlanDao.getTagById(userId, tagId) ?: return@withContext
            studyPlanDao.updateTag(existing.copy(name = newName))
        }

    suspend fun deleteTag(userId: Long, tagId: Long) =
        withContext(Dispatchers.IO) {
            val existing = studyPlanDao.getTagById(userId, tagId) ?: return@withContext
            studyPlanDao.deleteTag(existing)
        }

    suspend fun createPlan(userId: Long, tagId: Long?, title: String): StudyPlanEntity =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val plan = StudyPlanEntity(
                userId = userId,
                tagId = tagId,
                title = title,
                createdAt = now,
                updatedAt = now
            )
            val id = studyPlanDao.insertPlan(plan)
            plan.copy(id = id)
        }

    suspend fun renamePlan(userId: Long, planId: Long, newTitle: String) =
        withContext(Dispatchers.IO) {
            val existing = studyPlanDao.getPlanById(userId, planId) ?: return@withContext
            studyPlanDao.updatePlan(
                existing.copy(
                    title = newTitle,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

    suspend fun deletePlan(userId: Long, planId: Long) =
        withContext(Dispatchers.IO) {
            val existing = studyPlanDao.getPlanById(userId, planId) ?: return@withContext
            studyPlanDao.deletePlan(existing)
        }

    // ------- 课程表格子相关 -------

    suspend fun getSlotsForPlan(planId: Long): List<StudyPlanSlotEntity> =
        withContext(Dispatchers.IO) {
            studyPlanDao.getSlotsForPlan(planId)
        }

    suspend fun setSlot(
        userId: Long,
        planId: Long,
        rowIndex: Int,
        colIndex: Int,
        title: String,
        location: String,
        content: String
    ) = withContext(Dispatchers.IO) {
        // 若三项都为空，则视为清空该格子
        if (title.isBlank() && location.isBlank() && content.isBlank()) {
            studyPlanDao.clearSlot(planId, rowIndex, colIndex)
        } else {
            val slot = StudyPlanSlotEntity(
                id = 0L,
                planId = planId,
                userId = userId,
                rowIndex = rowIndex,
                colIndex = colIndex,
                title = title.ifBlank { null },
                location = location.ifBlank { null },
                content = content.ifBlank { null }
            )
            studyPlanDao.upsertSlot(slot)
        }
    }

    suspend fun clearSlot(planId: Long, rowIndex: Int, colIndex: Int) =
        withContext(Dispatchers.IO) {
            studyPlanDao.clearSlot(planId, rowIndex, colIndex)
        }

    suspend fun setSlotsBulk(
        userId: Long,
        planId: Long,
        cells: List<Pair<Int, Int>>,
        title: String,
        location: String,
        content: String
    ) = withContext(Dispatchers.IO) {
        cells.forEach { (row, col) ->
            setSlot(userId, planId, row, col, title, location, content)
        }
    }

    /**
     * 覆盖写入某个计划下的全部课程表格子：
     * - 先清空该计划下所有旧格子；
     * - 再按给定列表逐个写入。
     * 主要用于“从分享链接导入课表”的场景。
     */
    suspend fun overwriteSlotsForPlan(
        userId: Long,
        planId: Long,
        slots: List<ImportedSlot>
    ) = withContext(Dispatchers.IO) {
        studyPlanDao.clearAllSlotsForPlan(planId)
        slots.forEach { s ->
            val slot = StudyPlanSlotEntity(
                id = 0L,
                planId = planId,
                userId = userId,
                rowIndex = s.rowIndex,
                colIndex = s.colIndex,
                title = s.title.ifBlank { null },
                location = s.location.ifBlank { null },
                content = s.content.ifBlank { null }
            )
            studyPlanDao.upsertSlot(slot)
        }
    }

    // ------- 自定义模式事件相关 -------

    suspend fun getCustomEventsForRange(
        planId: Long,
        startDayEpoch: Long,
        endDayEpoch: Long
    ): List<StudyPlanCustomEventEntity> = withContext(Dispatchers.IO) {
        studyPlanDao.getCustomEventsForRange(planId, startDayEpoch, endDayEpoch)
    }

    suspend fun getCustomEventDateRange(planId: Long): Pair<Long, Long>? =
        withContext(Dispatchers.IO) {
            val first = studyPlanDao.getFirstCustomEventDay(planId)
            val last = studyPlanDao.getLastCustomEventDay(planId)
            if (first != null && last != null) first to last else null
        }

    suspend fun saveCustomEvent(
        userId: Long,
        planId: Long,
        eventId: Long?,
        dateEpochDay: Long,
        startMinutes: Int,
        endMinutes: Int,
        title: String,
        content: String
    ): StudyPlanCustomEventEntity = withContext(Dispatchers.IO) {
        val base = if (eventId != null && eventId > 0) {
            studyPlanDao.getCustomEventById(eventId)
                ?: StudyPlanCustomEventEntity(
                    id = 0L,
                    planId = planId,
                    userId = userId,
                    dateEpochDay = dateEpochDay,
                    startMinutes = startMinutes,
                    endMinutes = endMinutes,
                    title = title,
                    content = content
                )
        } else {
            StudyPlanCustomEventEntity(
                id = 0L,
                planId = planId,
                userId = userId,
                dateEpochDay = dateEpochDay,
                startMinutes = startMinutes,
                endMinutes = endMinutes,
                title = title,
                content = content
            )
        }
        val toSave = base.copy(
            dateEpochDay = dateEpochDay,
            startMinutes = startMinutes,
            endMinutes = endMinutes,
            title = title,
            content = content
        )
        val id = studyPlanDao.upsertCustomEvent(toSave)
        toSave.copy(id = if (toSave.id == 0L) id else toSave.id)
    }

    suspend fun deleteCustomEvent(eventId: Long) = withContext(Dispatchers.IO) {
        val exist = studyPlanDao.getCustomEventById(eventId) ?: return@withContext
        studyPlanDao.deleteCustomEvent(exist)
    }

    /**
     * 获取当前用户下的所有学习计划（包含有标签与未分组计划）。
     * 主要用于云备份场景。
     */
    suspend fun getAllPlansForUser(userId: Long): List<StudyPlanEntity> =
        withContext(Dispatchers.IO) {
            val grouped = studyPlanDao.getTagsWithPlansForUser(userId)
                .flatMap { it.plans }
            val ungrouped = studyPlanDao.getUngroupedPlans(userId)
            (grouped + ungrouped).sortedBy { it.createdAt }
        }

    /**
     * 覆盖写入某个计划下的全部自定义事件：
     * - 先清空该计划的所有自定义事件；
     * - 再按给定列表逐个写入。
     * 主要用于“从分享链接导入自定义计划”的场景。
     */
    suspend fun overwriteCustomEventsForPlan(
        userId: Long,
        planId: Long,
        events: List<ImportedCustomEvent>
    ) = withContext(Dispatchers.IO) {
        studyPlanDao.clearCustomEventsForPlan(planId)
        events.forEach { e ->
            val entity = StudyPlanCustomEventEntity(
                id = 0L,
                planId = planId,
                userId = userId,
                dateEpochDay = e.dateEpochDay,
                startMinutes = e.startMinutes,
                endMinutes = e.endMinutes,
                title = e.title,
                content = e.content
            )
            studyPlanDao.upsertCustomEvent(entity)
        }
    }
}

/**
 * 简单的导入用格子结构：用于从服务器分享的 JSON 中还原课程表。
 */
data class ImportedSlot(
    val rowIndex: Int,
    val colIndex: Int,
    val title: String,
    val location: String,
    val content: String
)

data class ImportedCustomEvent(
    val dateEpochDay: Long,
    val startMinutes: Int,
    val endMinutes: Int,
    val title: String,
    val content: String
)

/**
 * 简单的 StudyPlanRepository Provider。
 */
object StudyPlanRepositoryProvider {

    @Volatile
    private var repository: StudyPlanRepository? = null

    fun get(context: Context): StudyPlanRepository {
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

                StudyPlanRepository(db.studyPlanDao()).also { created ->
                    repository = created
                }
            }
        }
    }
}


