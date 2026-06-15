package com.app.medcontrol.screen.medicamento

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.domain.usecase.ExcluirMedicamentoUseCase
import com.app.medcontrol.model.mapper.toUI
import com.app.medcontrol.model.ui.MedicamentoUI
import com.app.medcontrol.repository.MedicamentoRepository
import com.app.medcontrol.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicamentoScreenViewModel @Inject constructor(
    private val repository: MedicamentoRepository,
    private val excluirMedicamentoUseCase: ExcluirMedicamentoUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val _UiEvent = Channel<MedicamentoUiEvent>()
    val UiEvent = _UiEvent.receiveAsFlow()

    val listaMedicamentosUI = repository.getAllMedicamentosAtivosFlow(usuarioId)
        .map { lista ->
            lista.map { it.toUI() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun excluirMedicamento(medicamentoId: Int) {
        viewModelScope.launch {
            try {
                excluirMedicamentoUseCase(medicamentoId)
                _UiEvent.send(MedicamentoUiEvent.MostrarSnackbar("Medicamento removido com sucesso"))
            } catch ( e: Exception) {
                e.printStackTrace()
                _UiEvent.send(MedicamentoUiEvent.MostrarSnackbar("Erro ao excluir medicamento"))
            }
        }
    }
}

sealed class MedicamentoUiEvent {
    data class MostrarSnackbar(val mensagem: String) : MedicamentoUiEvent()
}

