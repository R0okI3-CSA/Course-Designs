package com.example.coursereviewplanner.ui.studyplan

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.ImportedCustomEvent
import com.example.coursereviewplanner.data.StudyPlanRepositoryProvider
import com.example.coursereviewplanner.data.UserRepositoryProvider
import com.example.coursereviewplanner.data.local.TagWithPlans
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder

data class StudyPlanUiItem(
    val tagId: Long,
    val tagName: String,
    val color: Long,
    val isExpanded: Boolean,
    val plans: List<StudyPlanPlanItem>
)

data class StudyPlanPlanItem(
    val id: Long,
    val title: String
)

data class StudyPlanUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val items: List<StudyPlanUiItem> = emptyList(),
    val ungroupedPlans: List<StudyPlanPlanItem> = emptyList(),
    val showTagDialog: Boolean = false,
    val showPlanDialog: Boolean = false,
    val editingName: String = "",
    val targetTagId: Long? = null,
    val targetPlanId: Long? = null,
    val dialogMode: DialogMode = DialogMode.NONE
)

enum class DialogMode {
    NONE,
    CREATE_TAG,
    RENAME_TAG,
    CREATE_PLAN,
    RENAME_PLAN
}

class StudyPlanViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val studyPlanRepository = StudyPlanRepositoryProvider.get(application)

    var uiState by mutableStateOf(StudyPlanUiState())
        private set

    companion object {
        // 与项目中“分享导入/云备份”一致：指向你自建 Node.js 服务器地址
        private const val SERVER_BASE_URL_FOR_CLASS_PLANS = "http://192.168.43.201:3000"
    }

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            val userId = userRepository.currentUserIdFlow.first()
            if (userId == null) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = "当前未登录，请重新登录后再试"
                )
                return@launch
            }

            // 班级发布计划：拉取并导入到本地学习计划（去重）
            runCatching { syncPublishedPlansForJoinedClasses(userId) }

            val tagsWithPlans: List<TagWithPlans> =
                studyPlanRepository.loadTagsWithPlans(userId)
            val ungrouped = studyPlanRepository.loadUngroupedPlans(userId)
            val items = tagsWithPlans.map { group ->
                StudyPlanUiItem(
                    tagId = group.tag.id,
                    tagName = group.tag.name,
                    color = group.tag.color,
                    isExpanded = true,
                    plans = group.plans.map { p ->
                        StudyPlanPlanItem(
                            id = p.id,
                            title = p.title
                        )
                    }
                )
            }
            uiState = uiState.copy(
                isLoading = false,
                items = items,
                ungroupedPlans = ungrouped.map { p ->
                    StudyPlanPlanItem(id = p.id, title = p.title)
                }
            )
        }
    }

    private suspend fun syncPublishedPlansForJoinedClasses(userId: Long) {
        val classes = userRepository.getJoinedClassesForCurrentUser()
        if (classes.isEmpty()) return

        val imported = userRepository.getImportedClassPlanIdsForCurrentUser().toMutableSet()
        classes.forEach { cls ->
            val plans = fetchClassPlansFromServer(cls)
            plans.forEach { plan ->
                val uniqueId = "${cls}#${plan.id}"
                if (imported.contains(uniqueId)) return@forEach

                val planTitle = plan.customSchedule.optString("planTitle", "").ifBlank { "班级发布计划" }
                val eventsArr = plan.customSchedule.optJSONArray("events")
                val events = mutableListOf<ImportedCustomEvent>()
                if (eventsArr != null) {
                    for (i in 0 until eventsArr.length()) {
                        val e = eventsArr.optJSONObject(i) ?: continue
                        events.add(
                            ImportedCustomEvent(
                                dateEpochDay = e.optLong("dateEpochDay", 0L),
                                startMinutes = e.optInt("startMinutes", 0),
                                endMinutes = e.optInt("endMinutes", 60),
                                title = e.optString("title", "").ifBlank { planTitle },
                                content = e.optString("content", "")
                            )
                        )
                    }
                }
                if (events.isEmpty()) return@forEach

                // 1) 新建一个“未分组计划”
                val created = studyPlanRepository.createPlan(
                    userId = userId,
                    tagId = null,
                    title = "[班级:$cls] $planTitle"
                )
                // 2) 写入自定义事件（覆盖写）
                studyPlanRepository.overwriteCustomEventsForPlan(
                    userId = userId,
                    planId = created.id,
                    events = events
                )

                userRepository.markClassPlanImportedForCurrentUser(uniqueId)
                imported.add(uniqueId)
            }
        }
    }

    private data class RemoteClassPlan(
        val id: String,
        val createdAt: Long,
        val customSchedule: JSONObject
    )

    private suspend fun fetchClassPlansFromServer(className: String): List<RemoteClassPlan> =
        withContext(Dispatchers.IO) {
            try {
                val encoded = URLEncoder.encode(className, "UTF-8")
                val url = java.net.URL("$SERVER_BASE_URL_FOR_CLASS_PLANS/api/classPlans?className=$encoded&limit=50")
                val conn = (url.openConnection() as java.net.HttpURLConnection).apply {
                    connectTimeout = 5000
                    readTimeout = 5000
                    requestMethod = "GET"
                }
                val resp = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                conn.disconnect()

                val json = JSONObject(resp)
                if (!json.optBoolean("success", false)) return@withContext emptyList()
                val arr = json.optJSONArray("plans") ?: return@withContext emptyList()
                val list = mutableListOf<RemoteClassPlan>()
                for (i in 0 until arr.length()) {
                    val obj = arr.optJSONObject(i) ?: continue
                    val id = obj.optString("id", "")
                    val cs = obj.optJSONObject("customSchedule") ?: continue
                    if (id.isBlank()) continue
                    list.add(
                        RemoteClassPlan(
                            id = id,
                            createdAt = obj.optLong("createdAt", 0L),
                            customSchedule = cs
                        )
                    )
                }
                list
            } catch (_: Exception) {
                emptyList()
            }
        }

    fun onAddTagClick() {
        uiState = uiState.copy(
            dialogMode = DialogMode.CREATE_TAG,
            editingName = "",
            targetTagId = null,
            targetPlanId = null
        )
    }

    fun onAddPlanForTag(tagId: Long) {
        uiState = uiState.copy(
            dialogMode = DialogMode.CREATE_PLAN,
            editingName = "",
            targetTagId = tagId,
            targetPlanId = null
        )
    }

    /**
     * 从顶部“＋”菜单新建学习计划：
     * - 直接创建一个“未分组”的学习计划（不隶属于任何标签）。
     */
    fun onAddPlanFromTop() {
        uiState = uiState.copy(
            dialogMode = DialogMode.CREATE_PLAN,
            editingName = "",
            targetTagId = null,
            targetPlanId = null
        )
    }

    fun onRenameTag(tagId: Long, currentName: String) {
        uiState = uiState.copy(
            dialogMode = DialogMode.RENAME_TAG,
            editingName = currentName,
            targetTagId = tagId,
            targetPlanId = null
        )
    }

    fun onRenamePlan(tagId: Long?, planId: Long, currentTitle: String) {
        uiState = uiState.copy(
            dialogMode = DialogMode.RENAME_PLAN,
            editingName = currentTitle,
            targetTagId = tagId,
            targetPlanId = planId
        )
    }

    fun onDialogNameChange(value: String) {
        uiState = uiState.copy(editingName = value)
    }

    fun onDialogDismiss() {
        uiState = uiState.copy(
            dialogMode = DialogMode.NONE,
            editingName = "",
            targetTagId = null,
            targetPlanId = null
        )
    }

    fun onConfirmDialog() {
        val name = uiState.editingName.trim()
        if (name.isEmpty()) {
            uiState = uiState.copy(errorMessage = "名称不能为空")
            return
        }
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            when (uiState.dialogMode) {
                DialogMode.CREATE_TAG -> {
                    // 简单使用几种固定颜色，此处先固定为蓝色
                    val color = 0xFFFF9800 // 橙色
                    studyPlanRepository.createTag(userId, name, color)
                }

                DialogMode.RENAME_TAG -> {
                    val tagId = uiState.targetTagId ?: return@launch
                    studyPlanRepository.renameTag(userId, tagId, name)
                }

                DialogMode.CREATE_PLAN -> {
                    val tagId = uiState.targetTagId
                    studyPlanRepository.createPlan(userId, tagId, name)
                }

                DialogMode.RENAME_PLAN -> {
                    val planId = uiState.targetPlanId ?: return@launch
                    studyPlanRepository.renamePlan(userId, planId, name)
                }

                DialogMode.NONE -> Unit
            }
            onDialogDismiss()
            refresh()
        }
    }

    fun onDeleteTag(tagId: Long) {
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            studyPlanRepository.deleteTag(userId, tagId)
            refresh()
        }
    }

    fun onDeletePlan(planId: Long) {
        viewModelScope.launch {
            val userId = userRepository.currentUserIdFlow.first() ?: return@launch
            studyPlanRepository.deletePlan(userId, planId)
            refresh()
        }
    }

    fun toggleTagExpanded(tagId: Long) {
        uiState = uiState.copy(
            items = uiState.items.map { item ->
                if (item.tagId == tagId) {
                    item.copy(isExpanded = !item.isExpanded)
                } else {
                    item
                }
            }
        )
    }
}


