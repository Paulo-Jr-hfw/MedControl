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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.medcontrol.components.GlassCard
import com.app.medcontrol.components.MeshBackground
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.model.ui.DetalheMedicamentoUI
import com.app.medcontrol.ui.theme.MedicalTeal
import com.app.medcontrol.ui.theme.PureWhite
import com.app.medcontrol.util.DateTimeUtils
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
    state: DetalheMedicamentoUI,
    onVoltar: () -> Unit,
    onEditar: (Int) -> Unit,
    onMudarAba: (Int) -> Unit
) {
    MeshBackground {
        Scaffold(
            containerColor = Color.Transparent
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HeaderMedicamento(
                    medicamento = state.medicamento,
                    imagemArquivo = state.imagemArquivo,
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
}

@Composable
fun HeaderMedicamento(
    medicamento: MedicamentoEntity?,
    imagemArquivo: java.io.File?,
    onVoltar: () -> Unit,
    onEditar: (Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {

        if (imagemArquivo != null && imagemArquivo.exists()) {
            AsyncImage(
                model = imagemArquivo,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.5f)
                            )
                        )
                    )
            )
        } else {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(MedicalTeal, MedicalTeal.copy(alpha = 0.7f))
                        )
                    )
            ) {
                Icon(
                    imageVector = Icons.Default.MedicalServices,
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.Center)
                        .alpha(0.2f),
                    tint = PureWhite
                )
            }
        }


        IconButton(
            onClick = onVoltar,
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(PureWhite.copy(alpha = 0.3f), shape = CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar", tint = PureWhite)
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Text(
                text = medicamento?.nome ?: "Carregando...",
                style = MaterialTheme.typography.headlineMedium,
                color = PureWhite,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = medicamento?.dosagem ?: "",
                color = PureWhite.copy(alpha = 0.9f),
                style = MaterialTheme.typography.bodyLarge
            )
        }


        IconButton(
            onClick = { medicamento?.let { onEditar(it.id) } },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
                .background(PureWhite.copy(alpha = 0.3f), shape = CircleShape)
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = PureWhite)
        }
    }
}

@Composable
fun TabsMedicamento(
    abaSelecionada: Int,
    onMudarAba: (Int) -> Unit
) {
    GlassCard(
        modifier = Modifier.padding(16.dp)
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
            containerColor = if (selecionado) MedicalTeal else Color.Transparent,
            contentColor = if (selecionado) PureWhite else Color.Gray
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
                val ehEsquecido = registro.dataHoraTomado == null

                GlassCard {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .background(MedicalTeal.copy(alpha = 0.1f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = MedicalTeal, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(16.dp))

                        val horaPrevista = registro.dataHoraPrevista.format(DateTimeUtils.HH_MM)
                        if (ehEsquecido) {
                            Text(
                                text = "Previsto: $horaPrevista • Não tomado (Esquecido)",
                                color = Color(0xFFEF5350),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        } else {
                            val horaTomada = registro.dataHoraTomado.format(DateTimeUtils.HH_MM)
                            Text(
                                text = "Previsto: $horaPrevista • Tomado: $horaTomada",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
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
        GlassCard {
            Column(Modifier.padding(16.dp)) {
                Text("INSTRUÇÕES", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text(text = medicamento?.instrucoes ?: "Sem instruções adicionais", color = MaterialTheme.colorScheme.onSurface)
            }
        }


        GlassCard {
            Column(Modifier.padding(16.dp)) {
                Text("HORÁRIOS", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    medicamento?.horario?.forEach { hora ->
                        AssistChip(
                            onClick = {},
                            label = { Text(hora.format(DateTimeUtils.HH_MM)) },
                            modifier = Modifier.padding(end = 8.dp),
                            leadingIcon = { Icon(Icons.Default.Schedule, null, Modifier.size(18.dp)) },
                            colors = AssistChipDefaults.assistChipColors(
                                containerColor = Color.White.copy(alpha = 0.3f),
                                labelColor = MaterialTheme.colorScheme.primary,
                                leadingIconContentColor = MaterialTheme.colorScheme.primary
                            ),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }
            }
        }
    }
}
