@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.Path
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Subject
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.coursereviewplanner.ui.note.NoteBlock
import com.example.coursereviewplanner.ui.note.NoteDetailUiState
import com.example.coursereviewplanner.ui.note.NoteDetailViewModel
import com.example.coursereviewplanner.ui.note.PageStyle
import com.example.coursereviewplanner.ui.note.TextFormat
import com.example.coursereviewplanner.ui.note.DoodleStroke
import com.example.coursereviewplanner.ui.note.MindMapEntry
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput

class NoteDetailActivity : ComponentActivity() {

    private lateinit var noteViewModel: NoteDetailViewModel

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                val uri = saveBitmapToInternal(this, bitmap)
                noteViewModel.onInsertImage(uri.toString())
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                noteViewModel.onInsertImage(uri.toString())
            }
        }

    private var mediaRecorder: android.media.MediaRecorder? = null
    private var currentAudioFile: File? = null

    private val recordAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                // 首次授权后自动开始录音，避免第一次点“开始录音”没有真正录到
                startRecordingInternal()
            } else {
                Toast.makeText(this, "录音权限被拒绝，无法开始录音", Toast.LENGTH_SHORT).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val noteId = intent.getLongExtra("noteId", -1L)
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: NoteDetailViewModel = viewModel()
                noteViewModel = vm
                LaunchedEffect(noteId) {
                    vm.initialize(noteId)
                }
                NoteDetailScreen(
                    viewModel = vm,
                    onBack = { finish() },
                    onRequestInsertImageFromCamera = { takePictureLauncher.launch(null) },
                    onRequestInsertImageFromGallery = { pickImageLauncher.launch("image/*") },
                    onRequestStartRecording = { ensureRecordPermissionThenStart() },
                    onRequestStopRecordingAndSave = { stopRecordingAndInsert() },
                    onRequestDeleteImage = { index ->
                        vm.onDeleteImage(index)
                    },
                    onNavigateToNote = { targetNoteId ->
                        if (targetNoteId > 0) {
                            startActivity(
                                Intent(this, NoteDetailActivity::class.java).apply {
                                    putExtra("noteId", targetNoteId)
                                }
                            )
                        }
                    }
                )
            }
        }
    }

    private fun ensureRecordPermissionThenStart() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) {
            startRecordingInternal()
        } else {
            recordAudioPermissionLauncher.launch(android.Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun startRecordingInternal() {
        if (mediaRecorder != null) return
        val file = File(filesDir, "note_audio_${System.currentTimeMillis()}.m4a")
        currentAudioFile = file
        val recorder = android.media.MediaRecorder().apply {
            setAudioSource(android.media.MediaRecorder.AudioSource.MIC)
            setOutputFormat(android.media.MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AAC)
            setOutputFile(file.absolutePath)
            prepare()
            start()
        }
        mediaRecorder = recorder
    }

    private fun stopRecordingAndInsert() {
        val recorder = mediaRecorder ?: return
        try {
            recorder.stop()
        } catch (_: Exception) {
        } finally {
            recorder.release()
            mediaRecorder = null
        }
        val file = currentAudioFile
        currentAudioFile = null
        if (file != null) {
            val uri = Uri.fromFile(file)
            noteViewModel.onInsertAudio(uri.toString())
        }
    }
}

private fun saveBitmapToInternal(context: Context, bitmap: Bitmap): Uri {
    val fileName = "note_img_${System.currentTimeMillis()}.jpg"
    val file = File(context.filesDir, fileName)
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
    }
    return Uri.fromFile(file)
}

