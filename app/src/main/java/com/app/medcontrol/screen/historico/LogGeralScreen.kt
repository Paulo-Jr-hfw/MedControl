package com.app.medcontrol.screen.historico

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.data.entity.LogGeralEntity
import com.app.medcontrol.data.entity.StatusEvento
import com.app.medcontrol.data.entity.TipoEvento
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
        topBar = {
            TopAppBar(
                title = { Text("Histórico de Interações") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.mensagemErro != null) {
                Text(uiState.mensagemErro, color = Color.Red, modifier = Modifier.align(Alignment.Center))
            } else if (uiState.logsAgrupados.isEmpty()) {
                Text("Nenhum registro encontrado.", modifier = Modifier.align(Alignment.Center), color = Color.Gray)
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val datasOrdenadas = uiState.logsAgrupados.keys.sortedDescending()

                    datasOrdenadas.forEach { data ->
                        // Cabeçalho do Dia
                        item {
                            DataHeader(data = data)
                        }

                        // Itens do Dia
                        items(uiState.logsAgrupados[data] ?: emptyList()) { log ->
                            LogItemZigZag(log = log)
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

@Composable
fun LogItemZigZag(log: LogGeralEntity) {

    val alinhamentoADireita = log.tipoEvento == TipoEvento.MEDICAMENTO

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (alinhamentoADireita) Arrangement.End else Arrangement.Start
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.85f),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (alinhamentoADireita) 16.dp else 2.dp,
                bottomEnd = if (alinhamentoADireita) 2.dp else 16.dp
            ),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                val corStatus = when (log.status) {
                    StatusEvento.SUCESSO -> Color(0xFF26A69A)
                    StatusEvento.ATRASADO -> Color(0xFFFFA726)
                    StatusEvento.ALERTA -> Color(0xFFEF5350)
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(corStatus.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (log.tipoEvento == TipoEvento.MEDICAMENTO)
                            Icons.Default.MedicalServices else Icons.Default.MonitorHeart,
                        contentDescription = null,
                        tint = corStatus,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = log.titulo,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF37474F)
                        )
                        Text(
                            text = log.dataHora.format(DateTimeFormatter.ofPattern("HH:mm")),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                    }
                    Text(
                        text = log.descricao,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF546E7A)
                    )
                }
            }
        }
    }
}