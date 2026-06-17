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
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MintBase)
            .background(
                Brush.radialGradient(
                    colors = listOf(LimeLight, Color.Transparent),
                    center = Offset(x = Float.POSITIVE_INFINITY, y = 0f),
                    radius = 700f
                )
            )
            .background(
                Brush.radialGradient(
                    colors = listOf(TurquoiseDeep, Color.Transparent),
                    center = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY),
                    radius = 800f
                )
            )
    ) {
        content()
    }
}