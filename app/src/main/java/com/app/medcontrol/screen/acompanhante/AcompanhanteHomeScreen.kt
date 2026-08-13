package com.app.medcontrol.screen.acompanhante

import android.widget.Toast
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Link
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.components.DoseItemHome
import com.app.medcontrol.components.GlassCard
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
    val codigoInput by viewModel.codigoInput.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is AcompanhanteHomeScreenViewModel.AcompanhanteUiEvent.Logout -> onLogout()
                is AcompanhanteHomeScreenViewModel.AcompanhanteUiEvent.ShowError -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    if (!uiState.estaVinculado) {
        VincularPacienteScreen(
            codigo = codigoInput,
            onCodigoChange = viewModel::onCodigoChange,
            onVincular = viewModel::vincularPaciente,
            onLogout = viewModel::logout
        )
    } else {
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
                    nomeUsuario = uiState.nomePaciente,
                    onDesvincular = viewModel::desvincular
                )
            }
            item {
                ProgressBarDinamica(
                    total = uiState.totalDosesDia,
                    tomadas = uiState.dosesTomadas
                )
            }
            
            secaoListaMedicamentosAcompanhante(
                doses = uiState.dosesPaciente
            )
        }
    }
}

@Composable
fun VincularPacienteScreen(
    codigo: String,
    onCodigoChange: (String) -> Unit,
    onVincular: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Link,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Vincular Paciente",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Text(
            text = "Digite o código fornecido pelo paciente para acompanhar sua saúde.",
            style = MaterialTheme.typography.bodyMedium,
            color = com.app.medcontrol.ui.theme.TextSecondary,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        GlassCard {
            Column(modifier = Modifier.padding(16.dp)) {
                OutlinedTextField(
                    value = codigo,
                    onValueChange = onCodigoChange,
                    label = { Text("Código do Paciente") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onVincular,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("VINCULAR PACIENTE", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        IconButton(onClick = onLogout) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, null)
                Spacer(Modifier.size(8.dp))
                Text("Sair da conta")
            }
        }
    }
}

@Composable
fun AcompanhanteHeader(
    onLogoutClick: () -> Unit,
    nomeUsuario: String,
    onDesvincular: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            GreetingText(nome = nomeUsuario)
            DataAtualText()
            Text(
                text = "Modo Acompanhante",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
        
        IconButton(onClick = onDesvincular) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Desvincular",
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.6f)
            )
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
                isReadOnly = true
            )
        }
    }
}
