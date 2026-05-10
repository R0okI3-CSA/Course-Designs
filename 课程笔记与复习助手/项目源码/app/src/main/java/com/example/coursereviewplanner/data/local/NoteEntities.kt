package com.example.coursereviewplanner.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 笔记标签实体，用于对笔记进行分组。
 */
@Entity(
    tableName = "note_tags",
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
data class NoteTagEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val name: String,
    val color: Long,
    val sortOrder: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 笔记实体：可以选择归属某个标签，也可以是未分组笔记（tagId = null）。
 */
@Entity(
    tableName = "notes",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NoteTagEntity::class,
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
data class NoteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val tagId: Long?, // null 表示未分组
    val title: String,
    // 纯文本内容（向下兼容，方便全文检索等简单场景）
    val content: String = "",
    // 富文本内容的 JSON 表示，存储段落及其样式等信息
    val richContentJson: String? = null,
    // 页面背景样式（如空白、横线、网格），使用字符串保存枚举名
    val pageStyle: String = "LINES",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * 知识点实体：用于跨笔记复用的知识点概念（例如某个公式、定理、语法点）。
 */
@Entity(
    tableName = "knowledge_points",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["userId", "title"], unique = false)
    ]
)
data class KnowledgePointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val title: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * 笔记批注实体：一条笔记下的一条批注，绑定到某个知识点。
 * 简化实现：暂时只按 note 维度关联，不精确到具体字符位置。
 */
@Entity(
    tableName = "note_annotations",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = NoteEntity::class,
            parentColumns = ["id"],
            childColumns = ["noteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = KnowledgePointEntity::class,
            parentColumns = ["id"],
            childColumns = ["knowledgePointId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["noteId"]),
        Index(value = ["knowledgePointId"])
    ]
)
data class NoteAnnotationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val userId: Long,
    val noteId: Long,
    val knowledgePointId: Long,
    // 知识导图用编号，如 "1"、"1.2"、"1.2.3"
    val indexCode: String = "",
    val comment: String,
    // 简化：记录批注大致挂在第几个内容块/段落上，供“第几行”展示和后续跳转使用
    val anchorIndex: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)


