@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.coursereviewplanner.ui.auth

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.coursereviewplanner.MainActivity
import com.example.coursereviewplanner.ui.theme.CourseReviewPlannerTheme

class AuthActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CourseReviewPlannerTheme {
                val vm: AuthViewModel = viewModel()

                AuthScreen(
                    viewModel = vm,
                    onLoginSuccess = {
                        // 跳转到主界面，并结束当前登录页
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: () -> Unit
) {
    val state = viewModel.uiState

    // 登录/注册成功后回调
    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            onLoginSuccess()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (state.isLoginMode) "登录" else "注册",
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "课程笔记与复习助手",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = state.username,
                onValueChange = viewModel::onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("手机号（即用户名）") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 登录模式下，根据是否使用验证码登录决定展示密码输入框
            if (!(state.isLoginMode && state.useSmsLogin)) {
                OutlinedTextField(
                    value = state.password,
                    onValueChange = viewModel::onPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("密码") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            // 注册模式下，或登录模式 + 验证码登录时，展示验证码输入与获取按钮
            if (!state.isLoginMode || (state.isLoginMode && state.useSmsLogin)) {
                OutlinedTextField(
                    value = state.smsCode,
                    onValueChange = viewModel::onSmsCodeChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("验证码") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = { viewModel.requestSmsCode() },
                    enabled = !state.isLoading
                ) {
                    Text(text = "获取验证码")
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            if (!state.isLoginMode) {
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = viewModel::onConfirmPasswordChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("确认密码") },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.displayName,
                    onValueChange = viewModel::onDisplayNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("昵称（可选）") },
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = { viewModel.submit() },
                enabled = !state.isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(text = if (state.isLoginMode) "登录" else "注册")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 仅在登录模式下展示「密码登录 / 验证码登录」切换
            if (state.isLoginMode) {
                Text(
                    text = if (state.useSmsLogin) "当前为验证码登录，点此切换为密码登录" else "当前为密码登录，点此切换为验证码登录",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable { viewModel.toggleLoginMethod() }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = if (state.isLoginMode) "还没有账号？点此注册" else "已有账号？点此登录",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .clickable { viewModel.toggleMode() }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthScreenPreview() {
    CourseReviewPlannerTheme {
        AuthScreen(
            viewModel = AuthViewModel(Application()),
            onLoginSuccess = {}
        )
    }
}


