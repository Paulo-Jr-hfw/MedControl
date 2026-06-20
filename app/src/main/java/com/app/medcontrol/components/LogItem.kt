package com.app.medcontrol.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.medcontrol.data.entity.LogGeralEntity
import com.app.medcontrol.data.entity.StatusEvento
import com.app.medcontrol.data.entity.TipoEvento
import com.app.medcontrol.ui.theme.MedControlTheme
import com.app.medcontrol.util.DateTimeUtils
import java.time.LocalDateTime

@Composable
fun LogItem(log: LogGeralEntity) {
    val alinhamentoADireita = log.tipoEvento == TipoEvento.MEDICAMENTO

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (alinhamentoADireita) Arrangement.End else Arrangement.Start
    ) {
        GlassCard(
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                val corStatus = when (log.status) {
                    StatusEvento.SUCESSO -> Color(0xFF26A69A)
                    StatusEvento.ATRASADO -> Color(0xFFFFA726)
                    StatusEvento.ALERTA -> Color(0xFFEF5350)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(corStatus.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (log.tipoEvento == TipoEvento.MEDICAMENTO)
                            Icons.Default.MedicalServices else Icons.Default.MonitorHeart,
                        contentDescription = null,
                        tint = corStatus,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = log.titulo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = log.dataHora.format(DateTimeUtils.HH_MM),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = log.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun LogItemPreview() {
    // Paleta oficial do Mesh Background de referência
    val mintBase = Color(0xFFBFF0DB)
    val limeLight = Color(0xFFE3FCEF)
    val turquoiseDeep = Color(0xFF1ADBB1)

    MedControlTheme(darkTheme = false) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(mintBase)
                .background(
                    Brush.radialGradient(
                        colors = listOf(limeLight, Color.Transparent),
                        center = Offset(x = Float.POSITIVE_INFINITY, y = 0f),
                        radius = 700f
                    )
                )
                .background(
                    Brush.radialGradient(
                        colors = listOf(turquoiseDeep, Color.Transparent),
                        center = Offset(x = Float.POSITIVE_INFINITY, y = Float.POSITIVE_INFINITY),
                        radius = 800f
                    )
                )
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "Histórico de hoje",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1C2D2C),
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                // Item alinhado à DIREITA (TipoEvento.MEDICAMENTO)
                LogItem(
                    log = LogGeralEntity(
                        id = 1,
                        titulo = "Dipirona 500mg",
                        descricao = "Agendado: 08:00 • Tomado: 08:05",
                        dataHora = LocalDateTime.now(),
                        tipoEvento = TipoEvento.MEDICAMENTO,
                        status = StatusEvento.SUCESSO,
                        usuarioId = 1
                    )
                )

                // Item alinhado à ESQUERDA (TipoEvento.SINAIS)
                LogItem(
                    log = LogGeralEntity(
                        id = 2,
                        titulo = "Aferição de Sinais",
                        descricao = "Sinais vitais verificados com sucesso às 10:30.",
                        dataHora = LocalDateTime.now(),
                        tipoEvento = TipoEvento.SINAL, // Ajustado para corresponder ao seu enum
                        status = StatusEvento.ALERTA,
                        usuarioId = 1
                    )
                )
            }
        }
    }
}
