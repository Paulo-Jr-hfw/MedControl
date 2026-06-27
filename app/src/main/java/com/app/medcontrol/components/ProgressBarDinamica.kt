package com.app.medcontrol.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.medcontrol.ui.theme.MedicalTeal
import com.app.medcontrol.ui.theme.TealTrack
import com.app.medcontrol.ui.theme.TextPrimary
import com.app.medcontrol.ui.theme.TextSecondary

@Composable
fun ProgressBarDinamica(total: Int, tomadas: Int) {

    val progresso = if (total > 0) tomadas.toFloat() / total.toFloat() else 0f

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Progresso de hoje",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )


                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            color = TextPrimary
                        )
                        ) {
                            append("$tomadas")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Light, fontSize = 20.sp, color = TextSecondary)) {
                            append(" / $total\n")
                        }
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Normal, fontSize = 14.sp, color = TextSecondary)) {
                            append("concluídos")
                        }
                    },
                    lineHeight = 24.sp
                )
            }


            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {

                    drawCircle(
                        color = TealTrack,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )

                    drawArc(
                        color = MedicalTeal,
                        startAngle = -90f,
                        sweepAngle = progresso * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }


                val percentagem = if (total > 0) (progresso * 100).toInt() else 0
                Text(
                    text = "$percentagem%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }
        }
    }
}