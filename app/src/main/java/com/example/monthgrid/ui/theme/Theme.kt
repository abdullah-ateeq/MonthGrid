package com.example.monthgrid.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.monthgrid.model.ThemeMode

private val DarkColorScheme = darkColorScheme(
    primary = SlateBluePrimaryDark,
    onPrimary = SlateBlueOnPrimaryDark,
    primaryContainer = SlateBlueContainerDark,
    onPrimaryContainer = SlateBlueOnContainerDark,
    secondary = WarmGraphiteSecondaryDark,
    onSecondary = WarmGraphiteOnSecondaryDark,
    secondaryContainer = WarmGraphiteContainerDark,
    onSecondaryContainer = WarmGraphiteOnContainerDark,
    tertiary = TealAccentTertiaryDark,
    onTertiary = TealAccentOnTertiaryDark,
    tertiaryContainer = TealAccentContainerDark,
    onTertiaryContainer = TealAccentOnContainerDark,
    background = SurfaceDark,
    onBackground = OnSurfaceDark,
    surface = SurfaceContainerDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
    primary = SlateBluePrimaryLight,
    onPrimary = SlateBlueOnPrimaryLight,
    primaryContainer = SlateBlueContainerLight,
    onPrimaryContainer = SlateBlueOnContainerLight,
    secondary = WarmGraphiteSecondaryLight,
    onSecondary = WarmGraphiteOnSecondaryLight,
    secondaryContainer = WarmGraphiteContainerLight,
    onSecondaryContainer = WarmGraphiteOnContainerLight,
    tertiary = TealAccentTertiaryLight,
    onTertiary = TealAccentOnTertiaryLight,
    tertiaryContainer = TealAccentContainerLight,
    onTertiaryContainer = TealAccentOnContainerLight,
    background = SurfaceLight,
    onBackground = OnSurfaceLight,
    surface = SurfaceContainerLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    outline = OutlineLight
)

@Composable
fun MonthGridTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> systemDark
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

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
