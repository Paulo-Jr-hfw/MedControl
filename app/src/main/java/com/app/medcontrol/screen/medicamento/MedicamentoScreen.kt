package com.app.medcontrol.screen.medicamento


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.components.MedicamentoItem
import com.app.medcontrol.data.entity.MedicamentoEntity

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoScreen(
    viewModel: MedicamentoScreenViewModel = hiltViewModel(),
    onNavigateToCadastro: () -> Unit,
    onNavigateToDetalhes: (Int) -> Unit
) {
    val medicamentos by viewModel.listaMedicamentosUI.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var medicamentoParaExcluir by remember { mutableStateOf<MedicamentoEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.UiEvent.collect { event ->
            when (event) {
                is MedicamentoUiEvent.MostrarSnackbar -> {
                    snackbarHostState.showSnackbar(
                        event.mensagem,
                        duration = SnackbarDuration.Short
                    )
                }
            }
        }
    }

    DialogConfirmarExclusao(
        medicamento = medicamentoParaExcluir,
        onConfirmar = {
            medicamentoParaExcluir?.let { viewModel.excluirMedicamento(it) }
            medicamentoParaExcluir = null // Fecha o dialog ao limpar o estado
        },
        onDismiss = { medicamentoParaExcluir = null } // Fecha o dialog ao cancelar
    )

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }, // ONDE a snackbar aparece
        topBar = {
            TopAppBar(
                title = { Text("Meus Medicamentos",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToCadastro) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Adicionar Novo",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        if (medicamentos.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Nenhum Medicamento em Uso",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(
                    items = medicamentos,
                    key = { it.id }
                ) { medUi ->
                    MedicamentoItem(
                        medicamento = medUi,
                        onDeleteClick = {
                            // Opcional: Adicionar um Dialog de confirmação aqui
                            medicamentoParaExcluir = medUi.entityOriginal
                        },
                        onEditClick = {
                            onNavigateToDetalhes(medUi.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun DialogConfirmarExclusao(
    medicamento: MedicamentoEntity?,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    if (medicamento == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Medicamento") },
        text = {
            Text(
                "Deseja remover \"${medicamento.nome}\"?\n\n" +
                        "As doses de hoje serão canceladas, mas o histórico será preservado."
            )
        },
        confirmButton = {
            TextButton(
                onClick = onConfirmar,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Excluir", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}