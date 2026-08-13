package com.app.medcontrol.screen.Paciente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.app.medcontrol.components.GlassCard
import com.app.medcontrol.components.ProgressBarDinamica
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Composable
fun PacienteHomeScreen(
    viewModel: PacienteHomeScreenViewModel = hiltViewModel(),
    onNavigateToCadastroMed: () -> Unit,
    onNavigateToCadastroSinais: () -> Unit,
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is PacienteHomeScreenViewModel.HomeUiEvent.Logout -> onLogout()
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 16.dp
        ),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { Header(
            onLogoutClick = viewModel::logout,
            nomeUsuario = uiState.nomeUser) }
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

        item {
            SecaoAcompanhante(
                codigoVinculo = uiState.usuarioId.toString(),
                acompanhante = uiState.acompanhanteVinculado,
                onDesvincular = { id -> viewModel.desvincularAcompanhante(id) }
            )
        }
    }
}

@Composable
fun Header(
    onLogoutClick: () -> Unit,
    nomeUsuario: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ){
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GreetingText(nome = nomeUsuario)
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
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
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

@Composable
fun SecaoAcompanhante(
    codigoVinculo: String,
    acompanhante: com.app.medcontrol.data.entity.UsuarioEntity?,
    onDesvincular: (Int) -> Unit
) {
    var mostrarConfirmacao by remember { mutableStateOf(false) }

    GlassCard {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Acompanhante",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))

            if (acompanhante == null) {
                Text(
                    text = "Seu código de vínculo:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = codigoVinculo,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Compartilhe este código com seu acompanhante.",
                    style = MaterialTheme.typography.bodySmall,
                    color = com.app.medcontrol.ui.theme.TextSecondary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "[ Aguardando acompanhante ]",
                    style = MaterialTheme.typography.labelMedium,
                    color = com.app.medcontrol.ui.theme.AmberSignal,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "Acompanhante vinculado:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = acompanhante.nome,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ID: ${acompanhante.id}",
                            style = MaterialTheme.typography.bodySmall,
                            color = com.app.medcontrol.ui.theme.TextSecondary
                        )
                    }
                    IconButton(onClick = { mostrarConfirmacao = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Desvincular",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }

    if (mostrarConfirmacao && acompanhante != null) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { mostrarConfirmacao = false },
            title = { Text("Desvincular Acompanhante") },
            text = { Text("Tem certeza que deseja desvincular ${acompanhante.nome}? Ele perderá o acesso aos seus dados instantaneamente.") },
            confirmButton = {
                androidx.compose.material3.TextButton(
                    onClick = {
                        onDesvincular(acompanhante.id)
                        mostrarConfirmacao = false
                    }
                ) {
                    Text("Desvincular", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = { mostrarConfirmacao = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}
