package com.example.coursereviewplanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 用户表实体：
 * - 后续用于本地登录、区分不同用户的数据（计划、笔记、提醒等）
 */
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val username: String,
    val passwordHash: String,
    val displayName: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)


