package com.app.medcontrol.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.medcontrol.model.ui.SinaisUI


@Composable
fun SinaisItem(
    sinal: SinaisUI,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Data e Hora no topo
            Text(
                text = sinal.dataFormatada,
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // O GRID MÁGICO: FlowRow
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

            // Observações ao final
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