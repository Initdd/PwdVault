package com.example.passmanager.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    background = eerieBlack,
    primary = pigmentedGreen,
    secondary = pakistanGreen,
    onSecondary = coolgrey,
    tertiary = lion,
    outline = teagreen,
    surface = black,
)

private val LightColorScheme = lightColorScheme(
    background = mintcream,
    primary = pakistanGreen,
    secondary = hunterGreen,
    tertiary = lion,
    outline = teagreen,
    surface = white,
)



@Composable
fun PassManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
