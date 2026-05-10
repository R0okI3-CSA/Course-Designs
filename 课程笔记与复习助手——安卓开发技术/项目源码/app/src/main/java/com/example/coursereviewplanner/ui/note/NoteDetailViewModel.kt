package com.example.coursereviewplanner.ui.note

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.NoteRepositoryProvider
import com.example.coursereviewplanner.data.UserRepositoryProvider
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class PageStyle {
    PLAIN,
    LINES,
    GRID
}

data class TextFormat(
    // "Hei"=黑体, "Song"=宋体, "Kai"=楷体
    val fontFamily: String = "Hei",
    val fontSizeSp: Int = 16,
    val colorArgb: Long = 0xFF000000,
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underline: Boolean = false
)

sealed class NoteBlock {
    data class TextBlock(
        val text: String,
        val format: TextFormat
    ) : NoteBlock()

    data class ImageBlock(
        val uri: String
    ) : NoteBlock()

    data class AudioBlock(
        val uri: String
    ) : NoteBlock()
}

data class DoodlePoint(
    val x: Float,
    val y: Float
)

data class DoodleStroke(
    val points: List<DoodlePoint>,
    val colorArgb: Long,
    val width: Float
)

data class NoteDetailUiState(
    val noteId: Long = -1L,
    val title: String = "",
    // 段落级简易富文本：已经固化的内容块（文本块 / 图片块等）
    val segments: List<NoteBlock> = emptyList(),
    val currentInput: String = "",
    val pageStyle: PageStyle = PageStyle.LINES,
    val textFormat: TextFormat = TextFormat(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // 笔记下的所有批注（含知识点信息与引用次数）
    val annotations: List<NoteAnnotationItem> = emptyList(),
    val isAnnotationDialogVisible: Boolean = false,
    val annotationIndexCode: String = "",
    val annotationKnowledgeTitle: String = "",
    val annotationKnowledgeDescription: String = "",
    val annotationComment: String = "",
    val isAnnotationDetailVisible: Boolean = false,
    val selectedAnnotation: NoteAnnotationItem? = null,
    val annotationReferences: List<KnowledgeReferenceItem> = emptyList(),
    // 知识导图用的所有批注（跨笔记，同一知识点）
    val mindMapEntries: List<MindMapEntry> = emptyList(),
    // 画笔/涂鸦相关
    val doodleStrokes: List<DoodleStroke> = emptyList(),
    val isDrawingMode: Boolean = false,
    val doodleColorArgb: Long = 0xFF2196F3,
    val doodleStrokeWidth: Float = 8f,
    val isEraserMode: Boolean = false,
    val eraserSize: Float = 24f
)

data class NoteAnnotationItem(
    val id: Long,
    val knowledgePointId: Long,
    val knowledgeTitle: String,
    val knowledgeDescription: String,
    val comment: String,
    val indexCode: String,
    val totalRefs: Int
)

data class KnowledgeReferenceItem(
    val noteId: Long,
    val noteTitle: String,
    val anchorIndex: Int
)

data class MindMapEntry(
    val indexCode: String,
    val comment: String
)

class NoteDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val noteRepository = NoteRepositoryProvider.get(application)

    var uiState by mutableStateOf(NoteDetailUiState())
        private set

    private var saveJob: Job? = null

    fun initialize(noteId: Long) {
        if (uiState.noteId == noteId && uiState.noteId > 0) return
        if (noteId <= 0) {
            uiState = uiState.copy(
                noteId = noteId,
                isLoading = false,
                errorMessage = "无效的笔记 ID"
            )
            return
        }
        uiState = uiState.copy(noteId = noteId, isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first()
            if (userId == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "当前未登录，请重新登录后再试"
                )
                return@launch
            }
            val note = noteRepository.getNote(userId, noteId)
            if (note == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "笔记不存在或已被删除"
                )
            } else {
                // 优先从 richContentJson 恢复富文本段落和页面样式
                val (segmentsFromJson, pageStyleFromJson, doodlesFromJson) =
                    parseRichContentJson(note.richContentJson)
                val segments = if (segmentsFromJson.isNotEmpty()) {
                    segmentsFromJson
                } else if (note.content.isNotEmpty()) {
                    // 向下兼容：如果还没有富文本 JSON，只用纯文本构造一个默认段落
                    listOf(NoteBlock.TextBlock(text = note.content, format = TextFormat()))
                } else {
                    emptyList()
                }
                // 加载该笔记下的所有批注 + 知识点信息
                val annWithPoints = noteRepository.getAnnotationsForNote(userId, noteId)
                val annotationItems = annWithPoints.map {
                    NoteAnnotationItem(
                        id = it.annotation.id,
                        knowledgePointId = it.annotation.knowledgePointId,
                        knowledgeTitle = it.point.title,
                        knowledgeDescription = it.point.description,
                        comment = it.annotation.comment,
                        indexCode = it.annotation.indexCode,
                        totalRefs = it.totalRefs
                    )
                }
                uiState = uiState.copy(
                    isLoading = false,
                    title = note.title,
                    segments = segments,
                    currentInput = "",
                    pageStyle = pageStyleFromJson ?: PageStyle.LINES,
                    annotations = annotationItems,
                    doodleStrokes = doodlesFromJson
                )

                // 记录“上次打开的笔记”，供主界面展示
                UserRepositoryProvider.get(getApplication()).recordLastOpenedNote(
                    noteId = noteId,
                    title = note.title
                )
            }
        }
    }

    fun onPageStyleChange(style: PageStyle) {
        uiState = uiState.copy(pageStyle = style)
    }

    fun onTitleChange(newTitle: String) {
        uiState = uiState.copy(title = newTitle)
        scheduleSave()
    }

    /**
     * 在当前光标位置插入一张图片（作为独立块）。
     * 会先把当前输入文本固化为一个文本块，再追加图片块。
     */
    fun onInsertImage(uri: String) {
        if (uri.isBlank()) return
        val oldInput = uiState.currentInput
        val oldFormat = uiState.textFormat
        var blocks = uiState.segments
        if (oldInput.isNotEmpty()) {
            blocks = blocks + NoteBlock.TextBlock(text = oldInput, format = oldFormat)
        }
        blocks = blocks + NoteBlock.ImageBlock(uri = uri)
        uiState = uiState.copy(segments = blocks, currentInput = "")
        scheduleSave()
    }

    /**
     * 在当前光标位置插入一条录音（作为独立块）。
     */
    fun onInsertAudio(uri: String) {
        if (uri.isBlank()) return
        val oldInput = uiState.currentInput
        val oldFormat = uiState.textFormat
        var blocks = uiState.segments
        if (oldInput.isNotEmpty()) {
            blocks = blocks + NoteBlock.TextBlock(text = oldInput, format = oldFormat)
        }
        blocks = blocks + NoteBlock.AudioBlock(uri = uri)
        uiState = uiState.copy(segments = blocks, currentInput = "")
        scheduleSave()
    }

    fun onDeleteAudio(index: Int) {
        if (index !in uiState.segments.indices) return
        val blocks = uiState.segments.toMutableList()
        if (blocks[index] is NoteBlock.AudioBlock) {
            blocks.removeAt(index)
            uiState = uiState.copy(segments = blocks)
            scheduleSave()
        }
    }

    fun onDeleteImage(index: Int) {
        if (index !in uiState.segments.indices) return
        val blocks = uiState.segments.toMutableList()
        if (blocks[index] is NoteBlock.ImageBlock) {
            blocks.removeAt(index)
            uiState = uiState.copy(segments = blocks)
            scheduleSave()
        }
    }

    fun onUpdateImage(index: Int, newUri: String) {
        if (index !in uiState.segments.indices || newUri.isBlank()) return
        val blocks = uiState.segments.toMutableList()
        val block = blocks[index]
        if (block is NoteBlock.ImageBlock) {
            blocks[index] = block.copy(uri = newUri)
            uiState = uiState.copy(segments = blocks)
            scheduleSave()
        }
    }

    fun onCurrentInputChange(newContent: String) {
        uiState = uiState.copy(currentInput = newContent)
        scheduleSave()
    }

    /**
     * 在指定位置插入或更新一个文本块，供「任意行编辑」和「在图片/录音上下插入文字」复用。
     */
    fun upsertTextBlockAt(index: Int, newText: String) {
        if (newText.isEmpty() && index !in uiState.segments.indices) return
        val blocks = uiState.segments.toMutableList()
        val existing = blocks.getOrNull(index)
        val format = (existing as? NoteBlock.TextBlock)?.format ?: uiState.textFormat
        if (existing is NoteBlock.TextBlock) {
            blocks[index] = existing.copy(text = newText)
        } else {
            blocks.add(index.coerceIn(0, blocks.size), NoteBlock.TextBlock(text = newText, format = format))
        }
        uiState = uiState.copy(segments = blocks)
        scheduleSave()
    }

    /**
     * 直接编辑某一段文本块的内容（用于在任意行点击后就地编辑）。
     */
    fun onEditTextBlock(index: Int, newText: String) {
        upsertTextBlockAt(index, newText)
    }

    fun onFormatChange(
        fontFamily: String? = null,
        fontSizeSp: Int? = null,
        colorArgb: Long? = null,
        bold: Boolean? = null,
        italic: Boolean? = null,
        underline: Boolean? = null
    ) {
        // 切换样式前，将当前输入内容固化为一个段落，使其保留原有样式
        val oldFormat = uiState.textFormat
        val oldInput = uiState.currentInput
        var newSegments = uiState.segments
        if (oldInput.isNotEmpty()) {
            newSegments = newSegments + NoteBlock.TextBlock(text = oldInput, format = oldFormat)
        }

        val tf = oldFormat
        val updatedFormat = tf.copy(
            fontFamily = fontFamily ?: tf.fontFamily,
            fontSizeSp = fontSizeSp ?: tf.fontSizeSp,
            colorArgb = colorArgb ?: tf.colorArgb,
            bold = bold ?: tf.bold,
            italic = italic ?: tf.italic,
            underline = underline ?: tf.underline
        )

        uiState = uiState.copy(
            segments = newSegments,
            currentInput = "",
            textFormat = updatedFormat
        )
        scheduleSave()
    }

    // ---- 批注相关 ----

    fun onOpenAnnotationDialog() {
        uiState = uiState.copy(
            isAnnotationDialogVisible = true,
            annotationKnowledgeTitle = "",
            annotationKnowledgeDescription = "",
            annotationComment = "",
            annotationIndexCode = ""
        )
    }

    fun onChangeAnnotationKnowledgeTitle(value: String) {
        uiState = uiState.copy(annotationKnowledgeTitle = value)
    }

    fun onChangeAnnotationKnowledgeDescription(value: String) {
        uiState = uiState.copy(annotationKnowledgeDescription = value)
    }

    fun onChangeAnnotationIndexCode(value: String) {
        uiState = uiState.copy(annotationIndexCode = value)
    }

    fun onChangeAnnotationComment(value: String) {
        uiState = uiState.copy(annotationComment = value)
    }

    fun onDismissAnnotationDialog() {
        uiState = uiState.copy(isAnnotationDialogVisible = false)
    }

    fun onConfirmAddAnnotation() {
        val noteId = uiState.noteId
        if (noteId <= 0) return
        val title = uiState.annotationKnowledgeTitle.trim()
        val comment = uiState.annotationComment.trim()
        val indexCode = uiState.annotationIndexCode.trim()
        if (title.isEmpty() || comment.isEmpty()) return
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            // 简单地把批注挂在当前内容块列表的最后一个索引上
            val anchorIndex = (uiState.segments.size - 1).coerceAtLeast(0)
            val res = noteRepository.addAnnotationToNote(
                userId = userId,
                noteId = noteId,
                knowledgeTitle = title,
                knowledgeDescription = uiState.annotationKnowledgeDescription,
                comment = comment,
                indexCode = indexCode,
                anchorIndex = anchorIndex
            )
            val newItem = NoteAnnotationItem(
                id = res.annotation.id,
                knowledgePointId = res.annotation.knowledgePointId,
                knowledgeTitle = res.point.title,
                knowledgeDescription = res.point.description,
                comment = res.annotation.comment,
                indexCode = res.annotation.indexCode,
                totalRefs = res.totalRefs
            )
            uiState = uiState.copy(
                isAnnotationDialogVisible = false,
                annotations = uiState.annotations + newItem,
                annotationKnowledgeTitle = "",
                annotationKnowledgeDescription = "",
                annotationComment = "",
                annotationIndexCode = ""
            )
        }
    }

    fun onDeleteAnnotation(itemId: Long) {
        val noteId = uiState.noteId
        if (noteId <= 0) return
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            noteRepository.deleteAnnotation(userId, itemId)
            uiState = uiState.copy(
                annotations = uiState.annotations.filterNot { it.id == itemId }
            )
        }
    }

    fun onOpenAnnotationDetail(item: NoteAnnotationItem) {
        val noteId = uiState.noteId
        if (noteId <= 0) return
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            val refs = noteRepository.getReferencesForKnowledgePoint(userId, item.knowledgePointId)
            val refItems = refs.map {
                KnowledgeReferenceItem(
                    noteId = it.noteId,
                    noteTitle = it.noteTitle,
                    anchorIndex = it.anchorIndex
                )
            }
            val briefs = noteRepository.getAnnotationBriefsForKnowledgePoint(userId, item.knowledgePointId)
            val mindEntries = briefs
                .filter { it.indexCode.isNotBlank() }
                .map {
                    MindMapEntry(
                        indexCode = it.indexCode,
                        comment = it.comment
                    )
                }
            uiState = uiState.copy(
                isAnnotationDetailVisible = true,
                selectedAnnotation = item,
                annotationReferences = refItems,
                mindMapEntries = mindEntries
            )
        }
    }

    fun onDismissAnnotationDetail() {
        uiState = uiState.copy(
            isAnnotationDetailVisible = false,
            selectedAnnotation = null,
            annotationReferences = emptyList()
        )
    }

    private fun scheduleSave() {
        val noteId = uiState.noteId
        if (noteId <= 0) return
        saveJob?.cancel()
        saveJob = viewModelScope.launch {
            delay(500) // 简单防抖，避免高频写入数据库
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            // 将所有段落的纯文本 + 当前输入合并为一个整体内容，便于简单场景下展示/检索
            val mergedContent = buildString {
                uiState.segments.forEach { block ->
                    if (block is NoteBlock.TextBlock) {
                        append(block.text)
                    }
                }
                append(uiState.currentInput)
            }
            // 构建富文本 JSON，用于完整恢复段落及其样式
            val richJson = buildRichContentJson(
                segments = uiState.segments,
                currentInput = uiState.currentInput,
                currentFormat = uiState.textFormat,
                pageStyle = uiState.pageStyle,
                doodles = uiState.doodleStrokes
            )
            noteRepository.updateNoteContent(
                userId = userId,
                noteId = noteId,
                newTitle = uiState.title,
                newContent = mergedContent,
                newRichContentJson = richJson,
                newPageStyle = uiState.pageStyle.name
            )
        }
    }

    /**
     * 将当前段落列表 + 当前输入内容 + 页面样式序列化为 JSON 字符串，方便持久化存储。
     */
    private fun buildRichContentJson(
        segments: List<NoteBlock>,
        currentInput: String,
        currentFormat: TextFormat,
        pageStyle: PageStyle,
        doodles: List<DoodleStroke>
    ): String {
        val jsonSegments = JSONArray()
        segments.forEach { block ->
            val obj = JSONObject()
            when (block) {
                is NoteBlock.TextBlock -> {
                    obj.put("type", "text")
                    obj.put("text", block.text)
                    obj.put("format", formatToJson(block.format))
                }
                is NoteBlock.ImageBlock -> {
                    obj.put("type", "image")
                    obj.put("uri", block.uri)
                }
                is NoteBlock.AudioBlock -> {
                    obj.put("type", "audio")
                    obj.put("uri", block.uri)
                }
            }
            jsonSegments.put(obj)
        }
        if (currentInput.isNotEmpty()) {
            val obj = JSONObject()
            obj.put("text", currentInput)
            obj.put("format", formatToJson(currentFormat))
            jsonSegments.put(obj)
        }
        val root = JSONObject()
        root.put("segments", jsonSegments)
        root.put("pageStyle", pageStyle.name)

        // 涂鸦数据（简化版）：每一笔由若干点组成，记录颜色与粗细
        val doodleArr = JSONArray()
        doodles.forEach { stroke ->
            val strokeObj = JSONObject()
            strokeObj.put("colorArgb", stroke.colorArgb)
            strokeObj.put("width", stroke.width)
            val ptsArr = JSONArray()
            stroke.points.forEach { pt ->
                val p = JSONObject()
                p.put("x", pt.x)
                p.put("y", pt.y)
                ptsArr.put(p)
            }
            strokeObj.put("points", ptsArr)
            doodleArr.put(strokeObj)
        }
        root.put("doodles", doodleArr)

        return root.toString()
    }

    private fun formatToJson(format: TextFormat): JSONObject =
        JSONObject().apply {
            put("fontFamily", format.fontFamily)
            put("fontSizeSp", format.fontSizeSp)
            put("colorArgb", format.colorArgb)
            put("bold", format.bold)
            put("italic", format.italic)
            put("underline", format.underline)
        }

    /**
     * 从数据库中的 JSON 恢复段落及页面样式。
     * 解析失败时返回空列表和 null，以便走纯文本兼容逻辑。
     */
    private fun parseRichContentJson(json: String?): Triple<List<NoteBlock>, PageStyle?, List<DoodleStroke>> {
        if (json.isNullOrBlank()) return Triple(emptyList(), null, emptyList())
        return try {
            val root = JSONObject(json)
            val arr = root.optJSONArray("segments") ?: JSONArray()
            val segments = mutableListOf<NoteBlock>();
            for (i in 0 until arr.length()) {
                val segObj = arr.optJSONObject(i) ?: continue
                val type = segObj.optString("type", "text")
                when (type) {
                    "image" -> {
                        val uri = segObj.optString("uri", "")
                        if (uri.isNotBlank()) {
                            segments.add(NoteBlock.ImageBlock(uri = uri))
                        }
                    }
                    "audio" -> {
                        val uri = segObj.optString("uri", "")
                        if (uri.isNotBlank()) {
                            segments.add(NoteBlock.AudioBlock(uri = uri))
                        }
                    }
                    else -> {
                        val text = segObj.optString("text", "")
                        val fmtObj = segObj.optJSONObject("format")
                        val fmt = if (fmtObj != null) {
                            TextFormat(
                                fontFamily = fmtObj.optString("fontFamily", "Hei"),
                                fontSizeSp = fmtObj.optInt("fontSizeSp", 16),
                                colorArgb = fmtObj.optLong("colorArgb", 0xFF000000),
                                bold = fmtObj.optBoolean("bold", false),
                                italic = fmtObj.optBoolean("italic", false),
                                underline = fmtObj.optBoolean("underline", false)
                            )
                        } else {
                            TextFormat()
                        }
                        segments.add(NoteBlock.TextBlock(text = text, format = fmt))
                    }
                }
            }
            val styleName = root.optString("pageStyle", /* fallback */ "")
            val pageStyle = styleName?.let {
                runCatching { PageStyle.valueOf(it) }.getOrNull()
            }

            // 解析涂鸦数据
            val doodlesJson = root.optJSONArray("doodles") ?: JSONArray()
            val doodles = mutableListOf<DoodleStroke>()
            for (i in 0 until doodlesJson.length()) {
                val strokeObj = doodlesJson.optJSONObject(i) ?: continue
                val color = strokeObj.optLong("colorArgb", 0xFF2196F3)
                val width = strokeObj.optDouble("width", 8.0).toFloat()
                val ptsArr = strokeObj.optJSONArray("points") ?: JSONArray()
                val pts = mutableListOf<DoodlePoint>() 
                for (j in 0 until ptsArr.length()) {
                    val p = ptsArr.optJSONObject(j) ?: continue
                    val x = p.optDouble("x", 0.0).toFloat()
                    val y = p.optDouble("y", 0.0).toFloat()
                    pts.add(DoodlePoint(x, y))
                }
                if (pts.size > 1) {
                    doodles.add(DoodleStroke(points = pts, colorArgb = color, width = width))
                }
            }

            Triple(segments, pageStyle, doodles)
        } catch (_: Exception) {
            Triple(emptyList(), null, emptyList())
        }
    }

    // ---- 画笔 / 涂鸦相关 ----

    fun setDrawingMode(enabled: Boolean) {
        uiState = uiState.copy(
            isDrawingMode = enabled,
            // 开启画笔时自动关闭橡皮
            isEraserMode = if (enabled) false else uiState.isEraserMode
        )
    }

    fun setEraserMode(enabled: Boolean) {
        uiState = uiState.copy(
            isEraserMode = enabled,
            // 开启橡皮时关闭画笔
            isDrawingMode = if (enabled) false else uiState.isDrawingMode
        )
    }

    fun updateEraserSize(size: Float) {
        uiState = uiState.copy(eraserSize = size.coerceIn(4f, 48f))
    }

    fun updateDoodleConfig(colorArgb: Long, width: Float) {
        uiState = uiState.copy(
            doodleColorArgb = colorArgb,
            doodleStrokeWidth = width.coerceIn(2f, 24f)
        )
    }

    /**
     * 在当前画笔配置下，追加一笔新的涂鸦。
     * points 为相对于画布的坐标集合（单位：像素）。
     */
    fun addDoodleStroke(points: List<Pair<Float, Float>>) {
        if (points.size < 2) return
        val stroke = DoodleStroke(
            points = points.map { (x, y) -> DoodlePoint(x, y) },
            colorArgb = uiState.doodleColorArgb,
            width = uiState.doodleStrokeWidth
        )
        uiState = uiState.copy(doodleStrokes = uiState.doodleStrokes + stroke)
        scheduleSave()
    }

    /**
     * 按给定坐标附近擦除笔画：
     * - 将距离任意擦除点小于 halfSize 的整条笔画删除。
     * - 简化实现为“整笔删除”，避免复杂的曲线裁剪。
     */
    fun eraseAt(points: List<Pair<Float, Float>>, size: Float) {
        if (points.isEmpty() || uiState.doodleStrokes.isEmpty()) return
        val halfSize = size.coerceIn(4f, 48f)
        val sqRadius = halfSize * halfSize
        val newStrokes = uiState.doodleStrokes.filter { stroke ->
            // 若这条笔画上存在任意一点靠近擦除轨迹，则整条删除（filter 返回 false）
            val hit = stroke.points.any { p ->
                points.any { (x, y) ->
                    val dx = p.x - x
                    val dy = p.y - y
                    dx * dx + dy * dy <= sqRadius
                }
            }
            !hit
        }
        if (newStrokes.size != uiState.doodleStrokes.size) {
            uiState = uiState.copy(doodleStrokes = newStrokes)
            scheduleSave()
        }
    }
}


