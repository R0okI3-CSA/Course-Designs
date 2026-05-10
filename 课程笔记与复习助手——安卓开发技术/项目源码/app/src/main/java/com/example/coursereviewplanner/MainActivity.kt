@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.StickyNote2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursereviewplanner.data.BackupPayload
import com.example.coursereviewplanner.data.BackupRepositoryProvider
import com.example.coursereviewplanner.data.StudyPlanRepositoryProvider
import com.example.coursereviewplanner.data.UserRepository
import com.example.coursereviewplanner.data.UserRepositoryProvider
import com.example.coursereviewplanner.data.local.NoteEntity
import com.example.coursereviewplanner.data.local.ReviewReminderEntity
import com.example.coursereviewplanner.data.local.StudyPlanEntity
import com.example.coursereviewplanner.ui.note.NoteViewModel
import com.example.coursereviewplanner.ui.review.ReviewReminderViewModel
import com.example.coursereviewplanner.ui.studyplan.StudyPlanViewModel
import com.example.coursereviewplanner.ui.auth.AuthActivity
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme
import com.example.coursereviewplanner.util.NewsArticle
import com.example.coursereviewplanner.util.fetchTodayNewsFromApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.rememberAsyncImagePainter

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val userRepository = UserRepositoryProvider.get(this)
            CourseReviewPlannerTheme {
                MainScreen(
                    userRepository = userRepository,
                    onLogoutClick = {
                        lifecycleScope.launch {
                            userRepository.logout()
                            startActivity(Intent(this@MainActivity, AuthActivity::class.java))
                            finish()
                        }
                    }
                )
            }
        }
    }
}

enum class MainTab {
    HOME, PLAN, NOTE, REMINDER
}

data class TodayPlanSummary(
    val planCount: Int,
    val totalMinutes: Int,
    val remainingMinutes: Int,
    val titles: List<String>
)

