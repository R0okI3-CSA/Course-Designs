package com.example.coursereviewplanner.ui.auth

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.coursereviewplanner.data.UserRepositoryProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AuthUiState(
    val isLoginMode: Boolean = true,
    val username: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val smsCode: String = "",
    val useSmsLogin: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val userRepository = UserRepositoryProvider.get(application)
    private val phoneRegex = Regex("^1[3-9]\\d{9}$")
    // 与云备份相同服务器，只是这里主要用于手机号验证码接口；如有变更可统一调整
    private val serverBaseUrl = "http://192.168.43.201:3000"

    var uiState by mutableStateOf(AuthUiState())
        private set

    fun toggleMode() {
        uiState = uiState.copy(
            isLoginMode = !uiState.isLoginMode,
            password = "",
            confirmPassword = "",
            smsCode = "",
            errorMessage = null,
            loginSuccess = false
        )
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value, errorMessage = null)
    }

    fun onPasswordChange(value: String) {
        uiState = uiState.copy(password = value, errorMessage = null)
    }

    fun onConfirmPasswordChange(value: String) {
        uiState = uiState.copy(confirmPassword = value, errorMessage = null)
    }

    fun onDisplayNameChange(value: String) {
        uiState = uiState.copy(displayName = value, errorMessage = null)
    }

    fun onSmsCodeChange(value: String) {
        uiState = uiState.copy(smsCode = value, errorMessage = null)
    }

    fun toggleLoginMethod() {
        // 仅在登录模式下切换密码登录 / 验证码登录
        if (!uiState.isLoginMode) return
        uiState = uiState.copy(
            useSmsLogin = !uiState.useSmsLogin,
            password = "",
            smsCode = "",
            errorMessage = null
        )
    }

    fun submit() {
        val username = uiState.username.trim()
        val password = uiState.password
        val smsCode = uiState.smsCode

        if (uiState.isLoginMode && uiState.useSmsLogin) {
            // 验证码登录：只要求手机号和验证码
            if (username.isEmpty() || smsCode.isEmpty()) {
                uiState = uiState.copy(errorMessage = "请输入手机号和验证码")
                return
            }
        } else {
            // 密码登录或注册：要求用户名和密码
            if (username.isEmpty() || password.isEmpty()) {
                uiState = uiState.copy(errorMessage = "请输入用户名和密码")
                return
            }
        }

        // 注册模式下：用户名必须是手机号、校验两次密码一致、验证码非空
        if (!uiState.isLoginMode) {
            if (!phoneRegex.matches(username)) {
                uiState = uiState.copy(errorMessage = "用户名必须是有效的手机号")
                return
            }
            if (password != uiState.confirmPassword) {
                uiState = uiState.copy(errorMessage = "两次输入的密码不一致")
                return
            }
            if (smsCode.isBlank()) {
                uiState = uiState.copy(errorMessage = "请输入收到的验证码")
                return
            }
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            if (uiState.isLoginMode) {
                if (uiState.useSmsLogin) {
                    // ===== 验证码登录路径 =====
                    val verifyOk = verifySmsCodeOnServer(username, smsCode)
                    if (!verifyOk) {
                        uiState = uiState.copy(
                            isLoading = false,
                            loginSuccess = false,
                            errorMessage = "验证码错误或已过期，请重新获取"
                        )
                        return@launch
                    }

                    val user = userRepository.loginByVerifiedUsername(username)
                    if (user == null) {
                        uiState = uiState.copy(
                            isLoading = false,
                            loginSuccess = false,
                            errorMessage = "该手机号尚未注册，请先完成注册"
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            loginSuccess = true,
                            errorMessage = null
                        )
                    }
                } else {
                    // ===== 原有密码登录路径 =====
                    val user = userRepository.login(username, password)
                    if (user == null) {
                        uiState = uiState.copy(
                            isLoading = false,
                            loginSuccess = false,
                            errorMessage = "用户名或密码错误"
                        )
                    } else {
                        uiState = uiState.copy(
                            isLoading = false,
                            loginSuccess = true,
                            errorMessage = null
                        )
                    }
                }
            } else {
                // ===== 注册：先服务器验证验证码，再走本地注册逻辑 =====
                val verifyOk = verifySmsCodeOnServer(username, smsCode)
                if (!verifyOk) {
                    uiState = uiState.copy(
                        isLoading = false,
                        loginSuccess = false,
                        errorMessage = "验证码错误或已过期，请重新获取"
                    )
                    return@launch
                }

                val user = userRepository.register(
                    username = username,
                    plainPassword = password,
                    displayName = uiState.displayName.ifBlank { null }
                )
                if (user == null) {
                    uiState = uiState.copy(
                        isLoading = false,
                        loginSuccess = false,
                        errorMessage = "该用户名已存在，请更换一个"
                    )
                } else {
                    // 注册成功后回到登录模式，由用户主动登录
                    uiState = uiState.copy(
                        isLoading = false,
                        isLoginMode = true,
                        password = "",
                        confirmPassword = "",
                        loginSuccess = false,
                        errorMessage = "注册成功，请使用该账号登录"
                    )
                }
            }
        }
    }

    /**
     * 请求服务器为当前手机号生成短信验证码。
     */
    fun requestSmsCode() {
        val phone = uiState.username.trim()
        if (!phoneRegex.matches(phone)) {
            uiState = uiState.copy(errorMessage = "请先输入合法的手机号（11 位）")
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            val ok = requestSmsCodeFromServer(phone)
            uiState = if (ok) {
                uiState.copy(
                    isLoading = false,
                    errorMessage = "验证码已发送（教学环境请查看服务器控制台或返回的 debugCode）"
                )
            } else {
                uiState.copy(
                    isLoading = false,
                    errorMessage = "验证码发送失败，请稍后重试"
                )
            }
        }
    }

    // ========== 与服务器交互的私有方法 ==========

    private suspend fun requestSmsCodeFromServer(phone: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("$serverBaseUrl/api/requestSmsCode")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }

                val body = JSONObject().apply { put("phone", phone) }.toString()
                conn.outputStream.use { os ->
                    os.write(body.toByteArray(Charsets.UTF_8))
                }

                val code = conn.responseCode
                if (code != HttpURLConnection.HTTP_OK) {
                    return@withContext false
                }

                val responseText = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val json = JSONObject(responseText)
                json.optBoolean("success", false)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    private suspend fun verifySmsCodeOnServer(phone: String, code: String): Boolean =
        withContext(Dispatchers.IO) {
            try {
                val url = URL("$serverBaseUrl/api/verifySmsCode")
                val conn = (url.openConnection() as HttpURLConnection).apply {
                    requestMethod = "POST"
                    connectTimeout = 5000
                    readTimeout = 5000
                    doOutput = true
                    setRequestProperty("Content-Type", "application/json; charset=utf-8")
                }

                val body = JSONObject().apply {
                    put("phone", phone)
                    put("code", code)
                }.toString()
                conn.outputStream.use { os ->
                    os.write(body.toByteArray(Charsets.UTF_8))
                }

                val respCode = conn.responseCode
                if (respCode != HttpURLConnection.HTTP_OK) {
                    return@withContext false
                }

                val responseText = conn.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
                val json = JSONObject(responseText)
                json.optBoolean("success", false)
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
}


