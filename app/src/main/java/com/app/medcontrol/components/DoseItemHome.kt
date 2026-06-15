package com.app.medcontrol.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.screen.Paciente.DoseAgendada
import com.app.medcontrol.ui.theme.AmberSignal
import com.app.medcontrol.ui.theme.CoralSignal


enum class CartaoStatus {
    NORMAL,
    ATRASADO,
    CRITICO
}

fun obterStatusDose(statusNoBanco: StatusConsumo): CartaoStatus {
    return when (statusNoBanco) {
        StatusConsumo.TOMADO -> CartaoStatus.NORMAL
        StatusConsumo.PENDENTE -> CartaoStatus.NORMAL
        StatusConsumo.ATRASADO -> CartaoStatus.ATRASADO
        StatusConsumo.ESQUECIDO -> CartaoStatus.CRITICO
    }
}

@Composable
fun DoseItemHome(
    dose: DoseAgendada,
    onCheckClick: () -> Unit
) {
    val status = obterStatusDose(dose.status)

    val statusColor = when (status) {
        CartaoStatus.CRITICO -> CoralSignal
        CartaoStatus.ATRASADO -> AmberSignal
        CartaoStatus.NORMAL -> Color.Transparent
    }

    GlassCard {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {


            if (status != CartaoStatus.NORMAL) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(6.dp)
                        .background(
                            color = statusColor,
                            shape = RoundedCornerShape(topStart = 24.dp, bottomStart = 24.dp)
                        )
                )
            } else {
                Spacer(modifier = Modifier.width(12.dp))
            }

            Row(
                modifier = Modifier
                    .padding(vertical = 16.dp, horizontal = 12.dp)
                    .weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {

                MedImage(
                    uri = dose.imagemUri,
                    size = 52.dp,
                    shape = CircleShape,
                    backgroundColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.4f)
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = dose.nomeMedicamento,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = dose.horarioAgendado.toString(),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (status != CartaoStatus.NORMAL) statusColor else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "•  ${dose.dosagem}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(
                    onClick = onCheckClick,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Confirmar dose",
                        tint = if (status != CartaoStatus.NORMAL) statusColor else MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