@Composable
fun MainScreen(
    userRepository: UserRepository? = null,
    onLogoutClick: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    var displayName by remember { mutableStateOf<String?>(null) }
    var username by remember { mutableStateOf<String?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf("") }
    var showHelpDialog by remember { mutableStateOf(false) }

    // 班级：加入/退出 + 已加入班级展示
    var showJoinClassDialog by remember { mutableStateOf(false) }
    var joinClassName by remember { mutableStateOf("") }
    var pendingLeaveClass by remember { mutableStateOf<String?>(null) }
    val joinedClasses by (userRepository?.joinedClassesFlowForCurrentUser() ?: flowOf(emptySet()))
        .collectAsState(initial = emptySet())

    // 用户头像（从相册选择），当前简单保存在内存中
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    val avatarPickerLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            if (uri != null) {
                avatarUri = uri
            }
        }

    var todayPlanSummary by remember { mutableStateOf<TodayPlanSummary?>(null) }
    var remainingMinutesToday by remember { mutableStateOf<Int?>(null) }
    var lastNoteId by remember { mutableStateOf<Long?>(null) }
    var lastNoteTitle by remember { mutableStateOf<String?>(null) }

    var newsList by remember { mutableStateOf<List<NewsArticle>>(emptyList()) }
    var currentNewsIndex by remember { mutableStateOf(0) }
    var isLoadingNews by remember { mutableStateOf(false) }
    var newsError by remember { mutableStateOf<String?>(null) }

    // 当前底部导航所在的页面
    var currentTab by remember { mutableStateOf(MainTab.HOME) }

    // 云同步：比较本地与云端数据量，决定上传或下载
    fun triggerCloudSync() {
        val repoUserRepository = userRepository
        if (repoUserRepository == null) {
            android.widget.Toast.makeText(
                context,
                "当前未登录，无法云同步",
                android.widget.Toast.LENGTH_SHORT
            ).show()
            return
        }
        val backupRepo = BackupRepositoryProvider.get(context)
        scope.launch {
            val user = repoUserRepository.getCurrentUser() ?: run {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        "当前未登录，无法云同步",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
                return@launch
            }
            val userId = user.id

            val localPayload = backupRepo.buildPayloadForUser(userId)
            val localSize =
                localPayload.notes.size + localPayload.studyPlans.size + localPayload.reminders.size

            val serverBackup = fetchBackupFromServer(userId)
            val serverPayload = serverBackup?.payload
            val serverSize = serverPayload?.let {
                it.notes.size + it.studyPlans.size + it.reminders.size
            } ?: 0

            if (serverPayload == null || localSize > serverSize) {
                val json = buildBackupJsonString(userId, localPayload)
                val ok = postBackupToServer(json)
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        if (ok) "已将本地数据上传到云端" else "上传云备份失败，请稍后重试",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } else if (serverSize > localSize) {
                backupRepo.restoreFromPayload(userId, serverPayload)
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        "已从云端恢复到本地（覆盖原有数据）",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                withContext(Dispatchers.Main) {
                    android.widget.Toast.makeText(
                        context,
                        "本地与云端数据量相近，无需同步",
                        android.widget.Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    suspend fun refreshDashboardWidgets(repo: UserRepository) {
        val user = repo.getCurrentUser()
        displayName = user?.displayName
        username = user?.username

        val userId = user?.id
        if (userId != null) {
            val studyRepo = StudyPlanRepositoryProvider.get(context)
            val summary = withContext(Dispatchers.IO) {
                val plans = studyRepo.getAllPlansForUser(userId)
                if (plans.isEmpty()) {
                    null
                } else {
                    // 使用 java.time 计算今天的日期和时间；低于 Android O 时退化为无计划
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        return@withContext null
                    }
                    val today = java.time.LocalDate.now()
                    val todayEpoch = today.toEpochDay()
                    val weekdayIndex = today.dayOfWeek.value - 1
                    val now = java.time.LocalTime.now()
                    val nowMinutes = now.hour * 60 + now.minute

                    var count = 0
                    var totalMinutes = 0
                    var remainingMinutes = 0
                    val titleSet = linkedSetOf<String>()

                    for (plan in plans) {
                        var hasToday = false

                        val slots = studyRepo.getSlotsForPlan(plan.id)
                        slots.forEach { slot ->
                            if (slot.colIndex == weekdayIndex) {
                                count++
                                val (startMin, endMin) = when (slot.rowIndex) {
                                    0 -> 8 * 60 to (8 * 60 + 45)
                                    1 -> (8 * 60 + 55) to (9 * 60 + 40)
                                    2 -> 10 * 60 to (10 * 60 + 45)
                                    3 -> (10 * 60 + 55) to (11 * 60 + 40)
                                    4 -> 14 * 60 to (14 * 60 + 45)
                                    5 -> (14 * 60 + 50) to (15 * 60 + 35)
                                    6 -> (15 * 60 + 55) to (16 * 60 + 40)
                                    7 -> (16 * 60 + 50) to (17 * 60 + 35)
                                    8 -> 19 * 60 to (19 * 60 + 45)
                                    9 -> (19 * 60 + 55) to (20 * 60 + 40)
                                    10 -> (20 * 60 + 50) to (21 * 60 + 35)
                                    11 -> (21 * 60 + 45) to (22 * 60 + 30)
                                    else -> 0 to 0
                                }
                                val duration = (endMin - startMin).coerceAtLeast(0)
                                totalMinutes += duration
                                val remainForSlot = when {
                                    nowMinutes >= endMin -> 0
                                    nowMinutes <= startMin -> duration
                                    else -> (endMin - nowMinutes).coerceAtLeast(0)
                                }
                                remainingMinutes += remainForSlot
                                hasToday = true
                            }
                        }

                        val events =
                            studyRepo.getCustomEventsForRange(plan.id, todayEpoch, todayEpoch)
                        events.forEach { ev ->
                            count++
                            val duration = (ev.endMinutes - ev.startMinutes).coerceAtLeast(0)
                            totalMinutes += duration
                            val startMin = ev.startMinutes
                            val endMin = ev.endMinutes
                            val remainForEvent = when {
                                nowMinutes >= endMin -> 0
                                nowMinutes <= startMin -> duration
                                else -> (endMin - nowMinutes).coerceAtLeast(0)
                            }
                            remainingMinutes += remainForEvent
                            hasToday = true
                        }

                        if (hasToday) {
                            titleSet.add(plan.title)
                        }
                    }

                    if (count == 0) {
                        null
                    } else {
                        TodayPlanSummary(
                            planCount = count,
                            totalMinutes = totalMinutes,
                            remainingMinutes = remainingMinutes,
                            titles = titleSet.toList()
                        )
                    }
                }
            }
            todayPlanSummary = summary
            remainingMinutesToday = summary?.remainingMinutes

            val last = repo.getLastOpenedNoteForCurrentUser()
            lastNoteId = last?.first
            lastNoteTitle = last?.second
        } else {
            todayPlanSummary = null
            remainingMinutesToday = null
            lastNoteId = null
            lastNoteTitle = null
        }
    }

    LaunchedEffect(userRepository) {
        if (userRepository != null) {
            refreshDashboardWidgets(userRepository)
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, userRepository) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && userRepository != null) {
                scope.launch {
                    refreshDashboardWidgets(userRepository)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 新闻：首次加载 + 轮播
    LaunchedEffect(newsList) {
        if (newsList.isEmpty()) {
            isLoadingNews = true
            newsError = null
            val apiKey = "pub_294614480c14464cb71c281565beeb3e"
            val result = fetchTodayNewsFromApi(apiKey)
            if (result.isNotEmpty()) {
                newsList = result
                currentNewsIndex = 0
            } else {
                newsError = "暂时无法获取今日新闻"
            }
            isLoadingNews = false
        } else {
            while (true) {
                kotlinx.coroutines.delay(6000L)
                currentNewsIndex = (currentNewsIndex + 1) % newsList.size
            }
        }
    }

    val effectiveName = displayName?.takeIf { it.isNotBlank() }
        ?: username?.let { "用户$it" }
        ?: "未登录用户"

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Text(text = "当前用户", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(12.dp))

                    // 头像 + 昵称区域
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // 头像：点击可从相册选择
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .clickable {
                                        avatarPickerLauncher.launch("image/*")
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (avatarUri != null) {
                                    Image(
                                        painter = rememberAsyncImagePainter(avatarUri),
                                        contentDescription = "头像",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Filled.AccountCircle,
                                        contentDescription = "默认头像",
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(56.dp)
                                    )
                                }
                            }

                            // 已加入班级：显示在头像下方；点击班级可退出
                            Spacer(modifier = Modifier.height(8.dp))
                            if (userRepository != null && joinedClasses.isNotEmpty()) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    joinedClasses
                                        .toList()
                                        .sorted()
                                        .take(3)
                                        .forEach { cls ->
                                            Text(
                                                text = cls,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier
                                                    .padding(vertical = 2.dp)
                                                    .clickable { pendingLeaveClass = cls }
                                            )
                                        }
                                    if (joinedClasses.size > 3) {
                                        Text(
                                            text = "…等${joinedClasses.size}个班级",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            } else {
                                Text(
                                    text = "未加入班级",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column(
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = effectiveName, style = MaterialTheme.typography.bodyMedium)
                            username?.let {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "账号：$it", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    if (userRepository != null) {
                        Button(
                            onClick = {
                                editingName = displayName?.takeIf { it.isNotBlank() } ?: ""
                                showEditDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(text = "修改昵称")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { triggerCloudSync() },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(text = "云同步")
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                joinClassName = ""
                                showJoinClassDialog = true
                            },
                            modifier = Modifier.fillMaxWidth(0.8f)
                        ) {
                            Text(text = "加入班级")
                        }
                    }
                }
            }
        }
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                if (currentTab == MainTab.HOME) {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = "课程笔记与复习助手",
                                style = MaterialTheme.typography.titleLarge
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch { drawerState.open() }
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.AccountCircle,
                                    contentDescription = "当前用户",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        },
                        actions = {
                            IconButton(onClick = onLogoutClick) {
                                Icon(
                                    imageVector = Icons.Filled.ExitToApp,
                                    contentDescription = "退出登录",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    )
                }
            },
            bottomBar = {
                MainBottomBar(
                    currentTab = currentTab,
                    onHomeClick = { currentTab = MainTab.HOME },
                    onPlanClick = { currentTab = MainTab.PLAN },
                    onNoteClick = { currentTab = MainTab.NOTE },
                    onReminderClick = { currentTab = MainTab.REMINDER }
                )
            }
        ) { innerPadding ->
            when (currentTab) {
                MainTab.HOME -> {
                    HomeTabContent(
                        innerPadding = innerPadding,
                        todayPlanSummary = todayPlanSummary,
                        remainingMinutesToday = remainingMinutesToday,
                        lastNoteId = lastNoteId,
                        lastNoteTitle = lastNoteTitle,
                        isLoadingNews = isLoadingNews,
                        newsList = newsList,
                        currentNewsIndex = currentNewsIndex,
                        newsError = newsError,
                        onStudyPlanClick = { currentTab = MainTab.PLAN },
                        onNoteManagementClick = { currentTab = MainTab.NOTE },
                        onReviewReminderClick = { currentTab = MainTab.REMINDER },
                        onShowHelp = { showHelpDialog = true }
                    )
                }
                MainTab.PLAN -> {
                    val vm: StudyPlanViewModel = viewModel()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        StudyPlanScreen(
                            viewModel = vm,
                            onOpenPlanDetail = { planId, title ->
                                context.startActivity(
                                    Intent(
                                        context,
                                        StudyPlanDetailActivity::class.java
                                    ).putExtra("planId", planId)
                                        .putExtra("planTitle", title)
                                )
                            }
                        )
                    }
                }
                MainTab.NOTE -> {
                    val vm: NoteViewModel = viewModel()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        NoteManagementScreen(
                            viewModel = vm,
                            onOpenNoteDetail = { noteId ->
                                context.startActivity(
                                    Intent(
                                        context,
                                        NoteDetailActivity::class.java
                                    ).putExtra("noteId", noteId)
                                )
                            }
                        )
                    }
                }
                MainTab.REMINDER -> {
                    val vm: ReviewReminderViewModel = viewModel()
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        ReviewReminderScreen(
                            viewModel = vm,
                            onOpenReminderDetail = { reminderId ->
                                context.startActivity(
                                    Intent(
                                        context,
                                        ReviewReminderDetailActivity::class.java
                                    ).putExtra("reminderId", reminderId)
                                )
                            }
                        )
                    }
                }
            }
        }
    }

    // 加入班级弹窗
    if (showJoinClassDialog && userRepository != null) {
        AlertDialog(
            onDismissRequest = { showJoinClassDialog = false },
            title = { Text("加入班级") },
            text = {
                Column {
                    Text(text = "请输入班级名称（加入后可接收班级发布的学习计划）")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = joinClassName,
                        onValueChange = { joinClassName = it },
                        singleLine = true,
                        label = { Text("班级名称") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = joinClassName.trim()
                        if (name.isBlank()) {
                            android.widget.Toast.makeText(
                                context,
                                "班级名称不能为空",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                            return@TextButton
                        }
                        scope.launch {
                            userRepository.joinClassForCurrentUser(name)
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                    context,
                                    "已加入班级：$name",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        showJoinClassDialog = false
                    }
                ) { Text("保存") }
            },
            dismissButton = {
                TextButton(onClick = { showJoinClassDialog = false }) { Text("取消") }
            }
        )
    }

    // 退出班级确认弹窗
    val leaving = pendingLeaveClass
    if (leaving != null && userRepository != null) {
        AlertDialog(
            onDismissRequest = { pendingLeaveClass = null },
            title = { Text("退出班级") },
            text = { Text(text = "确定退出班级「$leaving」吗？退出后将不再接收该班级发布的学习计划。") },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            userRepository.leaveClassForCurrentUser(leaving)
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(
                                    context,
                                    "已退出班级：$leaving",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        pendingLeaveClass = null
                    }
                ) { Text("退出") }
            },
            dismissButton = {
                TextButton(onClick = { pendingLeaveClass = null }) { Text("取消") }
            }
        )
    }

    // 修改昵称弹窗
    if (showEditDialog && userRepository != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text(text = "修改昵称") },
            text = {
                OutlinedTextField(
                    value = editingName,
                    onValueChange = { editingName = it },
                    singleLine = true,
                    label = { Text("新的昵称（可选）") }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val newName = editingName.trim()
                        scope.launch {
                            userRepository.updateDisplayName(newName)
                            displayName = newName
                            showEditDialog = false
                        }
                    }
                ) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    // 使用说明弹窗
    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = { Text(text = "使用说明") },
            text = {
                Column {
                    Text(
                        text = "本应用用于管理课程笔记、学习计划和复习提醒，并支持云同步与知识导图等扩展功能。" +
                                "\n\n主界面：\n- 顶部展示今日新闻、今日计划、今日剩余学习时间和上次打开的笔记。\n" +
                                "- 中间四个大按钮分别进入学习计划、笔记管理、复习提醒以及本说明界面。\n\n" +
                                "底部导航栏：\n- 可在主界面、学习计划、笔记管理和复习提醒之间快速切换。\n\n" +
                                "左上角用户图标：\n- 点击可打开侧边栏，修改昵称并执行云同步（将本地数据与自建服务器上的备份进行同步）。",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text(text = "知道了")
                }
            }
        )
    }
}

