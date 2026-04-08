package com.app.medcontrol.screen.sinais

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.SinaisDao
import com.app.medcontrol.data.toDomain
import com.app.medcontrol.data.toUI
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SinaisScreenViewModel@Inject constructor(
    private val sinaisDao: SinaisDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val _uiEvent = Channel<SinaisUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()


    @RequiresApi(Build.VERSION_CODES.O)
    val listaSinaisUI = sinaisDao.getAllSinais(usuarioId)
        .map { listaEntity ->
            listaEntity.map { entity ->
                entity.toDomain().toUI()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _mostrarBottomSheet = MutableStateFlow(false)
    val mostrarBottomSheet = _mostrarBottomSheet.asStateFlow()

    fun abrirBottomSheet() {
        _mostrarBottomSheet.value = true
    }

    fun fecharBottomSheet() {
        _mostrarBottomSheet.value = false
    }

    fun excluirSinal(sinaisId: Int) {
        viewModelScope.launch {
            try {
                sinaisDao.deleteSinalPorId(sinaisId)
                _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Sinal excluído com sucesso"))
            } catch (e: Exception) {
                _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Erro ao excluir registro"))
            }
        }
    }
}

sealed class  SinaisUiEvent{
    data class MostrarSnackbar(val mensagem: String) : SinaisUiEvent()
    data object NavegarParaRelogio : SinaisUiEvent()
}
