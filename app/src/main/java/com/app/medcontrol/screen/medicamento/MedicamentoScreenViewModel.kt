package com.app.medcontrol.screen.medicamento

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.model.Medicamento
import com.app.medcontrol.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicamentoScreenViewModel @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
    private val alarmScheduler: AlarmScheduler,
    private val registroDao: RegistroConsumoDao
) : ViewModel() {
    val listaMedicamentos = medicamentoDao.getAllMedicamentosAtivosFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    @RequiresApi(Build.VERSION_CODES.O)
    fun excluirMedicamento(medicamento: Medicamento) {
        viewModelScope.launch {
            val mId = medicamento.medicamentoId

            val registrosHoje = registroDao.getRegistrosPorMedicamentoHoje(mId)
            registrosHoje.forEach { registro ->
                alarmScheduler.cancelarAlarme(registro.id)
            }

            medicamentoDao.desativarMedicamento(mId)

            registroDao.cancelarDosesPendentesHoje(mId)
        }
    }


}