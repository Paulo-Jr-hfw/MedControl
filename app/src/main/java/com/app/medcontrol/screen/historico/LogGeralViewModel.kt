package com.app.medcontrol.screen.historico

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.LogGeralDao
import com.app.medcontrol.model.ui.LogGeralUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogGeralViewModel @Inject constructor (
    private val logGeral: LogGeralDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])

    val uiState: StateFlow<LogGeralUI> = logGeral.getLogsByUsuarioIdFlow(usuarioId)
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
}