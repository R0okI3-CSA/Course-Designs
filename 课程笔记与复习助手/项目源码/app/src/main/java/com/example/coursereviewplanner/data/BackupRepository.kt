package com.example.coursereviewplanner.data

import android.content.Context
import com.example.coursereviewplanner.data.local.AppDatabase
import com.example.coursereviewplanner.data.local.NoteEntity
import com.example.coursereviewplanner.data.local.ReviewReminderEntity
import com.example.coursereviewplanner.data.local.StudyPlanEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 简单的云备份聚合仓库：
 * - 从现有的 Note / StudyPlan / ReviewReminder 仓库中拉取当前用户的核心数据，
 *   组装成一个 BackupPayload，供上传到自建服务器使用。
 * - 也提供从 BackupPayload 恢复到本地数据库的简易实现，用于“云同步”场景。
 */
data class BackupPayload(
    val notes: List<NoteEntity>,
    val studyPlans: List<StudyPlanEntity>,
    val reminders: List<ReviewReminderEntity>
)

class BackupRepository(
    private val noteRepository: NoteRepository,
    private val studyPlanRepository: StudyPlanRepository,
    private val reviewReminderRepository: ReviewReminderRepository
) {

    /**
     * 为指定用户构建一份备份快照。
     * 当前实现选择应用中最核心的三类数据：学习计划、笔记、复习提醒。
     */
    suspend fun buildPayloadForUser(userId: Long): BackupPayload = withContext(Dispatchers.IO) {
        val notes = noteRepository.getAllNotesForUser(userId)
        val plans = studyPlanRepository.getAllPlansForUser(userId)
        val reminders = reviewReminderRepository.getAllRemindersForUser(userId)
        BackupPayload(
            notes = notes,
            studyPlans = plans,
            reminders = reminders
        )
    }

    /**
     * 使用一份云端的 BackupPayload 覆盖本地当前用户的数据。
     *
     * 简化实现说明（符合课程实验需求）：
     * - 只同步三类核心实体：笔记、学习计划、复习提醒；
     * - 为避免外键约束问题，恢复时统一作为“未分组”数据处理（tagId = null）；
     * - 先删除当前用户的相关记录，再按 payload 内容重新创建。
     *   这样可以保证“云端多 -> 下载覆盖本地”的效果，逻辑也比较直观。
     */
    suspend fun restoreFromPayload(userId: Long, payload: BackupPayload) =
        withContext(Dispatchers.IO) {
            // 1. 删除当前用户本地已有的数据
            val existingNotes = noteRepository.getAllNotesForUser(userId)
            existingNotes.forEach { note ->
                noteRepository.deleteNote(userId, note.id)
            }

            val existingPlans = studyPlanRepository.getAllPlansForUser(userId)
            existingPlans.forEach { plan ->
                studyPlanRepository.deletePlan(userId, plan.id)
            }

            val existingReminders = reviewReminderRepository.getAllRemindersForUser(userId)
            existingReminders.forEach { reminder ->
                reviewReminderRepository.deleteReminder(userId, reminder.id)
            }

            // 2. 按 payload 重建数据（分组信息暂不恢复，均作为未分组数据）
            payload.notes.forEach { n ->
                val created = noteRepository.createNote(
                    userId = userId,
                    tagId = null, // 为避免外键问题，这里恢复为未分组
                    title = n.title
                )
                noteRepository.updateNoteContent(
                    userId = userId,
                    noteId = created.id,
                    newTitle = n.title,
                    newContent = n.content,
                    newRichContentJson = n.richContentJson,
                    newPageStyle = n.pageStyle
                )
            }

            payload.studyPlans.forEach { p ->
                // 学习计划目前主要包含标题与时间戳，这里重点恢复标题信息
                studyPlanRepository.createPlan(
                    userId = userId,
                    tagId = null,
                    title = p.title
                )
            }

            payload.reminders.forEach { r ->
                val created = reviewReminderRepository.createReminder(
                    userId = userId,
                    tagId = null,
                    title = r.title
                )
                reviewReminderRepository.updateReminderDetail(
                    userId = userId,
                    reminderId = created.id,
                    newTitle = r.title,
                    newContent = r.content,
                    newTimeMillis = r.targetTime,
                    isEnabled = r.isEnabled
                )
            }
        }
}

object BackupRepositoryProvider {

    @Volatile
    private var repository: BackupRepository? = null

    fun get(context: Context): BackupRepository {
        val appContext = context.applicationContext
        return repository ?: synchronized(this) {
            repository ?: run {
                // 直接复用各领域已有的 Repository 实现
                val noteRepo = NoteRepositoryProvider.get(appContext)
                val studyRepo = StudyPlanRepositoryProvider.get(appContext)
                val reminderRepo = ReviewReminderRepositoryProvider.get(appContext)
                BackupRepository(
                    noteRepository = noteRepo,
                    studyPlanRepository = studyRepo,
                    reviewReminderRepository = reminderRepo
                ).also { created ->
                    repository = created
                }
            }
        }
    }
}


