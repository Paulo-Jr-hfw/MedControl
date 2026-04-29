package com.app.medcontrol.screen.detalhemed

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import com.app.medcontrol.data.entity.MedicamentoEntity
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DetalheMedicamentoScreen(
    viewModel: DetalheScreenViewModel = hiltViewModel(),
    onVoltar: () -> Unit,
    onEditar: (Int) -> Unit
) {

    val uiState by viewModel.uiState.collectAsState()

    DetalheMedicamentoContent(
        state = uiState,
        onVoltar = onVoltar,
        onEditar = onEditar,
        onMudarAba = viewModel::mudarAba
    )
}

@Composable
fun DetalheMedicamentoContent(
    state: DetalheMedicamentoUiState,
    onVoltar: () -> Unit,
    onEditar: (Int) -> Unit,
    onMudarAba: (Int) -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            HeaderMedicamento(
                medicamento = state.medicamento,
                onVoltar = onVoltar,
                onEditar = onEditar
            )

            TabsMedicamento(
                abaSelecionada = state.abaSelecionada,
                onMudarAba = onMudarAba
            )

            Crossfade(targetState = state.abaSelecionada) { aba ->
                when (aba) {
                    0 -> DetalhesConteudo(state.medicamento)
                    1 -> HistoricoConteudo(state.historico)
                }
            }
        }
    }
}

@Composable
fun HeaderMedicamento(
    medicamento: MedicamentoEntity?,
    onVoltar: () -> Unit,
    onEditar: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF4DB6AC), Color(0xFF80CBC4))
                )
            )
            .padding(16.dp)
    ) {
        // Botão Voltar
        IconButton(
            onClick = onVoltar,
            modifier = Modifier
                .align(Alignment.TopStart)
                .background(Color.White.copy(alpha = 0.3f), shape = CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = Color.White)
        }

        // Info do Medicamento
        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(bottom = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagem ou Placeholder
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.2f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    modifier = Modifier.padding(16.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = medicamento?.nome ?: "Carregando...",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = medicamento?.dosagem ?: "",
                    color = Color.White.copy(alpha = 0.8f)
                )
            }

            // Botão Editar
            IconButton(
                onClick = { medicamento?.let { onEditar(it.id) } },
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.3f), shape = CircleShape)
            ) {
                Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
            }
        }
    }
}

@Composable
fun TabsMedicamento(
    abaSelecionada: Int,
    onMudarAba: (Int) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE9ECEF))
    ) {
        Row (modifier = Modifier.padding(4.dp)) {
            TabItem("Detalhes", abaSelecionada == 0, Modifier.weight(1f)) {
                onMudarAba(0)
            }
            TabItem("Histórico", abaSelecionada == 1, Modifier.weight(1f)) {
                onMudarAba(1)
            }
        }
    }
}

@Composable
fun TabItem(titulo: String, selecionado: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selecionado) Color(0xFF26A69A) else Color.Transparent,
            contentColor = if (selecionado) Color.White else Color.Gray
        ),
        shape = RoundedCornerShape(8.dp),
        elevation = null
    ) {
        Text(titulo)
    }
}

@Composable
fun HistoricoConteudo(
    historicoAgrupado: Map<java.time.LocalDate, List<HistoricoMedicamentoEntity>>
) {
    if ( historicoAgrupado.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhum histórico encontrado", color = Color.Gray)
        }
        return
        }
    LazyColumn (
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        val datasOrdenadas = historicoAgrupado.keys.sortedDescending()

        datasOrdenadas.forEach { data ->
            item {
                val dataFormatada = data.dayOfWeek.getDisplayName(TextStyle.FULL,
                    Locale.forLanguageTag("pt-BR")
                ).uppercase() +
                        ", ${data.dayOfMonth} DE ${data.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("pt-BR")).uppercase()}"

                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(Icons.Default.CalendarMonth, null, modifier = Modifier.size(18.dp), tint = Color(0xFF78909C))
                    Spacer(Modifier.width(8.dp))
                    Text(text = dataFormatada, style = MaterialTheme.typography.labelLarge, color = Color(0xFF78909C), fontWeight = FontWeight.Bold)
                }
            }

            items(historicoAgrupado[data] ?: emptyList()) { registro ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(2.dp)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(Color(0xFFE0F2F1), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = Color(0xFF26A69A), modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))

                        val horaPrevista = registro.dataHoraPrevista.format(DateTimeFormatter.ofPattern("HH:mm"))
                        val horaTomada = registro.dataHoraTomado?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: "--:--"

                        Text(
                            text = "Previsto: $horaPrevista • Tomado: $horaTomada",
                            color = Color(0xFF78909C),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DetalhesConteudo(medicamento: MedicamentoEntity?) {
    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Card Instruções
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("INSTRUÇÕES", style = MaterialTheme.typography.labelLarge, color = Color(0xFF78909C), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(text = medicamento?.instrucoes ?: "Sem instruções adicionais", color = Color.DarkGray)
            }
        }

        // Card Horários
        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("HORÁRIOS", style = MaterialTheme.typography.labelLarge, color = Color(0xFF78909C), fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    medicamento?.horario?.forEach { hora ->
                        AssistChip(
                            onClick = {},
                            label = { Text(hora.toString()) },
                            modifier = Modifier.padding(end = 8.dp),
                            leadingIcon = { Icon(Icons.Default.Schedule, null, Modifier.size(18.dp)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color(0xFFE0F2F1),
                                labelColor = Color(0xFF26A69A),
                                leadingIconContentColor = Color(0xFF26A69A)
                            ),
                            border = null,
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}