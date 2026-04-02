package com.app.medcontrol.screen.cadastrosinais

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CadastroSinaisScreen(
    viewModel: CadastroSinaisViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is CadastroSinaisViewModel.UiEvent.CadastroSucesso -> onNavigateBack()
                is CadastroSinaisViewModel.UiEvent.ShowError -> println(event.message)
            }
            }
        }

    Scaffold(
        topBar = { /* Sua TopBar aqui */ }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Registro de Sinais Vitais",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            val campos = listOf(
                "FC (bpm)" to { v: String -> viewModel.onFcChange(v) } to uiState.fc,
                "SpO2 (%)" to { v: String -> viewModel.onSpo2Change(v) } to uiState.spo2,
                "PA Sistólica" to { v: String -> viewModel.onPaSistolicaChange(v) } to uiState.paSistolica,
                "PA Diastólica" to { v: String -> viewModel.onPaDiastolicaChange(v) } to uiState.paDiastolica,
                "Temp (°C)" to { v: String -> viewModel.onTemperaturaChange(v) } to uiState.temperatura,
                "Glicose" to { v: String -> viewModel.onGlicoseChange(v) } to uiState.glicose
            )
            campos.chunked(2).forEach { parDeCampos ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    parDeCampos.forEach { campo ->
                        OutlinedTextField(
                            value = campo.second,
                            onValueChange = campo.first.second,
                            label = { Text(campo.first.first) },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Campo de Observações
            OutlinedTextField(
                value = uiState.observacoes,
                onValueChange = { viewModel.onObservacoesChange(it) },
                label = { Text("Observações") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.weight(1f)) // Empurra os botões para o final

            // BOTÕES LADO A LADO
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = { viewModel.SalvarSinais() },
                    modifier = Modifier.weight(1f),
                    enabled = !uiState.isLoading // Desabilita enquanto salva
                ) {
                    if (uiState.isLoading) {
                        // Se estiver carregando, mostra um indicador de progresso
                    } else {
                        Text("Salvar")
                    }
                }
            }
        }
    }
}