@Composable
fun NoteDetailScreen(
    viewModel: NoteDetailViewModel,
    onBack: () -> Unit,
    onRequestInsertImageFromCamera: () -> Unit,
    onRequestInsertImageFromGallery: () -> Unit,
    onRequestStartRecording: () -> Unit,
    onRequestStopRecordingAndSave: () -> Unit,
    onRequestDeleteImage: (index: Int) -> Unit,
    onNavigateToNote: (Long) -> Unit
) {
    val state = viewModel.uiState
    val context = LocalContext.current
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showImageActionDialog by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf<Int?>(null) }
    var selectedImageUri by remember { mutableStateOf<String?>(null) }
    var showRecordDialog by remember { mutableStateOf(false) }
    var isRecording by remember { mutableStateOf(false) }
    var recordSeconds by remember { mutableStateOf(0) }
    var showPenDialog by remember { mutableStateOf(false) }
    var showEraserDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onBack) {
                    Text(text = "返回", color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = state.title.ifBlank { "未命名笔记" },
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 工具栏：页面格式 / 文本编辑 / 批注 / 图片 / 录音
            NoteToolbar(
                state = state,
                onPageStyleChange = viewModel::onPageStyleChange,
                onFormatChange = viewModel::onFormatChange,
                onClickImage = { showImageSourceDialog = true },
                onClickRecord = { showRecordDialog = true },
                onClickAnnotation = { viewModel.onOpenAnnotationDialog() },
                onClickPen = { showPenDialog = true },
                onClickEraser = { showEraserDialog = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            val scrollState = rememberScrollState()
            val focusRequester = remember { FocusRequester() }
            var editingIndex by remember { mutableStateOf<Int?>(null) }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .drawNoteBackground(style = state.pageStyle)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // 已固化的内容块：文本块 / 图片块 / 录音块
                    state.segments.forEachIndexed { index, block ->
                        when (block) {
                            is NoteBlock.TextBlock -> {
                                if (editingIndex == index) {
                                    val localText = remember(block.text) { mutableStateOf(block.text) }
                                    BasicTextField(
                                        value = localText.value,
                                        onValueChange = {
                                            localText.value = it
                                            viewModel.onEditTextBlock(index, it)
                                        },
                                        textStyle = block.format.toTextStyle(),
                                        cursorBrush = SolidColor(Color(block.format.colorArgb)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .focusRequester(focusRequester)
                                    )
                                } else {
                                    Text(
                                        text = block.text,
                                        style = block.format.toTextStyle(),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                editingIndex = index
                                                focusRequester.requestFocus()
                                            }
                                    )
                                }
                            }

                            is NoteBlock.ImageBlock -> {
                                // 图片上方若没有文字，则插入一行可编辑文字
                                if (index == 0 || state.segments.getOrNull(index - 1) !is NoteBlock.TextBlock) {
                                    var aboveText by remember { mutableStateOf("") }
                                    BasicTextField(
                                        value = aboveText,
                                        onValueChange = {
                                            aboveText = it
                                            viewModel.upsertTextBlockAt(index, it)
                                        },
                                        textStyle = state.textFormat.toTextStyle(),
                                        cursorBrush = SolidColor(Color(state.textFormat.colorArgb)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                AsyncImage(
                                    model = block.uri,
                                    contentDescription = "插入的图片",
                                    contentScale = ContentScale.FillWidth,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 160.dp)
                                        .clickable {
                                            selectedImageIndex = index
                                            selectedImageUri = block.uri
                                            showImageActionDialog = true
                                        }
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // 图片下方若没有文字，则插入一行可编辑文字
                                val nextIndex = index + 1
                                if (nextIndex >= state.segments.size || state.segments.getOrNull(nextIndex) !is NoteBlock.TextBlock) {
                                    var belowText by remember { mutableStateOf("") }
                                    BasicTextField(
                                        value = belowText,
                                        onValueChange = {
                                            belowText = it
                                            viewModel.upsertTextBlockAt(nextIndex, it)
                                        },
                                        textStyle = state.textFormat.toTextStyle(),
                                        cursorBrush = SolidColor(Color(state.textFormat.colorArgb)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                            is NoteBlock.AudioBlock -> {
                                // 录音上方若没有文字，则插入一行可编辑文字
                                if (index == 0 || state.segments.getOrNull(index - 1) !is NoteBlock.TextBlock) {
                                    var aboveText by remember { mutableStateOf("") }
                                    BasicTextField(
                                        value = aboveText,
                                        onValueChange = {
                                            aboveText = it
                                            viewModel.upsertTextBlockAt(index, it)
                                        },
                                        textStyle = state.textFormat.toTextStyle(),
                                        cursorBrush = SolidColor(Color(state.textFormat.colorArgb)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                AudioBlockPlayer(
                                    uri = block.uri,
                                    onDelete = { viewModel.onDeleteAudio(index) }
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                // 录音下方若没有文字，则插入一行可编辑文字
                                val nextIndex = index + 1
                                if (nextIndex >= state.segments.size || state.segments.getOrNull(nextIndex) !is NoteBlock.TextBlock) {
                                    var belowText by remember { mutableStateOf("") }
                                    BasicTextField(
                                        value = belowText,
                                        onValueChange = {
                                            belowText = it
                                            viewModel.upsertTextBlockAt(nextIndex, it)
                                        },
                                        textStyle = state.textFormat.toTextStyle(),
                                        cursorBrush = SolidColor(Color(state.textFormat.colorArgb)),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }

                    // 当前笔记下的所有批注列表（知识点关联展示）
                    if (state.annotations.isNotEmpty()) {
                        Text(
                            text = "批注 / 知识点关联",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        state.annotations.forEach { item ->
                            androidx.compose.material3.Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text(
                                        text = item.knowledgeTitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    if (item.knowledgeDescription.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = item.knowledgeDescription,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "批注：${item.comment}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "该知识点在其它笔记中的引用：约 ${item.totalRefs} 处",
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                        Row {
                                            TextButton(onClick = { viewModel.onOpenAnnotationDetail(item) }) {
                                                Text("详情")
                                            }
                                            TextButton(onClick = { viewModel.onDeleteAnnotation(item.id) }) {
                                                Text("删除批注")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 当前正在输入的内容，使用当前选中的样式（始终位于内容与批注之后）
                    BasicTextField(
                        value = state.currentInput,
                        onValueChange = viewModel::onCurrentInputChange,
                        textStyle = state.textFormat.toTextStyle(),
                        cursorBrush = SolidColor(Color(state.textFormat.colorArgb)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 画笔涂鸦区域：作为纸面的一部分，随页面一起滚动
                    DoodleCanvas(
                        strokes = state.doodleStrokes,
                        isDrawing = state.isDrawingMode,
                        isErasing = state.isEraserMode,
                        colorArgb = state.doodleColorArgb,
                        strokeWidth = state.doodleStrokeWidth,
                        eraserSize = state.eraserSize,
                        onStrokeFinished = { points ->
                            viewModel.addDoodleStroke(points)
                        },
                        onEraseAt = { pts ->
                            viewModel.eraseAt(pts, state.eraserSize)
                        }
                    )

                    // 追加一定空白高度，让页面仍然可以向下滑动
                    Spacer(modifier = Modifier.height(400.dp))
                }
            }
        }
    }

    // 选择插入图片来源：拍照 / 相册
    if (showImageSourceDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                Column {
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        onRequestInsertImageFromCamera()
                    }) {
                        Text("使用相机拍照")
                    }
                    TextButton(onClick = {
                        showImageSourceDialog = false
                        onRequestInsertImageFromGallery()
                    }) {
                        Text("从图库中选择")
                    }
                }
            }
        )
    }

    // 图片点击后的操作：删除
    if (showImageActionDialog && selectedImageIndex != null && selectedImageUri != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showImageActionDialog = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                Column {
                    TextButton(onClick = {
                        showImageActionDialog = false
                        onRequestDeleteImage(selectedImageIndex!!)
                    }) {
                        Text("删除")
                    }
                }
            }
        )
    }

    // 录音弹窗：开始 / 结束并保存
    if (showRecordDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showRecordDialog = false
                if (isRecording) {
                    onRequestStopRecordingAndSave()
                    isRecording = false
                    recordSeconds = 0
                }
            },
            confirmButton = {},
            dismissButton = {},
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val minutes = recordSeconds / 60
                    val seconds = recordSeconds % 60
                    val timeText = String.format("%02d:%02d", minutes, seconds)
                    Text(text = if (isRecording) "正在录音  $timeText" else "准备开始录音  00:00")
                    if (isRecording) {
                        LaunchedEffect(recordSeconds) {
                            kotlinx.coroutines.delay(1000)
                            recordSeconds += 1
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(onClick = {
                            if (!isRecording) {
                                onRequestStartRecording()
                                isRecording = true
                                recordSeconds = 0
                            } else {
                                onRequestStopRecordingAndSave()
                                isRecording = false
                                recordSeconds = 0
                                showRecordDialog = false
                            }
                        }) {
                            Text(if (!isRecording) "开始录音" else "结束并保存")
                        }
                        TextButton(onClick = {
                            if (isRecording) {
                                onRequestStopRecordingAndSave()
                                isRecording = false
                                recordSeconds = 0
                            }
                            showRecordDialog = false
                        }) {
                            Text("取消")
                        }
                    }
                }
            }
        )
    }

    // 画笔设置弹窗：颜色与粗细
    if (showPenDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showPenDialog = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                var tempColor by remember { mutableStateOf(state.doodleColorArgb) }
                var tempWidth by remember { mutableStateOf(state.doodleStrokeWidth) }
                Column {
                    Text(text = "画笔颜色与粗细设置")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "颜色")
                    Spacer(modifier = Modifier.height(4.dp))
                    Row {
                        val colors = listOf(
                            0xFF000000,
                            0xFFF44336,
                            0xFF2196F3,
                            0xFF4CAF50
                        )
                        colors.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .height(24.dp)
                                    .width(24.dp)
                                    .background(Color(c))
                                    .clickable { tempColor = c }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "粗细：${"%.1f".format(tempWidth)}")
                    Slider(
                        value = tempWidth,
                        onValueChange = { tempWidth = it.coerceIn(2f, 24f) },
                        valueRange = 2f..24f
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            viewModel.setDrawingMode(false)
                            showPenDialog = false
                        }) {
                            Text("关闭画笔")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            viewModel.updateDoodleConfig(tempColor, tempWidth)
                            viewModel.setDrawingMode(true)
                            showPenDialog = false
                        }) {
                            Text("开始涂鸦")
                        }
                    }
                }
            }
        )
    }

    // 橡皮设置弹窗：只设置擦除范围大小
    if (showEraserDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showEraserDialog = false },
            confirmButton = {},
            dismissButton = {},
            text = {
                var tempSize by remember { mutableStateOf(state.eraserSize) }
                Column {
                    Text(text = "橡皮大小设置")
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "大小：${"%.1f".format(tempSize)} 像素")
                    Slider(
                        value = tempSize,
                        onValueChange = { tempSize = it.coerceIn(4f, 48f) },
                        valueRange = 4f..48f
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = {
                            viewModel.setEraserMode(false)
                            showEraserDialog = false
                        }) {
                            Text("关闭橡皮")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = {
                            // 这里只更新 ViewModel 中的尺寸，同时开启橡皮模式
                            viewModel.updateEraserSize(tempSize)
                            viewModel.setEraserMode(true)
                            showEraserDialog = false
                        }) {
                            Text("开始擦除")
                        }
                    }
                }
            }
        )
    }

    // 批注弹窗：选择/新建知识点 + 填写批注内容
    if (state.isAnnotationDialogVisible) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onDismissAnnotationDialog() },
            confirmButton = {},
            dismissButton = {},
            text = {
                Column {
                    Text(text = "添加批注并关联知识点")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "知识点标题")
                    BasicTextField(
                        value = state.annotationKnowledgeTitle,
                        onValueChange = viewModel::onChangeAnnotationKnowledgeTitle,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "知识点说明（可选）")
                    BasicTextField(
                        value = state.annotationKnowledgeDescription,
                        onValueChange = viewModel::onChangeAnnotationKnowledgeDescription,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "编号（用于知识导图，如 1、1.2、1.2.3，可选）")
                    BasicTextField(
                        value = state.annotationIndexCode,
                        onValueChange = viewModel::onChangeAnnotationIndexCode,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "批注内容")
                    BasicTextField(
                        value = state.annotationComment,
                        onValueChange = viewModel::onChangeAnnotationComment,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 80.dp)
                            .background(Color.White)
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { viewModel.onDismissAnnotationDialog() }) {
                            Text("取消")
                        }
                        TextButton(onClick = { viewModel.onConfirmAddAnnotation() }) {
                            Text("保存批注")
                        }
                    }
                }
            }
        )
    }

    // 批注详情弹窗：显示当前批注 + 所有关联位置，并支持跨笔记跳转
    if (state.isAnnotationDetailVisible && state.selectedAnnotation != null) {
        val ann = state.selectedAnnotation
        var showMindMap by remember { mutableStateOf(false) }
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.onDismissAnnotationDetail() },
            confirmButton = {},
            dismissButton = {},
            text = {
                Column {
                    Text(text = "知识点详情", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "标题：${ann.knowledgeTitle}")
                    if (ann.knowledgeDescription.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "说明：${ann.knowledgeDescription}")
                    }
                    if (ann.indexCode.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "编号：${ann.indexCode}")
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "当前批注：${ann.comment}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "引用位置（本笔记及其它笔记）")
                    Spacer(modifier = Modifier.height(4.dp))
                    state.annotationReferences.forEach { ref ->
                        val isCurrent = ref.noteId == state.noteId
                        TextButton(
                            onClick = {
                                viewModel.onDismissAnnotationDetail()
                                if (!isCurrent) {
                                    onNavigateToNote(ref.noteId)
                                } else {
                                    // 当前笔记暂时只关闭弹窗，后续可扩展滚动到对应行
                                }
                            }
                        ) {
                            val lineText = "第 ${ref.anchorIndex + 1} 行"
                            val label = if (isCurrent) {
                                "[本笔记] ${ref.noteTitle} · $lineText"
                            } else {
                                "[其它笔记] ${ref.noteTitle} · $lineText"
                            }
                            Text(text = label)
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(onClick = { showMindMap = true }) {
                        Text(text = "生成知识导图")
                    }
                }
            }
        )

        if (showMindMap) {
            KnowledgeMindMapDialog(
                title = ann.knowledgeTitle,
                entries = state.mindMapEntries,
                onDismiss = { showMindMap = false }
            )
        }
    }
}

