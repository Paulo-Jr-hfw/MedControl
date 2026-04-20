package com.app.medcontrol.screen.sinais

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.components.SinaisItem
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinaisScreen(
    viewModel: SinaisScreenViewModel = hiltViewModel(),
    onNavigateToManual: () -> Unit
) {
    val listaSinais by viewModel.listaSinaisUI.collectAsState()
    val mostrarBottomSheet by viewModel.mostrarBottomSheet.collectAsState()
    val loadingRelogio by viewModel.loadingRelogio.collectAsState()

    val sheetState = rememberModalBottomSheetState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    val launcher = rememberHealthPermissionsLauncher(
        onSuccess = { viewModel.importarDadosRelogio() },
        onError = {
            scope.launch { snackbarHostState.showSnackbar("Permissão negada.") }
        }
    )

    if (loadingRelogio) {
        Dialog(onDismissRequest = { }) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFF57C00))
            }
        }
    }


    LaunchedEffect(Unit){
        viewModel.uiEvent.collect { event ->
            when (event) {
                is SinaisUiEvent.MostrarSnackbar -> {
                    snackbarHostState.showSnackbar(
                        event.mensagem,
                        duration = SnackbarDuration.Short
                    )
                }
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            SinaisTopBar(onRegistrarClick = { viewModel.abrirBottomSheet() })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            items(listaSinais) { sinal ->
                SinaisItem(
                    sinal = sinal,
                    onExcluirClick = { viewModel.excluirSinal(sinal.sinaisId) }
                )
            }
        }
        if (mostrarBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.fecharBottomSheet() },
                sheetState = sheetState,
                containerColor = Color.White
            ) {
                // Conteúdo da Escolha
                SelecaoRegistroContent(
                    onManualClick = {
                        viewModel.fecharBottomSheet()
                        onNavigateToManual()
                    },
                    onRelogioClick = {
                        viewModel.fecharBottomSheet()
                        viewModel.verificarERegistrarSinaisPeloRelogio(
                            onSolicitarPermissao = {
                                launcher.launch(viewModel.healthConnectManager.permissoesNecessarias)
                            }
                        )
                    }
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SinaisTopBar(onRegistrarClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = null,
                    tint = Color(0xFFF57C00)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "Sinais", fontWeight = FontWeight.Bold)
            }
        },
        actions = {
            Button(
                onClick =  onRegistrarClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF57C00)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text( "registrar")
            }
        }
    )
}

@Composable
fun SelecaoRegistroContent(
    onManualClick: () -> Unit,
    onRelogioClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 40.dp, top = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Como deseja registrar?",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Botão Smartwatch (O Diferencial)
        OutlinedCard(
            onClick = onRelogioClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFFF57C00))
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Watch, // Ícone de relógio
                    contentDescription = null,
                    tint = Color(0xFFF57C00),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Usar Smartwatch", fontWeight = FontWeight.Bold)
                    Text("Sincronizar batimentos e SpO2 agora", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botão Manual
        OutlinedCard(
            onClick = onManualClick,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.EditNote,
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Registro Manual", fontWeight = FontWeight.Bold)
                    Text("Digitar os valores manualmente", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun rememberHealthPermissionsLauncher(
    onSuccess: () -> Unit,
    onError: () -> Unit
) = rememberLauncherForActivityResult(
    PermissionController.createRequestPermissionResultContract()
) { granted ->
    if (granted.isNotEmpty()) {
        onSuccess()
    } else {
        onError()
    }
}