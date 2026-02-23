package com.app.medcontrol.components

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SelecaoHorarios(
    horarios: List<LocalTime>,
    indexEditando: Int?,
    onSetEditando: (Int?) -> Unit,
    onAdicionarHorario: () -> Unit,
    onAtualizarHorario: (Int, LocalTime) -> Unit,
    onRemoverHorario: (Int) -> Unit
) {

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Horários", style = MaterialTheme.typography.titleMedium)

            TextButton(onClick = onAdicionarHorario) {
                Text("+ Adicionar horário")
            }
        }

        horarios.forEachIndexed { index, horario ->
            HorarioItem(
                index = index,
                horario = horario,
                mostrarBotaoRemover = index > 0,
                estaSendoEditado = indexEditando == index,
                onEditar = { onSetEditando(index) },
                onFechar = { onSetEditando(null) },
                onHorarioChange = { novo -> onAtualizarHorario(index, novo) },
                onRemover = { onRemoverHorario(index) }
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun HorarioItem(
    index: Int,
    horario: LocalTime,
    mostrarBotaoRemover: Boolean,
    estaSendoEditado: Boolean,
    onEditar: () -> Unit,
    onFechar: () -> Unit,
    onHorarioChange: (LocalTime) -> Unit,
    onRemover: () -> Unit
) {
    var horaTemp by remember(horario) { mutableStateOf(horario.hour) }
    var minutoTemp by remember(horario) { mutableStateOf(horario.minute) }

    Column {
        OutlinedCard(
            onClick = onEditar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Schedule, null)
                Spacer(Modifier.width(12.dp))
                Text(
                    text = "%02d:%02d".format(horario.hour, horario.minute),
                    modifier = Modifier.weight(1f)
                )
                if (mostrarBotaoRemover) {
                    IconButton(onClick = onRemover) {
                        Icon(Icons.Default.Close, "Remover")
                    }
                }
            }
        }

        if (estaSendoEditado) {

            androidx.compose.ui.window.Dialog(onDismissRequest = onFechar) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Selecionar Horário", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(16.dp))

                        Row(horizontalArrangement = Arrangement.Center) {
                            WheelTimePicker(
                                items = (0..23).map { it.toString().padStart(2, '0') },
                                initialIndex = horaTemp,
                                onItemSelected = { horaTemp = it.toInt() }
                            )
                            Text(":", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(horizontal = 8.dp))
                            WheelTimePicker(
                                items = (0..59).map { it.toString().padStart(2, '0') },
                                initialIndex = minutoTemp,
                                onItemSelected = { minutoTemp = it.toInt() }
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = onFechar) { Text("Cancelar") }
                            TextButton(onClick = {
                                onHorarioChange(LocalTime.of(horaTemp, minutoTemp))
                                onFechar()
                            }) { Text("Confirmar") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WheelTimePicker(
    items: List<String>,
    initialIndex: Int,
    onItemSelected: (String) -> Unit
) {
    val itemHeight = 40.dp
    val numberOfVisibleItems = 3
    val containerHeight = itemHeight * numberOfVisibleItems

    val state = rememberLazyListState(initialFirstVisibleItemIndex = initialIndex)
    val snapFlingBehavior = rememberSnapFlingBehavior(lazyListState = state)

    Box(
        modifier = Modifier
            .width(50.dp)
            .height(containerHeight),
        contentAlignment = Alignment.Center
    ) {

        Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
            Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.LightGray))
            Spacer(modifier = Modifier.height(itemHeight))
            Spacer(modifier = Modifier.height(1.dp).fillMaxWidth().background(Color.LightGray))
        }

        LazyColumn(
            state = state,
            flingBehavior = snapFlingBehavior,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            item { Spacer(modifier = Modifier.height(itemHeight)) }

            itemsIndexed(items) { index, item ->

                val isSelected = state.firstVisibleItemIndex == index

                Box(
                    modifier = Modifier.fillMaxWidth().height(itemHeight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        style = if (isSelected) MaterialTheme.typography.titleLarge
                        else MaterialTheme.typography.bodyMedium,
                        color = if (isSelected) MaterialTheme.colorScheme.primary
                        else Color.Gray
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(itemHeight)) }
        }
    }

    LaunchedEffect(state.firstVisibleItemIndex) {
        if (state.firstVisibleItemIndex < items.size) {
            onItemSelected(items[state.firstVisibleItemIndex])
        }
    }
}