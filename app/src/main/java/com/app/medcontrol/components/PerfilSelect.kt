package com.app.medcontrol.components

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.medcontrol.model.TipoUsuario

@Composable
fun PerfilSelect(
    tipoSelecionado: TipoUsuario,
    onTipoSelected: (TipoUsuario) -> Unit
) {

    val transition = updateTransition(
        targetState = tipoSelecionado,
        label = "PerfilTransition"
    )

    val bias by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 400) },
        label = "BiasAnimation"
    ) { state ->
        if (state == TipoUsuario.PACIENTE) -1f else 1f
    }

    val backgroundColor by transition.animateColor(
        transitionSpec = { tween(durationMillis = 400) },
        label = "ColorAnimation"
    ) { state ->
        if (state == TipoUsuario.PACIENTE)
            Color(0xFF4CAF50)
        else
            Color(0xFF673AB7)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .background(Color(0xFFE0E0E0), RoundedCornerShape(27.dp))
            .padding(4.dp)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()
                .align(BiasAlignment(bias, 0f))
                .background(backgroundColor, RoundedCornerShape(23.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTipoSelected(TipoUsuario.PACIENTE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Paciente",
                    color = if (tipoSelecionado == TipoUsuario.PACIENTE)
                        Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable { onTipoSelected(TipoUsuario.ACOMPANHANTE) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Acompanhante",
                    color = if (tipoSelecionado == TipoUsuario.ACOMPANHANTE)
                        Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}