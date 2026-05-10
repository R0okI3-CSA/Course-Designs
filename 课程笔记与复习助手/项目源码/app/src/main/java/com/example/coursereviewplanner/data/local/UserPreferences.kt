package com.example.coursereviewplanner.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

// DataStore 扩展属性：在 Application / Activity 的 Context 上使用
val Context.userPreferencesDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * 管理与用户相关的偏好设置：
 * - 当前登录用户的 userId
 */
class UserPreferenceManager(
    private val dataStore: DataStore<Preferences>
) {

    companion object {
        private val KEY_CURRENT_USER_ID = longPreferencesKey("current_user_id")
        private val KEY_LAST_NOTE_USER_ID = longPreferencesKey("last_note_user_id")
        private val KEY_LAST_NOTE_ID = longPreferencesKey("last_note_id")
        private val KEY_LAST_NOTE_TITLE = stringPreferencesKey("last_note_title")

        // 班级功能：按 userId 分桶，避免多账号串数据
        private fun keyJoinedClasses(userId: Long) = stringSetPreferencesKey("joined_classes_$userId")
        private fun keyImportedClassPlanIds(userId: Long) =
            stringSetPreferencesKey("imported_class_plan_ids_$userId")
    }

    /**
     * 持续监听当前登录用户的 userId，未登录时为 null。
     */
    val currentUserIdFlow: Flow<Long?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                // 读写异常时提供空偏好，避免崩溃
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            if (prefs.contains(KEY_CURRENT_USER_ID)) {
                prefs[KEY_CURRENT_USER_ID]
            } else {
                null
            }
        }

    /**
     * 设置当前登录用户 id。
     */
    suspend fun setCurrentUserId(userId: Long) {
        dataStore.edit { prefs ->
            prefs[KEY_CURRENT_USER_ID] = userId
        }
    }

    /**
     * 清除当前登录用户信息（退出登录时调用）。
     */
    suspend fun clearCurrentUser() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_CURRENT_USER_ID)
        }
    }

    suspend fun setLastOpenedNote(userId: Long, noteId: Long, title: String) {
        dataStore.edit { prefs ->
            prefs[KEY_LAST_NOTE_USER_ID] = userId
            prefs[KEY_LAST_NOTE_ID] = noteId
            prefs[KEY_LAST_NOTE_TITLE] = title
        }
    }

    /**
     * 获取某个用户的“上次打开笔记”；若不存在或用户不匹配则返回 null。
     */
    suspend fun getLastOpenedNoteForUser(userId: Long): Pair<Long, String?>? {
        val prefs = dataStore.data.first()
        val storedUserId = prefs[KEY_LAST_NOTE_USER_ID] ?: return null
        if (storedUserId != userId) return null
        val noteId = prefs[KEY_LAST_NOTE_ID] ?: return null
        val title = prefs[KEY_LAST_NOTE_TITLE]
        return noteId to title
    }

    // =========================
    // 班级加入/退出（本地持久化）
    // =========================
    fun joinedClassesFlow(userId: Long): Flow<Set<String>> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            prefs[keyJoinedClasses(userId)] ?: emptySet()
        }

    suspend fun getJoinedClassesForUser(userId: Long): Set<String> {
        val prefs = dataStore.data.first()
        return prefs[keyJoinedClasses(userId)] ?: emptySet()
    }

    suspend fun addJoinedClass(userId: Long, className: String) {
        val name = className.trim()
        if (name.isEmpty()) return
        dataStore.edit { prefs ->
            val key = keyJoinedClasses(userId)
            val old = prefs[key] ?: emptySet()
            prefs[key] = (old + name)
        }
    }

    suspend fun removeJoinedClass(userId: Long, className: String) {
        val name = className.trim()
        if (name.isEmpty()) return
        dataStore.edit { prefs ->
            val key = keyJoinedClasses(userId)
            val old = prefs[key] ?: emptySet()
            val updated = old - name
            if (updated.isEmpty()) prefs.remove(key) else prefs[key] = updated
        }
    }

    // =========================
    // 班级发布计划去重标记（本地）
    // =========================
    suspend fun getImportedClassPlanIds(userId: Long): Set<String> {
        val prefs = dataStore.data.first()
        return prefs[keyImportedClassPlanIds(userId)] ?: emptySet()
    }

    suspend fun markClassPlanImported(userId: Long, uniqueId: String) {
        val id = uniqueId.trim()
        if (id.isEmpty()) return
        dataStore.edit { prefs ->
            val key = keyImportedClassPlanIds(userId)
            val old = prefs[key] ?: emptySet()
            prefs[key] = old + id
        }
    }
}


