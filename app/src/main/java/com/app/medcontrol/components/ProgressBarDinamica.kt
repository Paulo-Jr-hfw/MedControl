package com.app.medcontrol.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.55f) // Reduzido para 55% para transparecer mais o fundo!
        ),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp), // Garante que nenhuma sombra estrague o vidro
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.7f)) // Borda ligeiramente mais nítida para marcar o vidro
    ) {
        Row(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Bloco da Esquerda: Dados de peso variável
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "Progresso de hoje",
                    fontSize = 14.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                // Texto imponente: "04 / 10 concluídos"
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 32.sp,
                            color = TextPrimary
                        )
                        ) {
                            append(String.format("%02d", tomadas))
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

            // Bloco da Direita: Anel de Progresso Circular
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(80.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // 1. O trilho de fundo do anel (vazio)
                    drawCircle(
                        color = TealTrack,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                    // 2. O arco preenchido com o progresso real (Inicia no topo: -90 graus)
                    drawArc(
                        color = MedicalTeal,
                        startAngle = -90f,
                        sweepAngle = progresso * 360f,
                        useCenter = false,
                        style = Stroke(width = 8.dp.toPx(), cap = StrokeCap.Round)
                    )
                }

                // Percentagem sutil no centro do anel
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