package com.app.medcontrol.screen.historico

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.LogGeralDao
import com.app.medcontrol.domain.usecase.ValidaAcessoPacienteUseCase
import com.app.medcontrol.model.ui.LogGeralUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogGeralViewModel @Inject constructor (
    private val logGeral: LogGeralDao,
    private val validaAcessoUseCase: ValidaAcessoPacienteUseCase,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val pacienteId: Int = checkNotNull(savedStateHandle["pacienteId"])

    private val _isAuthorized = MutableStateFlow<Boolean?>(null)

    val uiState: StateFlow<LogGeralUI> = logGeral.getLogsByUsuarioIdFlow(pacienteId)
        .map {listaBruta ->
            val logsAgrupados = listaBruta.groupBy { log ->
                log.dataHora.toLocalDate()
            }
            LogGeralUI(logsAgrupados = logsAgrupados, isLoading = false)
        }
        .catch { throwable ->
            emit(LogGeralUI(mensagemErro = throwable.message ?: "Erro desconhecido", isLoading = false))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LogGeralUI(isLoading = true)
        )

    init {
        viewModelScope.launch {
            if (!validaAcessoUseCase(pacienteId)) {
                _isAuthorized.value = false
            } else {
                _isAuthorized.value = true
            }
        }
    }
}
