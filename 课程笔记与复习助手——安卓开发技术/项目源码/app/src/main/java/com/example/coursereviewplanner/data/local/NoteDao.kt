package com.example.coursereviewplanner.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

data class NoteTagWithNotes(
    val tag: NoteTagEntity,
    val notes: List<NoteEntity>
)

@Dao
interface NoteDao {

    // 标签相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTag(tag: NoteTagEntity): Long

    @Update
    suspend fun updateTag(tag: NoteTagEntity)

    @Delete
    suspend fun deleteTag(tag: NoteTagEntity)

    @Query("SELECT * FROM note_tags WHERE userId = :userId ORDER BY sortOrder, createdAt")
    suspend fun getTagsByUser(userId: Long): List<NoteTagEntity>

    @Query("SELECT * FROM note_tags WHERE id = :tagId AND userId = :userId LIMIT 1")
    suspend fun getTagById(userId: Long, tagId: Long): NoteTagEntity?

    // 笔记相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteEntity): Long

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("SELECT * FROM notes WHERE userId = :userId AND tagId = :tagId ORDER BY createdAt DESC")
    suspend fun getNotesByTag(userId: Long, tagId: Long?): List<NoteEntity>

    @Query("SELECT * FROM notes WHERE userId = :userId AND id = :noteId LIMIT 1")
    suspend fun getNoteById(userId: Long, noteId: Long): NoteEntity?

    @Query("SELECT * FROM notes WHERE userId = :userId AND tagId IS NULL ORDER BY createdAt DESC")
    suspend fun getUngroupedNotes(userId: Long): List<NoteEntity>

    suspend fun getTagsWithNotesForUser(userId: Long): List<NoteTagWithNotes> {
        val tags = getTagsByUser(userId)
        return tags.map { tag ->
            val notes = getNotesByTag(userId, tag.id)
            NoteTagWithNotes(tag = tag, notes = notes)
        }
    }

    // 知识点相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertKnowledgePoint(point: KnowledgePointEntity): Long

    @Update
    suspend fun updateKnowledgePoint(point: KnowledgePointEntity)

    @Delete
    suspend fun deleteKnowledgePoint(point: KnowledgePointEntity)

    @Query("SELECT * FROM knowledge_points WHERE userId = :userId AND title = :title LIMIT 1")
    suspend fun getKnowledgePointByTitle(userId: Long, title: String): KnowledgePointEntity?

    @Query("SELECT * FROM knowledge_points WHERE userId = :userId ORDER BY createdAt DESC")
    suspend fun getKnowledgePointsByUser(userId: Long): List<KnowledgePointEntity>

    // 笔记批注相关
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnotation(annotation: NoteAnnotationEntity): Long

    @Update
    suspend fun updateAnnotation(annotation: NoteAnnotationEntity)

    @Delete
    suspend fun deleteAnnotation(annotation: NoteAnnotationEntity)

    @Query("SELECT * FROM note_annotations WHERE userId = :userId AND noteId = :noteId ORDER BY createdAt DESC")
    suspend fun getAnnotationsForNote(userId: Long, noteId: Long): List<NoteAnnotationEntity>

    @Query("SELECT COUNT(*) FROM note_annotations WHERE userId = :userId AND knowledgePointId = :kpId")
    suspend fun countAnnotationsForKnowledgePoint(userId: Long, kpId: Long): Int

    @Query("SELECT * FROM note_annotations WHERE userId = :userId AND knowledgePointId = :kpId ORDER BY createdAt DESC")
    suspend fun getAnnotationsForKnowledgePoint(userId: Long, kpId: Long): List<NoteAnnotationEntity>

    @Query("SELECT * FROM note_annotations WHERE userId = :userId AND id = :annotationId LIMIT 1")
    suspend fun getAnnotationById(userId: Long, annotationId: Long): NoteAnnotationEntity?
}