// 知识导图用的简单节点结构
private data class MindNode(
    val label: String,
    val fullCode: String,
    val comment: String,
    val children: MutableList<MindNode> = mutableListOf()
)

// 简单知识导图弹窗：根据编号（如 1、1.1、1.1.1）生成树状结构并绘制
@Composable
private fun KnowledgeMindMapDialog(
    title: String,
    entries: List<MindMapEntry>,
    onDismiss: () -> Unit
) {

    fun buildTree(): List<MindNode> {
        val roots = mutableListOf<MindNode>()
        val nodeMap = mutableMapOf<String, MindNode>()
        val sorted = entries
            .filter { it.indexCode.isNotBlank() }
            .sortedBy { it.indexCode }
        for (entry in sorted) {
            val code = entry.indexCode.trim()
            val parts = code.split(".")
            val label = parts.last()
            val node = MindNode(label = label, fullCode = code, comment = entry.comment)
            nodeMap[code] = node
            if (parts.size == 1) {
                roots.add(node)
            } else {
                val parentCode = parts.dropLast(1).joinToString(".")
                val parent = nodeMap[parentCode]
                if (parent != null) {
                    parent.children.add(node)
                } else {
                    roots.add(node)
                }
            }
        }
        return roots
    }

    val roots = remember(entries) { buildTree() }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        dismissButton = {},
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 200.dp, max = 480.dp)
            ) {
                Text(text = "知识导图：$title", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                if (roots.isEmpty()) {
                    Text(text = "当前知识点还没有填写编号的批注，暂无法生成导图。")
                } else {
                    val scrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(scrollState)
                            .fillMaxWidth()
                    ) {
                        roots.forEach { root ->
                            MindMapNodeView(node = root, level = 0)
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }
        }
    )
}