@Composable
fun FeatureSquareButton(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .size(120.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun MainBottomBar(
    currentTab: MainTab,
    onHomeClick: () -> Unit,
    onPlanClick: () -> Unit,
    onNoteClick: () -> Unit,
    onReminderClick: () -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceVariant
    ) {
        NavigationBarItem(
            selected = currentTab == MainTab.HOME,
            onClick = onHomeClick,
            icon = { Icon(imageVector = Icons.Filled.Home, contentDescription = "首页") },
            label = null,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
        NavigationBarItem(
            selected = currentTab == MainTab.PLAN,
            onClick = onPlanClick,
            icon = { Icon(imageVector = Icons.Filled.CalendarMonth, contentDescription = "学习计划") },
            label = null,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
        NavigationBarItem(
            selected = currentTab == MainTab.NOTE,
            onClick = onNoteClick,
            icon = { Icon(imageVector = Icons.Filled.StickyNote2, contentDescription = "笔记管理") },
            label = null,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
        NavigationBarItem(
            selected = currentTab == MainTab.REMINDER,
            onClick = onReminderClick,
            icon = { Icon(imageVector = Icons.Filled.Alarm, contentDescription = "复习提醒") },
            label = null,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

/** 将 BackupPayload 转成上传给服务器的 JSON 字符串 */
private fun buildBackupJsonString(userId: Long, payload: BackupPayload): String {
    val root = org.json.JSONObject()
    root.put("userId", userId)
    root.put("timestamp", System.currentTimeMillis())

    val p = org.json.JSONObject()

    val notesArray = org.json.JSONArray()
    payload.notes.forEach { n ->
        val o = org.json.JSONObject()
        o.put("id", n.id)
        o.put("userId", n.userId)
        o.put("tagId", n.tagId)
        o.put("title", n.title)
        o.put("content", n.content)
        o.put("richContentJson", n.richContentJson)
        o.put("pageStyle", n.pageStyle)
        o.put("createdAt", n.createdAt)
        o.put("updatedAt", n.updatedAt)
        notesArray.put(o)
    }
    p.put("notes", notesArray)

    val plansArray = org.json.JSONArray()
    payload.studyPlans.forEach { sp ->
        val o = org.json.JSONObject()
        o.put("id", sp.id)
        o.put("userId", sp.userId)
        o.put("tagId", sp.tagId)
        o.put("title", sp.title)
        o.put("createdAt", sp.createdAt)
        o.put("updatedAt", sp.updatedAt)
        plansArray.put(o)
    }
    p.put("studyPlans", plansArray)

    val remindersArray = org.json.JSONArray()
    payload.reminders.forEach { r ->
        val o = org.json.JSONObject()
        o.put("id", r.id)
        o.put("userId", r.userId)
        o.put("tagId", r.tagId)
        o.put("title", r.title)
        o.put("content", r.content)
        o.put("targetTime", r.targetTime)
        o.put("isEnabled", r.isEnabled)
        o.put("createdAt", r.createdAt)
        o.put("updatedAt", r.updatedAt)
        remindersArray.put(o)
    }
    p.put("reminders", remindersArray)

    root.put("payload", p)
    return root.toString()
}

private data class ServerBackup(
    val userId: Long,
    val timestamp: Long,
    val payload: BackupPayload
)

/** 从服务器获取当前用户的备份数据；失败或无备份返回 null */
private suspend fun fetchBackupFromServer(userId: Long): ServerBackup? =
    withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL("http://192.168.43.201:3000/api/backup?userId=$userId")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "GET"
            }
            val code = conn.responseCode
            if (code !in 200..299) {
                conn.disconnect()
                return@withContext null
            }
            val body = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            conn.disconnect()

            val root = org.json.JSONObject(body)
            if (!root.optBoolean("success", false)) return@withContext null
            val uid = root.optLong("userId", userId)
            val ts = root.optLong("timestamp", 0L)
            val payloadObj = root.optJSONObject("payload") ?: return@withContext null

            val notesJson = payloadObj.optJSONArray("notes") ?: org.json.JSONArray()
            val notes = mutableListOf<NoteEntity>()
            for (i in 0 until notesJson.length()) {
                val o = notesJson.getJSONObject(i)
                val title = o.optString("title", "")
                val content = o.optString("content", "")
                val richJson =
                    if (o.isNull("richContentJson")) null else o.optString("richContentJson")
                val pageStyle = o.optString("pageStyle", "LINES")
                val createdAt = o.optLong("createdAt", System.currentTimeMillis())
                val updatedAt = o.optLong("updatedAt", createdAt)
                notes.add(
                    NoteEntity(
                        id = 0L,
                        userId = uid,
                        tagId = null,
                        title = title,
                        content = content,
                        richContentJson = richJson,
                        pageStyle = pageStyle,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            }

            val plansJson = payloadObj.optJSONArray("studyPlans") ?: org.json.JSONArray()
            val plans = mutableListOf<StudyPlanEntity>()
            for (i in 0 until plansJson.length()) {
                val o = plansJson.getJSONObject(i)
                val title = o.optString("title", "")
                val createdAt = o.optLong("createdAt", System.currentTimeMillis())
                val updatedAt = o.optLong("updatedAt", createdAt)
                plans.add(
                    StudyPlanEntity(
                        id = 0L,
                        userId = uid,
                        tagId = null,
                        title = title,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            }

            val remindersJson = payloadObj.optJSONArray("reminders") ?: org.json.JSONArray()
            val reminders = mutableListOf<ReviewReminderEntity>()
            for (i in 0 until remindersJson.length()) {
                val o = remindersJson.getJSONObject(i)
                val title = o.optString("title", "")
                val content = o.optString("content", "")
                val targetTime =
                    if (o.isNull("targetTime")) null else o.optLong("targetTime")
                val isEnabled = o.optBoolean("isEnabled", false)
                val createdAt = o.optLong("createdAt", System.currentTimeMillis())
                val updatedAt = o.optLong("updatedAt", createdAt)
                reminders.add(
                    ReviewReminderEntity(
                        id = 0L,
                        userId = uid,
                        tagId = null,
                        title = title,
                        content = content,
                        targetTime = targetTime,
                        isEnabled = isEnabled,
                        createdAt = createdAt,
                        updatedAt = updatedAt
                    )
                )
            }

            ServerBackup(
                userId = uid,
                timestamp = ts,
                payload = BackupPayload(
                    notes = notes,
                    studyPlans = plans,
                    reminders = reminders
                )
            )
        } catch (_: Exception) {
            null
        }
    }

/** 将备份 JSON 通过 POST 发送到自建服务器 */
private suspend fun postBackupToServer(jsonBody: String): Boolean =
    withContext(Dispatchers.IO) {
        try {
            val url = java.net.URL("http://192.168.43.201:3000/api/backup")
            val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                connectTimeout = 5000
                readTimeout = 5000
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
            }
            conn.outputStream.use { os ->
                val bytes = jsonBody.toByteArray(Charsets.UTF_8)
                os.write(bytes)
            }
            val code = conn.responseCode
            conn.disconnect()
            code in 200..299
        } catch (_: Exception) {
            false
        }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CourseReviewPlannerTheme {
        MainScreen()
    }
}