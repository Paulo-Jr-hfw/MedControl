package com.app.medcontrol.screen.cadastrosinais

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.SinaisDao
import com.app.medcontrol.data.entity.SinaisEntity
import com.app.medcontrol.screen.cadastromed.CadastroMedScreenViewModel.UiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CadastroSinaisViewModel @Inject constructor(
    private val sinaisDao: SinaisDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val _uiState = MutableStateFlow(CadastroSinaisUiState())
    val uiState: StateFlow<CadastroSinaisUiState> = _uiState.asStateFlow()

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        object CadastroSucesso : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }

    fun onFcChange(novoFc: String) {
        _uiState.update { it.copy(fc = novoFc) }
    }
    fun onPaSistolicaChange(novoPaSistolica: String) {
        _uiState.update { it.copy(paSistolica = novoPaSistolica) }
    }
    fun onPaDiastolicaChange(novoPaDiastolica: String) {
        _uiState.update { it.copy(paDiastolica = novoPaDiastolica) }
    }
    fun onSpo2Change(novoSpo2: String) {
        _uiState.update { it.copy(spo2 = novoSpo2) }
    }
    fun onGlicoseChange(novoGlicose: String) {
        _uiState.update { it.copy(glicose = novoGlicose) }
    }
    fun onTemperaturaChange(novaTemperatura: String) {
        _uiState.update { it.copy(temperatura = novaTemperatura)}
    }
    fun onObservacoesChange(novasObservacoes: String) {
        _uiState.update { it.copy(observacoes = novasObservacoes) }
    }

    fun SalvarSinais(){
        val state = _uiState.value
        viewModelScope.launch {
            try{
                _uiState.update { it.copy(isLoading = true) }

            val entity = SinaisEntity(
                fc = state.fc.toIntOrNull() ?: 0,
                paSistolica = state.paSistolica.toIntOrNull() ?: 0,
                paDiastolica = state.paDiastolica.toIntOrNull() ?: 0,
                spo2 = state.spo2.toIntOrNull() ?: 0,
                glicose = state.glicose.toDoubleOrNull() ?: 0.0,
                temperatura = state.temperatura.toDoubleOrNull() ?: 0.0,
                observacoes = state.observacoes,
                pacienteId = usuarioId
            )
            sinaisDao.insertSinais(entity)
                _uiEvent.send(UiEvent.CadastroSucesso)
                _uiState.update { CadastroSinaisUiState() }}
            catch (e: Exception){
                _uiEvent.send(UiEvent.ShowError("Erro ao salvar: ${e.message}"))
            }
            finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}


data class CadastroSinaisUiState(
    val fc: String = "",
    val paSistolica: String = "",
    val paDiastolica: String = "",
    val spo2: String = "",
    val glicose: String = "",
    val temperatura: String = "",
    val observacoes: String = "",
    val isLoading: Boolean = false
)