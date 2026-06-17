package com.app.medcontrol.screen.Paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.R
import com.app.medcontrol.components.DoseItemHome
import com.app.medcontrol.components.ProgressBarDinamica
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun PacienteHomeScreen(
    viewModel: PacienteHomeScreenViewModel = hiltViewModel(),
    onNavigateToCadastroMed: () -> Unit,
    onNavigateToCadastroSinais: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Header(nomeUsuario = uiState.nomeUser) }
        item {
            ProgressBarDinamica(
                total = uiState.totalDosesDia,
                tomadas = uiState.dosesTomadas
            )
        }
        item {
            RowButtons(
                onNavigateToCadastroMed = onNavigateToCadastroMed,
                onNavigateToCadastroSinais = onNavigateToCadastroSinais
            )
        }
        secaoListaMedicamentos(
            doses = uiState.dosesPendentes,
            onConfirmar = { dose ->
                viewModel.marcarComoTomado(
                    registroId = dose.registroId,
                    medicamentoId = dose.medicamentoId,
                    usuarioId = dose.usuarioId
                )
            }
        )
    }
}

@Composable
fun Header(nomeUsuario: String) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        GreetingText(nome = nomeUsuario)
        DataAtualText()
    }
}

@Composable
fun GreetingText(nome:String) {
    Text(
        text = "Olá, $nome",
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun DataAtualText() {
    val hoje = LocalDate.now()
    val localeBR = java.util.Locale("pt", "BR")
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", localeBR)
    val dataFormatada = hoje.format(formatter).replaceFirstChar { it.uppercase() }

    Text(text = dataFormatada)
}


@Composable
fun RowButtons(
    onNavigateToCadastroMed: () -> Unit,
    onNavigateToCadastroSinais: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Card(
            onClick = onNavigateToCadastroMed,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "REMÉDIOS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Card(
            onClick = onNavigateToCadastroSinais,
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sinais_vitais),
                    contentDescription = null,
                    tint = com.app.medcontrol.ui.theme.CoralSignal, // Usa o coral sutil que combinamos
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SINAIS",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}


fun LazyListScope.secaoListaMedicamentos(
    doses: List<DoseAgendada>,
    onConfirmar: (DoseAgendada) -> Unit
) {
    item {
        Text(
            text = "Próximas doses de hoje",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }

    if (doses.isEmpty()) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Tudo em dia! Nenhuma dose pendente.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        }
    } else {

        items(
            items = doses,
            key = { it.registroId }
        ) { dose ->
            DoseItemHome(
                dose = dose,
                onCheckClick = { onConfirmar(dose) }
            )
        }
    }
}