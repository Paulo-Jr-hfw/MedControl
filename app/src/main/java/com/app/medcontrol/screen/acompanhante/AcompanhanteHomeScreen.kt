package com.app.medcontrol.screen.acompanhante

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.components.DoseItemHome
import com.app.medcontrol.components.ProgressBarDinamica
import com.app.medcontrol.screen.Paciente.DataAtualText
import com.app.medcontrol.screen.Paciente.DoseAgendada
import com.app.medcontrol.screen.Paciente.GreetingText

@Composable
fun AcompanhanteHomeScreen(
    viewModel: AcompanhanteHomeScreenViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AcompanhanteHomeScreenViewModel.AcompanhanteUiEvent.Logout -> onLogout()
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            AcompanhanteHeader(
                onLogoutClick = viewModel::logout,
                nomeUsuario = uiState.nomeUser
            )
        }
        item {
            ProgressBarDinamica(
                total = uiState.totalDosesDia,
                tomadas = uiState.dosesTomadas
            )
        }
        
        secaoListaMedicamentosAcompanhante(
            doses = uiState.dosesPendentes
        )
    }
}

@Composable
fun AcompanhanteHeader(
    onLogoutClick: () -> Unit,
    nomeUsuario: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GreetingText(nome = "$nomeUsuario (Acompanhante)")
            DataAtualText()
        }
        IconButton(onClick = onLogoutClick) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = "Sair do aplicativo"
            )
        }
    }
}

fun LazyListScope.secaoListaMedicamentosAcompanhante(
    doses: List<DoseAgendada>
) {
    item {
        Text(
            text = "Status de medicação do Paciente",
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
                    text = "Tudo em dia com o paciente!",
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
                onCheckClick = { /* Ação desabilitada para acompanhante */ }
            )
        }
    }
}
