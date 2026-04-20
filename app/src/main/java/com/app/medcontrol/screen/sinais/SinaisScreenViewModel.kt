package com.app.medcontrol.screen.sinais

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.SinaisDao
import com.app.medcontrol.data.mapHealthDataToEntity
import com.app.medcontrol.data.toDomain
import com.app.medcontrol.data.toUI
import com.app.medcontrol.service.healthconnect.HealthConnectManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
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
    val healthConnectManager: HealthConnectManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val _uiEvent = Channel<SinaisUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    private val _loadingRelogio = MutableStateFlow(false)
    val loadingRelogio = _loadingRelogio.asStateFlow()



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

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun importarDadosRelogio() {
        viewModelScope.launch {
            try {
                _loadingRelogio.value = true

                if (!healthConnectManager.temPermissoes()) {
                    _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Permissão necessária."))
                    return@launch
                }

                coroutineScope {
                    val bDeferred = async { healthConnectManager.lerUltimoBatimento() }
                    val pDeferred = async { healthConnectManager.lerUltimaPressao() }
                    val oDeferred = async { healthConnectManager.lerUltimaOxigenacao() }
                    val tDeferred = async { healthConnectManager.lerUltimaTemperatura() }

                    val batimento = bDeferred.await()
                    val pressao = pDeferred.await()
                    val oxigenacao = oDeferred.await()
                    val temperatura = tDeferred.await()

                    if (batimento == null && pressao == null && oxigenacao == null && temperatura == null) {
                        _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Nenhuma medição recente encontrada."))
                    } else {
                        val novoSinal = mapHealthDataToEntity(
                            usuarioId = usuarioId,
                            batimento = batimento,
                            pressao = pressao,
                            oxigenacao = oxigenacao,
                            temperatura = temperatura
                        )

                        sinaisDao.insertSinais(novoSinal)

                        _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Sinais salvos automaticamente!"))
                    }
                }
            } catch (e: Exception) {
                _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Erro ao salvar dados do relógio."))
                e.printStackTrace()
            } finally {
                _loadingRelogio.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun verificarERegistrarSinaisPeloRelogio(onSolicitarPermissao: () -> Unit) {
        val status = healthConnectManager.verificarDisponibilidade()

        when (status) {
            HealthConnectClient.SDK_AVAILABLE -> {
                viewModelScope.launch {
                    if (healthConnectManager.temPermissoes()) {
                        _loadingRelogio.value = true
                        importarDadosRelogio()
                        _loadingRelogio.value = false
                    } else {
                        onSolicitarPermissao()
                    }
                }
            }
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> {
                viewModelScope.launch {
                    _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Atualize o app Conexão Saúde na Play Store."))
                }
            }
            else -> {
                viewModelScope.launch {
                    _uiEvent.send(SinaisUiEvent.MostrarSnackbar("Health Connect não disponível neste aparelho."))
                }
            }
        }
    }
}

sealed class  SinaisUiEvent{
    data class MostrarSnackbar(val mensagem: String) : SinaisUiEvent()
    //data object NavegarParaRelogio : SinaisUiEvent()
}
