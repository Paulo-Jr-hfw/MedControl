package com.app.medcontrol.screen.historico

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.components.LogItem
import com.app.medcontrol.model.ui.LogGeralUI
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun LogGeralScreen(
    viewModel: LogGeralViewModel = hiltViewModel(),
    onVoltar: () -> Unit,
) {
    val uiState = viewModel.uiState.collectAsState()

    LogGeralScreenContent(
        uiState = uiState.value,
        onVoltar = onVoltar
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogGeralScreenContent(
    uiState: LogGeralUI,
    onVoltar: () -> Unit
) {
    Scaffold(
        containerColor = Color.Transparent,
        contentWindowInsets = WindowInsets(0.dp),
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Interações") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.mensagemErro != null) {
                Text(uiState.mensagemErro, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (uiState.logsAgrupados.isEmpty()) {
                Text("Nenhum registro encontrado.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                        .padding(top = paddingValues.calculateTopPadding()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = paddingValues.calculateBottomPadding() + 16.dp
                    )
                ) {
                    val datasOrdenadas = uiState.logsAgrupados.keys.sortedDescending()

                    datasOrdenadas.forEach { data ->
                        item {
                            DataHeader(data = data)
                        }

                        items(uiState.logsAgrupados[data] ?: emptyList()) { log ->
                            LogItem(log = log)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataHeader(data: java.time.LocalDate) {
    val formatador = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale("pt", "BR"))
    Text(
        text = data.format(formatador).replaceFirstChar { it.uppercase() },
        style = MaterialTheme.typography.labelLarge,
        color = Color(0xFF78909C),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}