@Composable
private fun MindMapNodeView(
    node: MindNode,
    level: Int
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (level * 24).dp)
    ) {
        // 左侧分支线 + 圆点
        Canvas(
            modifier = Modifier
                .width(24.dp)
                .height(32.dp)
        ) {
            val cx = size.width * 0.5f
            val cy = size.height * 0.5f
            drawLine(
                color = Color.Gray,
                start = Offset(cx, 0f),
                end = Offset(cx, cy),
                strokeWidth = 3f
            )
            drawCircle(
                color = Color(0xFF2196F3),
                radius = 6f,
                center = Offset(cx, cy)
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = 4.dp)
        ) {
            Text(
                text = "${node.fullCode}  ${node.comment}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
    node.children.forEach { child ->
        MindMapNodeView(node = child, level = level + 1)
    }
}

@Composable
private fun NoteToolbar(
    state: NoteDetailUiState,
    onPageStyleChange: (PageStyle) -> Unit,
    onFormatChange: (
        fontFamily: String?,
        fontSizeSp: Int?,
        colorArgb: Long?,
        bold: Boolean?,
        italic: Boolean?,
        underline: Boolean?
    ) -> Unit,
    onClickImage: () -> Unit,
    onClickRecord: () -> Unit,
    onClickAnnotation: () -> Unit,
    onClickPen: () -> Unit,
    onClickEraser: () -> Unit
) {
    var formatMenuExpanded by remember { mutableStateOf(false) }
    var fontMenuExpanded by remember { mutableStateOf(false) }
    var sizeMenuExpanded by remember { mutableStateOf(false) }
    var colorMenuExpanded by remember { mutableStateOf(false) }
    val toolbarScrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(toolbarScrollState)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        val activeColor = MaterialTheme.colorScheme.primary
        val inactiveColor = Color.Black

        Row(verticalAlignment = Alignment.CenterVertically) {
            // 页面格式：无、横线、网格（使用图标表示）
            TextButton(onClick = { onPageStyleChange(PageStyle.PLAIN) }) {
                Icon(
                    imageVector = Icons.Filled.Article,
                    contentDescription = "空白页面",
                    tint = if (state.pageStyle == PageStyle.PLAIN) activeColor else inactiveColor
                )
            }
            TextButton(onClick = { onPageStyleChange(PageStyle.LINES) }) {
                Icon(
                    imageVector = Icons.Filled.Subject,
                    contentDescription = "横线页面",
                    tint = if (state.pageStyle == PageStyle.LINES) activeColor else inactiveColor
                )
            }
            TextButton(onClick = { onPageStyleChange(PageStyle.GRID) }) {
                Icon(
                    imageVector = Icons.Filled.GridOn,
                    contentDescription = "网格页面",
                    tint = if (state.pageStyle == PageStyle.GRID) activeColor else inactiveColor
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // 文本编辑工具入口
            TextButton(onClick = { formatMenuExpanded = !formatMenuExpanded }) {
                Icon(
                    imageVector = Icons.Filled.TextFields,
                    contentDescription = "文本样式",
                    tint = if (formatMenuExpanded) activeColor else inactiveColor
                )
            }

            // 其余功能按钮
            TextButton(onClick = onClickAnnotation) {
                Icon(
                    imageVector = Icons.Filled.StickyNote2,
                    contentDescription = "批注",
                    tint = inactiveColor
                )
            }
            TextButton(onClick = onClickImage) {
                Icon(
                    imageVector = Icons.Filled.Image,
                    contentDescription = "插入图片",
                    tint = inactiveColor
                )
            }
            TextButton(onClick = onClickRecord) {
                Icon(
                    imageVector = Icons.Filled.Mic,
                    contentDescription = "录音",
                    tint = inactiveColor
                )
            }
            TextButton(onClick = onClickPen) {
                Icon(
                    imageVector = Icons.Filled.Brush,
                    contentDescription = "画笔",
                    tint = if (state.isDrawingMode) activeColor else inactiveColor
                )
            }
            TextButton(onClick = onClickEraser) {
                Icon(
                    imageVector = Icons.Filled.CleaningServices,
                    contentDescription = "橡皮",
                    tint = if (state.isEraserMode) activeColor else inactiveColor
                )
            }
        }
    }

    if (formatMenuExpanded) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 字体选择
                Text(text = "字体")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { fontMenuExpanded = true }) {
                    Text(
                        text = when (state.textFormat.fontFamily) {
                            "Song" -> "宋体"
                            "Kai" -> "楷体"
                            else -> "黑体"
                        }
                    )
                }
                DropdownMenu(
                    expanded = fontMenuExpanded,
                    onDismissRequest = { fontMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("宋体") },
                        onClick = {
                            fontMenuExpanded = false
                            onFormatChange("Song", null, null, null, null, null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("黑体") },
                        onClick = {
                            fontMenuExpanded = false
                            onFormatChange("Hei", null, null, null, null, null)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("楷体") },
                        onClick = {
                            fontMenuExpanded = false
                            onFormatChange("Kai", null, null, null, null, null)
                        }
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 字号
                Text(text = "字号")
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = { sizeMenuExpanded = true }) {
                    Text(text = "${state.textFormat.fontSizeSp}号")
                }
                DropdownMenu(
                    expanded = sizeMenuExpanded,
                    onDismissRequest = { sizeMenuExpanded = false }
                ) {
                    listOf(14, 16, 18, 20, 24).forEach { sz ->
                        DropdownMenuItem(
                            text = { Text("${sz}号") },
                            onClick = {
                                sizeMenuExpanded = false
                                onFormatChange(null, sz, null, null, null, null)
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 颜色
                Text(text = "颜色")
                Spacer(modifier = Modifier.width(4.dp))
                val colors = listOf(
                    0xFF000000, // 黑
                    0xFFF44336, // 红
                    0xFF2196F3, // 蓝
                    0xFF4CAF50  // 绿
                )
                colors.forEach { c ->
                    Box(
                        modifier = Modifier
                            .height(20.dp)
                            .width(20.dp)
                            .background(Color(c))
                            .clickable {
                                onFormatChange(null, null, c, null, null, null)
                            }
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 粗体 / 斜体 / 下划线
                TextButton(onClick = {
                    onFormatChange(
                        null, null, null,
                        !state.textFormat.bold,
                        null,
                        null
                    )
                }) {
                    Text(
                        text = "B",
                        fontWeight = FontWeight.Bold,
                        color = if (state.textFormat.bold) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )
                }
                TextButton(onClick = {
                    onFormatChange(
                        null, null, null,
                        null,
                        !state.textFormat.italic,
                        null
                    )
                }) {
                    Text(
                        text = "I",
                        fontStyle = FontStyle.Italic,
                        color = if (state.textFormat.italic) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )
                }
                TextButton(
                    onClick = {
                        onFormatChange(
                            null, null, null,
                            null,
                            null,
                            !state.textFormat.underline
                        )
                    }
                ) {
                    Text(
                        text = "U",
                        textDecoration = androidx.compose.ui.text.style.TextDecoration.Underline,
                        color = if (state.textFormat.underline) MaterialTheme.colorScheme.primary else Color.Unspecified
                    )
                }
            }
        }
    }
}

@Composable
private fun DoodleCanvas(
    strokes: List<DoodleStroke>,
    isDrawing: Boolean,
    isErasing: Boolean,
    colorArgb: Long,
    strokeWidth: Float,
    eraserSize: Float,
    onStrokeFinished: (List<Pair<Float, Float>>) -> Unit,
    onEraseAt: (List<Pair<Float, Float>>) -> Unit
) {
    val currentPoints = remember { mutableStateOf(listOf<Offset>()) }

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(600.dp)
            .background(Color.Transparent)
            .pointerInput(isDrawing, isErasing) {
                if (!isDrawing && !isErasing) return@pointerInput
                detectDragGestures(
                    onDragStart = { offset ->
                        if (isDrawing) {
                            currentPoints.value = listOf(offset)
                        } else if (isErasing) {
                            onEraseAt(listOf(offset.x to offset.y))
                        }
                    },
                    onDrag = { change, _ ->
                        if (isDrawing) {
                            currentPoints.value = currentPoints.value + change.position
                        } else if (isErasing) {
                            onEraseAt(listOf(change.position.x to change.position.y))
                        }
                    },
                    onDragEnd = {
                        if (isDrawing) {
                            val pts = currentPoints.value
                            if (pts.size > 1) {
                                onStrokeFinished(pts.map { it.x to it.y })
                            }
                        }
                        currentPoints.value = emptyList()
                    },
                    onDragCancel = {
                        currentPoints.value = emptyList()
                    }
                )
            }
    ) {
        // 先画已保存的笔画
        strokes.forEach { stroke ->
            if (stroke.points.size > 1) {
                val path = Path().apply {
                    moveTo(stroke.points[0].x, stroke.points[0].y)
                    for (i in 1 until stroke.points.size) {
                        lineTo(stroke.points[i].x, stroke.points[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = Color(stroke.colorArgb),
                    style = Stroke(
                        width = stroke.width,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // 正在绘制中的当前一笔（仅在画笔模式下显示）
        if (isDrawing) {
            val pts = currentPoints.value
            if (pts.size > 1) {
                val path = Path().apply {
                    moveTo(pts[0].x, pts[0].y)
                    for (i in 1 until pts.size) {
                        lineTo(pts[i].x, pts[i].y)
                    }
                }
                drawPath(
                    path = path,
                    color = Color(colorArgb),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }
    }
}

@Composable
fun AudioBlockPlayer(uri: String, onDelete: () -> Unit) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<android.media.MediaPlayer?>(null) }
    var duration by remember { mutableStateOf(0) }
    var position by remember { mutableStateOf(0) }
    val progress: Float = if (duration > 0) position.toFloat() / duration else 0f

    // 播放时定期更新进度
    LaunchedEffect(mediaPlayer?.isPlaying) {
        val mp = mediaPlayer
        if (mp != null && mp.isPlaying) {
            while (mp.isPlaying) {
                position = mp.currentPosition
                kotlinx.coroutines.delay(500)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {
        // 顶部：标题 + 时间
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "录音", style = MaterialTheme.typography.bodyMedium)
            val curSec = position / 1000
            val totalSec = duration / 1000
            val curText = String.format("%02d:%02d", curSec / 60, curSec % 60)
            val totalText = if (duration > 0) {
                String.format("%02d:%02d", totalSec / 60, totalSec % 60)
            } else {
                "--:--"
            }
            Text(text = "$curText / $totalText", style = MaterialTheme.typography.labelSmall)
        }

        // 中间：标准横向进度条
        Spacer(modifier = Modifier.height(4.dp))
        Slider(
            value = progress,
            onValueChange = { value ->
                val mp = mediaPlayer
                if (mp != null && duration > 0) {
                    val newPos = (value * duration).toInt()
                    mp.seekTo(newPos)
                    position = newPos
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        // 底部：控制按钮一排横向排布
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(onClick = {
                val mp = mediaPlayer
                if (mp == null) {
                    val newPlayer = android.media.MediaPlayer().apply {
                        setDataSource(context, Uri.parse(uri))
                        prepare()
                        duration = this.duration
                        start()
                    }
                    mediaPlayer = newPlayer
                } else {
                    mp.start()
                }
            }) {
                Text("播放")
            }
            TextButton(onClick = {
                mediaPlayer?.let { mp ->
                    mp.pause()
                }
            }) {
                Text("暂停")
            }
            TextButton(onClick = {
                mediaPlayer?.let { mp ->
                    val newPos = (mp.currentPosition - 10_000).coerceAtLeast(0)
                    mp.seekTo(newPos)
                    position = newPos
                }
            }) {
                Text("-10秒")
            }
            TextButton(onClick = {
                mediaPlayer?.let { mp ->
                    val newPos = (mp.currentPosition + 10_000).coerceAtMost(mp.duration)
                    mp.seekTo(newPos)
                    position = newPos
                }
            }) {
                Text("+10秒")
            }
            TextButton(onClick = {
                mediaPlayer?.let { mp ->
                    mp.stop()
                    mp.release()
                }
                mediaPlayer = null
                duration = 0
                position = 0
            }) {
                Text("停止")
            }
            TextButton(onClick = {
                mediaPlayer?.let { mp ->
                    mp.stop()
                    mp.release()
                }
                mediaPlayer = null
                duration = 0
                position = 0
                onDelete()
            }) {
                Text("删除")
            }
        }
    }
}

private fun TextFormat.toTextStyle(): TextStyle {
    val family = when (fontFamily) {
        "Song" -> FontFamily.Serif      // 宋体 → 衬线字体
        "Kai" -> FontFamily.Cursive    // 楷体 → 手写风格
        "Hei" -> FontFamily.SansSerif  // 黑体 / 默认 → 无衬线
        else -> FontFamily.SansSerif
    }
    return TextStyle(
        fontFamily = family,
        fontSize = fontSizeSp.sp,
        color = Color(colorArgb),
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontStyle = if (italic) FontStyle.Italic else FontStyle.Normal,
        textDecoration = if (underline) androidx.compose.ui.text.style.TextDecoration.Underline else null
    )
}

private fun Modifier.drawNoteBackground(style: PageStyle): Modifier = drawBehind {
    if (style == PageStyle.PLAIN) return@drawBehind
    val lineColor = Color(0xFFE0E0E0)
    val step = 48f
    if (style == PageStyle.LINES || style == PageStyle.GRID) {
        var y = 0f
        while (y < size.height) {
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(0f, y),
                end = androidx.compose.ui.geometry.Offset(size.width, y),
                strokeWidth = 1f
            )
            y += step
        }
    }
    if (style == PageStyle.GRID) {
        var x = 0f
        while (x < size.width) {
            drawLine(
                color = lineColor,
                start = androidx.compose.ui.geometry.Offset(x, 0f),
                end = androidx.compose.ui.geometry.Offset(x, size.height),
                strokeWidth = 1f
            )
            x += step
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NoteDetailPreview() {
    CourseReviewPlannerTheme {
        NoteDetailScreen(
            viewModel = NoteDetailViewModel(Application()),
            onBack = {},
            onRequestInsertImageFromCamera = {},
            onRequestInsertImageFromGallery = {},
            onRequestStartRecording = {},
            onRequestStopRecordingAndSave = {},
            onRequestDeleteImage = {},
            onNavigateToNote = {}
        )
    }
}


