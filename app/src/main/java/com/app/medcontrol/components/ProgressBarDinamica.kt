package com.app.medcontrol.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ProgressBarDinamica(progresso: Float) {
    Column {
        Text(text = "Seu progresso: ${(progresso * 100).toInt()}%")
        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}