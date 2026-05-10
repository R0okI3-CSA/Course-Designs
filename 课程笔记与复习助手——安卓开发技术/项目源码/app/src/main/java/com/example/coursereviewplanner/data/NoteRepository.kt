package com.example.coursereviewplanner.data

import android.content.Context
import androidx.room.Room
import com.example.coursereviewplanner.data.local.AppDatabase
import com.example.coursereviewplanner.data.local.KnowledgePointEntity
import com.example.coursereviewplanner.data.local.NoteAnnotationEntity
import com.example.coursereviewplanner.data.local.NoteDao
import com.example.coursereviewplanner.data.local.NoteEntity
import com.example.coursereviewplanner.data.local.NoteTagEntity
import com.example.coursereviewplanner.data.local.NoteTagWithNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class NoteRepository(
    private val noteDao: NoteDao
) {

    suspend fun loadTagsWithNotes(userId: Long): List<NoteTagWithNotes> =
        withContext(Dispatchers.IO) {
            noteDao.getTagsWithNotesForUser(userId)
        }

    suspend fun loadUngroupedNotes(userId: Long): List<NoteEntity> =
        withContext(Dispatchers.IO) {
            noteDao.getUngroupedNotes(userId)
        }

    suspend fun createTag(userId: Long, name: String, color: Long): NoteTagEntity =
        withContext(Dispatchers.IO) {
            val tag = NoteTagEntity(
                userId = userId,
                name = name,
                color = color
            )
            val id = noteDao.insertTag(tag)
            tag.copy(id = id)
        }

    suspend fun renameTag(userId: Long, tagId: Long, newName: String) =
        withContext(Dispatchers.IO) {
            val existing = noteDao.getTagById(userId, tagId) ?: return@withContext
            noteDao.updateTag(existing.copy(name = newName))
        }

    suspend fun deleteTag(userId: Long, tagId: Long) =
        withContext(Dispatchers.IO) {
            val existing = noteDao.getTagById(userId, tagId) ?: return@withContext
            noteDao.deleteTag(existing)
        }

    suspend fun createNote(userId: Long, tagId: Long?, title: String): NoteEntity =
        withContext(Dispatchers.IO) {
            val now = System.currentTimeMillis()
            val note = NoteEntity(
                userId = userId,
                tagId = tagId,
                title = title,
                content = "",
                richContentJson = null,
                pageStyle = "LINES",
                createdAt = now,
                updatedAt = now
            )
            val id = noteDao.insertNote(note)
            note.copy(id = id)
        }

    suspend fun renameNote(userId: Long, noteId: Long, newTitle: String) =
        withContext(Dispatchers.IO) {
            val existing = noteDao.getNoteById(userId, noteId) ?: return@withContext
            noteDao.updateNote(
                existing.copy(
                    title = newTitle,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }

    suspend fun deleteNote(userId: Long, noteId: Long) =
        withContext(Dispatchers.IO) {
            val existing = noteDao.getNoteById(userId, noteId) ?: return@withContext
            noteDao.deleteNote(existing)
        }

    suspend fun getNote(userId: Long, noteId: Long): NoteEntity? =
        withContext(Dispatchers.IO) {
            noteDao.getNoteById(userId, noteId)
        }

    suspend fun updateNoteContent(
        userId: Long,
        noteId: Long,
        newTitle: String,
        newContent: String,
        newRichContentJson: String? = null,
        newPageStyle: String? = null
    ) = withContext(Dispatchers.IO) {
        val existing = noteDao.getNoteById(userId, noteId) ?: return@withContext
        val updated = existing.copy(
            title = newTitle,
            content = newContent,
            richContentJson = newRichContentJson ?: existing.richContentJson,
            pageStyle = newPageStyle ?: existing.pageStyle,
            updatedAt = System.currentTimeMillis()
        )
        noteDao.updateNote(updated)
    }

    // ----- 知识点与批注相关 -----

    data class NoteAnnotationWithPoint(
        val annotation: NoteAnnotationEntity,
        val point: KnowledgePointEntity,
        val totalRefs: Int
    )

    /**
     * 为某条笔记添加一条批注 + 知识点绑定：
     * - 若当前用户下已存在同名知识点，则复用；否则新建一个。
     */
    suspend fun addAnnotationToNote(
        userId: Long,
        noteId: Long,
        knowledgeTitle: String,
        knowledgeDescription: String,
        comment: String,
        indexCode: String,
        anchorIndex: Int = 0
    ): NoteAnnotationWithPoint = withContext(Dispatchers.IO) {
        val existingPoint =
            noteDao.getKnowledgePointByTitle(userId = userId, title = knowledgeTitle)
        val point = if (existingPoint != null) {
            existingPoint
        } else {
            val newPoint = KnowledgePointEntity(
                userId = userId,
                title = knowledgeTitle,
                description = knowledgeDescription
            )
            val id = noteDao.insertKnowledgePoint(newPoint)
            newPoint.copy(id = id)
        }
        val annotation = NoteAnnotationEntity(
            userId = userId,
            noteId = noteId,
            knowledgePointId = point.id,
            indexCode = indexCode,
            comment = comment,
            anchorIndex = anchorIndex
        )
        val annId = noteDao.insertAnnotation(annotation)
        val saved = annotation.copy(id = annId)
        val totalRefs = noteDao.countAnnotationsForKnowledgePoint(userId, point.id)
        NoteAnnotationWithPoint(annotation = saved, point = point, totalRefs = totalRefs)
    }

    suspend fun getAnnotationsForNote(
        userId: Long,
        noteId: Long
    ): List<NoteAnnotationWithPoint> = withContext(Dispatchers.IO) {
        val annotations = noteDao.getAnnotationsForNote(userId, noteId)
        if (annotations.isEmpty()) return@withContext emptyList<NoteAnnotationWithPoint>()
        val kpIds = annotations.map { it.knowledgePointId }.toSet()
        val allPoints = noteDao.getKnowledgePointsByUser(userId)
            .filter { it.id in kpIds }
            .associateBy { it.id }
        annotations.map { ann ->
            val point = allPoints[ann.knowledgePointId]
                ?: KnowledgePointEntity(id = ann.knowledgePointId, userId = userId, title = "知识点", description = "")
            val totalRefs = noteDao.countAnnotationsForKnowledgePoint(userId, ann.knowledgePointId)
            NoteAnnotationWithPoint(annotation = ann, point = point, totalRefs = totalRefs)
        }
    }

    suspend fun deleteAnnotation(
        userId: Long,
        annotationId: Long
    ) = withContext(Dispatchers.IO) {
        val target = noteDao.getAnnotationById(userId, annotationId) ?: return@withContext
        noteDao.deleteAnnotation(target)
    }

    data class KnowledgeReference(
        val noteId: Long,
        val noteTitle: String,
        val anchorIndex: Int
    )

    data class KnowledgeAnnotationBrief(
        val comment: String,
        val indexCode: String
    )

    suspend fun getReferencesForKnowledgePoint(
        userId: Long,
        knowledgePointId: Long
    ): List<KnowledgeReference> = withContext(Dispatchers.IO) {
        val anns = noteDao.getAnnotationsForKnowledgePoint(userId, knowledgePointId)
        if (anns.isEmpty()) return@withContext emptyList<KnowledgeReference>()
        val noteIds = anns.map { it.noteId }.toSet()
        val titles = mutableMapOf<Long, String>()
        for (id in noteIds) {
            val note = noteDao.getNoteById(userId, id)
            if (note != null) {
                titles[id] = note.title
            }
        }
        anns.map { ann ->
            KnowledgeReference(
                noteId = ann.noteId,
                noteTitle = titles[ann.noteId] ?: "笔记 ${ann.noteId}",
                anchorIndex = ann.anchorIndex
            )
        }
    }

    /**
     * 获取某个知识点在所有笔记中的批注内容与编号（用于知识导图）。
     */
    suspend fun getAnnotationBriefsForKnowledgePoint(
        userId: Long,
        knowledgePointId: Long
    ): List<KnowledgeAnnotationBrief> = withContext(Dispatchers.IO) {
        val anns = noteDao.getAnnotationsForKnowledgePoint(userId, knowledgePointId)
        if (anns.isEmpty()) return@withContext emptyList<KnowledgeAnnotationBrief>()
        anns.map { ann ->
            KnowledgeAnnotationBrief(
                comment = ann.comment,
                indexCode = ann.indexCode
            )
        }
    }

    /**
     * 获取当前用户下的所有笔记列表（包含有标签与未分组笔记）。
     * 用于云备份时一次性导出所有笔记数据。
     */
    suspend fun getAllNotesForUser(userId: Long): List<NoteEntity> =
        withContext(Dispatchers.IO) {
            val grouped = noteDao.getTagsWithNotesForUser(userId)
                .flatMap { it.notes }
            val ungrouped = noteDao.getUngroupedNotes(userId)
            (grouped + ungrouped).sortedBy { it.createdAt }
        }
}

object NoteRepositoryProvider {

    @Volatile
    private var repository: NoteRepository? = null

    fun get(context: Context): NoteRepository {
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

                NoteRepository(db.noteDao()).also { created ->
                    repository = created
                }
            }
        }
    }
}


