package com.app.medcontrol.screen.acompanhante

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.domain.usecase.VincularAcompanhanteUseCase
import com.app.medcontrol.repository.RegistroRepository
import com.app.medcontrol.repository.UsuarioRepository
import com.app.medcontrol.repository.VinculoRepository
import com.app.medcontrol.screen.Paciente.DoseAgendada
import com.app.medcontrol.service.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class AcompanhanteHomeScreenViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val registroRepository: RegistroRepository,
    private val vinculoRepository: VinculoRepository,
    private val sessionManager: SessionManager,
    private val vincularUseCase: VincularAcompanhanteUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = savedStateHandle.get<Int>("usuarioId")
        ?: savedStateHandle.get<String>("usuarioId")?.toInt()
        ?: throw IllegalArgumentException("usuarioId é obrigatório")
    
    private val hoje = LocalDate.now()

    private val _uiEvent = Channel<AcompanhanteUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class AcompanhanteUiEvent {
        object Logout : AcompanhanteUiEvent()
        data class ShowError(val message: String) : AcompanhanteUiEvent()
    }

    private val _codigoInput = MutableStateFlow("")
    val codigoInput = _codigoInput.stateIn(viewModelScope, SharingStarted.Eagerly, "")

    val uiState: StateFlow<AcompanhanteHomeUiState> = vinculoRepository.getVinculoPorAcompanhante(usuarioId)
        .flatMapLatest { vinculo ->
            if (vinculo == null) {
                flowOf(AcompanhanteHomeUiState(estaVinculado = false, isLoading = false))
            } else {
                val pId = vinculo.pacienteId
                
                combine(
                    usuarioRepository.getUsuarioById(pId).let { flowOf(it) },
                    registroRepository.getDosesPendentesFlow(pId, hoje),
                    registroRepository.getTotalDosesDoDia(pId, hoje),
                    registroRepository.getDosesTomadasDoDia(pId, hoje)
                ) { paciente, doses, total, tomadas ->
                    AcompanhanteHomeUiState(
                        estaVinculado = true,
                        pacienteId = pId,
                        nomePaciente = paciente?.nome ?: "Paciente",
                        dosesPaciente = doses.map { item ->
                            DoseAgendada(
                                registroId = item.registro.id,
                                nomeMedicamento = item.medicamento.nome,
                                dosagem = item.medicamento.dosagem,
                                horarioAgendado = item.registro.horarioAgendado,
                                status = item.registro.status,
                                imagemUri = item.medicamento.imagemUri,
                                medicamentoId = item.medicamento.id,
                                usuarioId = item.medicamento.usuarioId
                            )
                        },
                        totalDosesDia = total,
                        dosesTomadas = tomadas,
                        isLoading = false
                    )
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = AcompanhanteHomeUiState(isLoading = true)
        )

    fun onCodigoChange(novo: String) {
        _codigoInput.value = novo
    }

    fun vincularPaciente() {
        val pId = _codigoInput.value.toIntOrNull()
        if (pId == null) {
            viewModelScope.launch { _uiEvent.send(AcompanhanteUiEvent.ShowError("Código inválido")) }
            return
        }

        viewModelScope.launch {
            val result = vincularUseCase(pId, usuarioId)
            result.onFailure { 
                _uiEvent.send(AcompanhanteUiEvent.ShowError(it.message ?: "Erro ao vincular"))
            }
        }
    }

    fun desvincular() {
        viewModelScope.launch {
            val pId = uiState.value.pacienteId
            if (pId != 0) {
                vinculoRepository.desvincular(pId, usuarioId)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _uiEvent.send(AcompanhanteUiEvent.Logout)
        }
    }
}

data class AcompanhanteHomeUiState(
    val estaVinculado: Boolean = false,
    val pacienteId: Int = 0,
    val nomePaciente: String = "",
    val isLoading: Boolean = false,
    val dosesPaciente: List<DoseAgendada> = emptyList(),
    val totalDosesDia: Int = 0,
    val dosesTomadas: Int = 0
)
