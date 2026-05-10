package com.example.coursereviewplanner.data

import android.content.Context
import androidx.room.Room
import com.example.coursereviewplanner.data.local.AppDatabase
import com.example.coursereviewplanner.data.local.UserDao
import com.example.coursereviewplanner.data.local.UserEntity
import com.example.coursereviewplanner.data.local.UserPreferenceManager
import com.example.coursereviewplanner.data.local.userPreferencesDataStore
import com.example.coursereviewplanner.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first

/**
 * 用户仓库：封装用户注册、登录和当前用户管理逻辑。
 *
 * 注意：这里先使用简单的单例 Provider，而不是完整的 Hilt 注入，
 * 便于在课程大作业中快速落地。
 */
class UserRepository(
    private val userDao: UserDao,
    private val userPreferenceManager: UserPreferenceManager
) {

    /**
     * 当前登录用户的 userId 流（未登录时为 null）。
     */
    val currentUserIdFlow: Flow<Long?> = userPreferenceManager.currentUserIdFlow

    /**
     * 注册新用户：
     * - 若用户名已存在，返回 null 表示失败。
     * - 成功时返回新建的 UserEntity。
     */
    suspend fun register(username: String, plainPassword: String, displayName: String? = null): UserEntity? {
        val existing = userDao.getUserByUsername(username)
        if (existing != null) {
            return null
        }
        val hashed = PasswordHasher.hash(plainPassword)
        val user = UserEntity(
            username = username,
            passwordHash = hashed,
            displayName = displayName
        )
        val newId = userDao.insertUser(user)
        return user.copy(id = newId)
    }

    /**
     * 登录：
     * - 用户名不存在或密码错误时返回 null。
     * - 成功时保存当前 userId，并返回 UserEntity。
     */
    suspend fun login(username: String, plainPassword: String): UserEntity? {
        val user = userDao.getUserByUsername(username) ?: return null
        return if (PasswordHasher.verify(plainPassword, user.passwordHash)) {
            userPreferenceManager.setCurrentUserId(user.id)
            user
        } else {
            null
        }
    }

  /**
   * 通过已在服务器完成短信验证码验证的用户名（手机号）直接登录：
   * - 若本地存在该用户名，则直接标记为当前用户并返回；
   * - 若本地不存在，则返回 null，由上层决定是否提示先完成注册。
   */
  suspend fun loginByVerifiedUsername(username: String): UserEntity? {
    val user = userDao.getUserByUsername(username) ?: return null
    userPreferenceManager.setCurrentUserId(user.id)
    return user
  }

    /**
     * 退出登录：只清除本地当前用户标记。
     */
    suspend fun logout() {
        userPreferenceManager.clearCurrentUser()
    }

    /**
     * 获取当前登录用户完整信息；若未登录返回 null。
     */
    suspend fun getCurrentUser(): UserEntity? {
        val id = currentUserIdFlow.first() ?: return null
        return userDao.getUserById(id)
    }

    /**
     * 更新当前用户昵称。
     */
    suspend fun updateDisplayName(newDisplayName: String) {
        val id = currentUserIdFlow.first() ?: return
        val user = userDao.getUserById(id) ?: return
        userDao.updateUser(
            user.copy(displayName = newDisplayName)
        )
    }

    /**
     * 记录“上次打开的笔记”，供主界面展示使用。
     */
    suspend fun recordLastOpenedNote(noteId: Long, title: String) {
        val userId = currentUserIdFlow.first() ?: return
        userPreferenceManager.setLastOpenedNote(userId, noteId, title)
    }

    /**
     * 获取当前登录用户的“上次打开的笔记”信息。
     * 返回 Pair<noteId, title?>，若不存在则为 null。
     */
    suspend fun getLastOpenedNoteForCurrentUser(): Pair<Long, String?>? {
        val userId = currentUserIdFlow.first() ?: return null
        return userPreferenceManager.getLastOpenedNoteForUser(userId)
    }

    // =========================
    // 班级功能（本地持久化）
    // =========================
    fun joinedClassesFlowForCurrentUser(): Flow<Set<String>> =
        currentUserIdFlow.flatMapLatest { uid ->
            if (uid == null) flowOf(emptySet()) else userPreferenceManager.joinedClassesFlow(uid)
        }

    suspend fun getJoinedClassesForCurrentUser(): Set<String> {
        val userId = currentUserIdFlow.first() ?: return emptySet()
        return userPreferenceManager.getJoinedClassesForUser(userId)
    }

    suspend fun joinClassForCurrentUser(className: String) {
        val userId = currentUserIdFlow.first() ?: return
        userPreferenceManager.addJoinedClass(userId, className)
    }

    suspend fun leaveClassForCurrentUser(className: String) {
        val userId = currentUserIdFlow.first() ?: return
        userPreferenceManager.removeJoinedClass(userId, className)
    }

    suspend fun getImportedClassPlanIdsForCurrentUser(): Set<String> {
        val userId = currentUserIdFlow.first() ?: return emptySet()
        return userPreferenceManager.getImportedClassPlanIds(userId)
    }

    suspend fun markClassPlanImportedForCurrentUser(uniqueId: String) {
        val userId = currentUserIdFlow.first() ?: return
        userPreferenceManager.markClassPlanImported(userId, uniqueId)
    }
}

/**
 * 简单的 UserRepository Provider：
 * - 确保整个应用只创建一份 AppDatabase 和 UserRepository。
 */
object UserRepositoryProvider {

    @Volatile
    private var repository: UserRepository? = null

    fun get(context: Context): UserRepository {
        // 使用 applicationContext 避免持有 Activity 引用
        val appContext = context.applicationContext
        return repository ?: synchronized(this) {
            repository ?: run {
                val db = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "course_review_planner.db"
                )
                    // 数据库结构变更时直接清空重建，便于课程大作业开发
                    .fallbackToDestructiveMigration()
                    .build()

                val prefsManager = UserPreferenceManager(appContext.userPreferencesDataStore)

                UserRepository(
                    userDao = db.userDao(),
                    userPreferenceManager = prefsManager
                ).also { created ->
                    repository = created
                }
            }
        }
    }
}


