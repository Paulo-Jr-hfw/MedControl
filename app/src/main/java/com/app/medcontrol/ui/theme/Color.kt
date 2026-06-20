package com.app.medcontrol.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val MintOffWhite = Color(0xFFF5F8F7)
val MedicalTeal = Color(0xFF1A5F60)
val TealTrack = Color(0xFFE0ECEB)
val PureWhite = Color(0xFFFFFFFF)
val TextPrimary = Color(0xFF1C2D2C)
val TextSecondary = Color(0xFF708482)
val CoralSignal = Color(0xFFE57373)
val AmberSignal= Color(0xFFFFBF00)
val GlassSurface = Color(0x66FFFFFF)

val MintBase = Color(0xFFBFF0DB)
val LimeLight = Color(0xFFE3FCEF)
val TurquoiseDeep = Color(0xFF1ADBB1)

//Brushes
val Color.signalBorderBrush: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            this.copy(alpha = 0.5f), // 'this' refere-se à própria cor que chama a extensão
            Color.White.copy(alpha = 0.2f)
        )
    )

val Color.signalGlassBackground: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            this.copy(alpha = 0.18f),
            this.copy(alpha = 0.10f)
        )
    )
