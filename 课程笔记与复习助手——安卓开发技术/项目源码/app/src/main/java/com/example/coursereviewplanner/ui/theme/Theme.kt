package com.example.coursereviewplanner.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    secondary = BlueSecondaryDark,
    tertiary = BlueAccentDark,
    background = Color(0xFF0B1726),
    surface = Color(0xFF101A2A),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFFE3F2FD),
    onSurface = Color(0xFFE3F2FD)
)

private val LightColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    secondary = BlueSecondaryLight,
    tertiary = BlueAccentLight,
    background = Color(0xFFFFFFFF),          // 纯白背景
    surface = Color(0xFFFFFFFF),             // 大部分页面、卡片也是白底
    surfaceVariant = LightSurfaceVariant,    // 工具栏、次级区域的淡蓝背景
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF0D47A1),        // 文本：深蓝而不是纯黑，更柔和
    onSurface = Color(0xFF0D47A1),
    outline = LightOutline
)

@Composable
fun CourseReviewPlannerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // 锁定为自定义蓝白配色，不使用系统动态颜色，保证风格统一
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}