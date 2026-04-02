package com.app.medcontrol.screen.medicamento

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import com.app.medcontrol.model.ui.MedicamentoUI


@HiltViewModel
class MedicamentoScreenViewModel @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
    private val alarmScheduler: AlarmScheduler,
    private val registroDao: RegistroConsumoDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val _UiEvent = Channel<MedicamentoUiEvent>()
    val UiEvent = _UiEvent.receiveAsFlow()

    @RequiresApi(Build.VERSION_CODES.O)
    val listaMedicamentosUI = medicamentoDao.getAllMedicamentosAtivosFlow(usuarioId)
        .map { lista ->
            lista.map { entity ->
                MedicamentoUI(
                    id = entity.id,
                    nome = entity.nome,
                    dosagem = entity.dosagem,
                    instrucoes = entity.instrucoes,
                    horariosFormatados = entity.horario.joinToString(" • ") {
                        it.format(DateTimeFormatter.ofPattern("HH:mm"))
                    },
                    imagemUri = entity.imagemUri,
                    entityOriginal = entity
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun excluirMedicamento(medicamento: MedicamentoEntity) {
        viewModelScope.launch {
            try {
                val mId = medicamento.id
                val medInativoSemFoto = medicamento.copy(ativo = false, imagemUri = null)

                val registrosHoje = registroDao.getRegistrosPorMedicamentoHoje(mId)
                registrosHoje.forEach { registro ->
                    alarmScheduler.cancelarAlarme(registro.id)
                }
                _UiEvent.send(MedicamentoUiEvent.MostrarSnackbar("Medicamento removido com sucesso"))

                medicamentoDao.updateMedicamento(medInativoSemFoto)
                medicamentoDao.desativarMedicamento(mId)
                registroDao.cancelarDosesPendentesHoje(mId)
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

