package com.app.medcontrol.screen.medicamento

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.domain.usecase.ExcluirMedicamentoUseCase
import com.app.medcontrol.model.mapper.toUI
import com.app.medcontrol.repository.MedicamentoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val validaAcessoUseCase: com.app.medcontrol.domain.usecase.ValidaAcessoPacienteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val pacienteId: Int = checkNotNull(savedStateHandle["pacienteId"])
    private val _UiEvent = Channel<MedicamentoUiEvent>()
    val UiEvent = _UiEvent.receiveAsFlow()

    private val _isAuthorized = MutableStateFlow<Boolean?>(null)

    val listaMedicamentosUI = repository.getAllMedicamentosAtivosFlow(pacienteId)
        .map { lista ->
            lista.map { it.toUI() }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        viewModelScope.launch {
            if (!validaAcessoUseCase(pacienteId)) {
                _UiEvent.send(MedicamentoUiEvent.MostrarSnackbar("Acesso negado ao paciente solicitado."))
                _isAuthorized.value = false
            } else {
                _isAuthorized.value = true
            }
        }
    }

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

