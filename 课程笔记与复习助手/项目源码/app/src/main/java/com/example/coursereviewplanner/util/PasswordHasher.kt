package com.example.coursereviewplanner.util

import java.security.MessageDigest

/**
 * 简单的密码哈希工具（仅用于本课程大作业示例）。
 *
 * 注意：真实上线产品需要使用更安全的方案（例如带盐的哈希、PBKDF2、bcrypt 等），
 * 这里为了便于理解和实现，仅使用单次 SHA-256。
 */
object PasswordHasher {

    /**
     * 将明文密码转换为 SHA-256 十六进制字符串。
     */
    fun hash(plainPassword: String): String {
        val bytes = plainPassword.toByteArray()
        val digest = MessageDigest.getInstance("SHA-256").digest(bytes)
        return digest.joinToString(separator = "") { byte ->
            "%02x".format(byte)
        }
    }

    /**
     * 校验输入密码是否与已保存的哈希匹配。
     */
    fun verify(plainPassword: String, hashed: String): Boolean {
        return hash(plainPassword) == hashed
    }
}


