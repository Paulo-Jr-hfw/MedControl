package com.app.medcontrol.screen.acompanhante

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.repository.RegistroRepository
import com.app.medcontrol.repository.UsuarioRepository
import com.app.medcontrol.screen.Paciente.DoseAgendada
import com.app.medcontrol.screen.Paciente.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class AcompanhanteHomeScreenViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val registroRepository: RegistroRepository,
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
    }

    val uiState: StateFlow<HomeUiState> = combine(
        registroRepository.getDosesPendentesFlow(hoje),
        registroRepository.getTotalDosesDoDia(hoje),
        registroRepository.getDosesTomadasDoDia(hoje)
    ) { registrosComMed, total, tomadas ->
        val usuario = usuarioRepository.getUsuarioById(usuarioId)

        val listaDoses = registrosComMed.map { item ->
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
        }

        HomeUiState(
            nomeUser = usuario?.nome ?: "Acompanhante",
            dosesPendentes = listaDoses,
            totalDosesDia = total,
            dosesTomadas = tomadas,
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HomeUiState(isLoading = true)
    )

    fun logout() {
        viewModelScope.launch {
            _uiEvent.send(AcompanhanteUiEvent.Logout)
        }
    }
}
