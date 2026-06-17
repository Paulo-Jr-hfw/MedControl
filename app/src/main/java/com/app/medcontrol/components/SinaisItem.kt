package com.app.medcontrol.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.medcontrol.model.ui.SinaisUI


@Composable
fun SinaisItem(
    sinal: SinaisUI,
    onExcluirClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassCard (
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = sinal.dataFormatada,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                IconButton(
                    onClick = onExcluirClick,
                    modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))


            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                maxItemsInEachRow = 3
            ) {
                sinal.frequenciaCardiaca?.let {
                    SinalQuadrante(it, "bpm", Icons.Default.FavoriteBorder, Color(0xFFE57373), Color(0xFFFFEBEE))
                }

                sinal.pressaoArterial?.let {
                    SinalQuadrante(it, "mmHg", Icons.Default.Timeline, Color(0xFF4DB6AC), Color(0xFFE0F2F1))
                }

                sinal.oxigenacaoSanguinea?.let {
                    SinalQuadrante(it, "SpO2", Icons.Default.Air, Color(0xFF4DD0E1), Color(0xFFE0F7FA))
                }

                sinal.temperatura?.let {
                    SinalQuadrante(it, "°C", Icons.Default.Thermostat, Color(0xFFFFB74D), Color(0xFFFFF3E0))
                }

                sinal.glicose?.let {
                    SinalQuadrante(it, "mg/dL", Icons.Default.Opacity, Color(0xFFBA68C8), Color(0xFFF3E5F5))
                }
            }


            sinal.observacoes?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = it, fontSize = 13.sp, fontStyle = FontStyle.Italic, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun SinalQuadrante(
    valor: String,
    unidade: String,
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color
) {
    Surface(
        modifier = Modifier
            .widthIn(min = 100.dp)
            .padding(4.dp),
        shape = RoundedCornerShape(12.dp),
        color = containerColor
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            Text(text = valor, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = unidade, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun SinaisItemPreview() {
    val mintBase = Color(0xFFBFF0DB)
    val limeLight = Color(0xFFE3FCEF)
    val turquoiseDeep = Color(0xFF1ADBB1)


    val mockSinal = SinaisUI(
        sinaisId = 1,
        dataFormatada = "Quarta-feira, 17 de junho • 14:30",
        frequenciaCardiaca = "78",
        pressaoArterial = "12/8",
        oxigenacaoSanguinea = "98",
        temperatura = "36.6",
        glicose = "92",
        observacoes = "Aferido em repouso após o almoço."
    )

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
        Column {
            Text(
                text = "Histórico de Sinais Vitais",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1C2D2C),
                modifier = Modifier.padding(vertical = 12.dp)
            )

            SinaisItem(
                sinal = mockSinal,
                onExcluirClick = {}
            )
        }
    }
}