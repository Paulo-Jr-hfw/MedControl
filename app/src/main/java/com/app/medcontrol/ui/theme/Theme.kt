package com.app.medcontrol.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = MedicalTeal,
    secondary = TealTrack,
    background = TextPrimary,
    surface = TextPrimary
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalTeal,
    secondary = TealTrack,
    background = MintOffWhite,
    surface = GlassSurface,
    onPrimary = PureWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

private val CompanionColorScheme = lightColorScheme(
    primary = CompanionPrimary,
    secondary = LavenderLight,
    background = PurpleBase,
    surface = GlassSurface,
    onPrimary = PureWhite,
    onBackground = TextPrimary,
    onSurface = TextPrimary,
    onSurfaceVariant = TextSecondary
)

@Composable
fun MedControlTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    isCompanion: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        isCompanion -> CompanionColorScheme
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
