package com.app.medcontrol.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.app.medcontrol.ui.theme.LimeLight
import com.app.medcontrol.ui.theme.MintBase
import com.app.medcontrol.ui.theme.TurquoiseDeep

@Composable
fun MeshBackground(
    baseColor: Color = MintBase,
    topSpotColor: Color = LimeLight,
    bottomSpotColor: Color = TurquoiseDeep,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(baseColor)
            .background(
                Brush.radialGradient(
                    colors = listOf(topSpotColor, Color.Transparent),
                    center = Offset(x = Float.POSITIVE_INFINITY, y = 0f),
                    radius = 700f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(bottomSpotColor, Color.Transparent),
                    center = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY),
                    radius = 800f
                )
            )
    ) {
        content()
    }
}
