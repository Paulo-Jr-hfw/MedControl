package com.app.medcontrol.screen.medicamento


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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicamentoScreen(
    viewModel: MedicamentoScreenViewModel = hiltViewModel(),
    onNavigateToCadastro: () -> Unit,
    onNavigateToDetalhes: (Int) -> Unit
) {
    val medicamentos by viewModel.listaMedicamentosUI.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var medicamentoIdParaExcluir by remember { mutableStateOf<Int?>(null) }
    var medicamentoNomeParaExcluir by remember { mutableStateOf<String?>(null) }

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
        medicamentoNome = medicamentoNomeParaExcluir,
        onConfirmar = {
            medicamentoIdParaExcluir?.let { viewModel.excluirMedicamento(it) }
            medicamentoIdParaExcluir = null
            medicamentoNomeParaExcluir = null
        },
        onDismiss = { 
            medicamentoIdParaExcluir = null
            medicamentoNomeParaExcluir = null
        }
    )
    Scaffold(
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Meus Medicamentos",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
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
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
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
                    .padding(top = paddingValues.calculateTopPadding()),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp
                )
            ) {
                items(
                    items = medicamentos,
                    key = { it.id }
                ) { medUi ->
                    MedicamentoItem(
                        medicamento = medUi,
                        onDeleteClick = {
                            medicamentoIdParaExcluir = medUi.id
                            medicamentoNomeParaExcluir = medUi.nome
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
    medicamentoNome: String?,
    onConfirmar: () -> Unit,
    onDismiss: () -> Unit
) {
    if (medicamentoNome == null) return

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Excluir Medicamento") },
        text = {
            Text(
                "Deseja remover \"$medicamentoNome\"?\n\n" +
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