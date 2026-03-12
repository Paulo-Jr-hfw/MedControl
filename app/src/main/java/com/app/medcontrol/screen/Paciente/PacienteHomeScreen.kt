package com.app.medcontrol.screen.Paciente

import android.os.Build
import androidx.annotation.RequiresApi
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.R
import com.app.medcontrol.components.DoseItemHome
import com.app.medcontrol.components.MedicamentoItem
import com.app.medcontrol.components.MenuButton
import com.app.medcontrol.components.ProgressBarDinamica
import com.app.medcontrol.model.Medicamento
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PacienteHomeScreen(
    viewModel: PacienteHomeScreenViewModel = hiltViewModel(),
    onNavigateToCadastroMed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item{Header(nomeUsuario = uiState.nomeUser)}
        item{ProgressBar(total = uiState.totalDosesDia, tomadas = uiState.dosesTomadas)}
        item{RowButtons(onNavigateToCadastroMed = onNavigateToCadastroMed)}
        secaoListaMedicamentos(
            doses = uiState.dosesPendentes,
            onConfirmar = { registroId ->
                viewModel.marcarComoTomado(registroId)
            }
        )
    }
    }

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DataAtualText() {
    val hoje = LocalDate.now()
    val localeBR = java.util.Locale("pt", "BR")
    val formatter = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM", localeBR)
    val dataFormatada = hoje.format(formatter).replaceFirstChar { it.uppercase() }

    Text(text = dataFormatada)
}

@Composable
fun ProgressBar(total: Int, tomadas: Int) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("Progresso de hoje")
            ProgressBarDinamica(
                total = total,
                tomadas = tomadas
            )
        }
    }
}

@Composable
fun RowButtons(onNavigateToCadastroMed: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MenuButton(
            onClick = onNavigateToCadastroMed,
            label = "REMÉDIOS",
            icon = { Icon(Icons.Default.Add, contentDescription = null) },
            modifier = Modifier.weight(1f)
        )
        MenuButton(
            onClick = { /* Navegar */ },
            label = "SINAIS",
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sinais_vitais),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )
            },
            modifier = Modifier.weight(1f)
        )
    }
}


fun LazyListScope.secaoListaMedicamentos(
    doses: List<DoseAgendada>,
    onConfirmar: (Int) -> Unit
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
                onCheckClick = { id -> onConfirmar(id) }
            )
        }
    }
}