package com.app.medcontrol.components

import com.app.medcontrol.R
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.screen.Paciente.DoseAgendada
import java.time.LocalTime


enum class CartaoStatus {
    NORMAL,
    ATRASADO,
    CRITICO
}

@RequiresApi(Build.VERSION_CODES.O)
fun obterStatusDose(horarioAgendado: LocalTime): CartaoStatus {
    val agora = LocalTime.now()
    val umaHoraAtras = agora.minusHours(1)
    val dozeHorasAtras = agora.minusHours(12)

    return when {
        horarioAgendado.isBefore(dozeHorasAtras) -> CartaoStatus.CRITICO
        horarioAgendado.isBefore(umaHoraAtras) -> CartaoStatus.ATRASADO
        else -> CartaoStatus.NORMAL
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DoseItemHome(
    dose: DoseAgendada,
    onCheckClick: (Int) -> Unit
) {
    val status = obterStatusDose(dose.horarioAgendado)

    // Define a cor baseada no status
    val containerColor = when (status) {
        CartaoStatus.CRITICO -> Color(0xFFFFEBEE) // Vermelho bem claro
        CartaoStatus.ATRASADO -> Color(0xFFFFF9C4) // Amarelo claro
        CartaoStatus.NORMAL -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (status) {
        CartaoStatus.CRITICO -> Color(0xFFB71C1C) // Texto Vermelho escuro
        CartaoStatus.ATRASADO -> Color(0xFFF57F17) // Texto Laranja/Amarelo escuro
        CartaoStatus.NORMAL -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Foto do medicamento (Se houver)
            Card(
                modifier = Modifier.size(60.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                AsyncImage(
                    model = dose.imagemUri,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.ic_launcher_foreground), // Adicione um placeholder
                    modifier = Modifier.fillMaxSize()
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp)
            ) {
                Text(
                    text = dose.nomeMedicamento,
                    fontWeight = FontWeight.Bold,
                    color = contentColor
                )
                Text(
                    text = "${dose.horarioAgendado} • ${dose.dosagem}",
                    style = MaterialTheme.typography.bodySmall,
                    color = contentColor
                )

                if (status != CartaoStatus.NORMAL) {
                    Text(
                        text = if (status == CartaoStatus.CRITICO) "MUITO ATRASADO" else "ATRASADO",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = contentColor
                    )
                }
            }

            IconButton(
                onClick = { onCheckClick(dose.registroId) },
                colors = IconButtonDefaults.iconButtonColors(contentColor = contentColor)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Confirmar dose",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "Normal")
@Composable
private fun PreviewNormal() {
    DoseItemHome(
        dose = DoseAgendada(1, "Dipirona", "1 comprimido", LocalTime.now().plusHours(2), StatusConsumo.PENDENTE, null),
        onCheckClick = {}
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, name = "Atrasado")
@Composable
private fun PreviewAtrasado() {
    DoseItemHome(
        dose = DoseAgendada(1, "Dipirona", "1 comprimido", LocalTime.now().minusHours(2), StatusConsumo.PENDENTE, null),
        onCheckClick = {}
    )
}
