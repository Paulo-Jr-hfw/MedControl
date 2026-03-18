package com.app.medcontrol.screen.Paciente

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.data.dao.UsuarioDao
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.service.AlarmScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PacienteHomeScreenViewModel @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val medicamentoDao: MedicamentoDao,
    private val registroDao: RegistroConsumoDao,
    private val alarmScheduler: AlarmScheduler,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val hoje = LocalDate.now()

    val uiState: StateFlow<HomeUiState> = combine(
        registroDao.getDosesPendentesComDetalhes(hoje),
        registroDao.getTotalDosesDoDia(hoje),
        registroDao.getDosesTomadasDoDia(hoje)
    ) { registrosComMed, total, tomadas ->
        val usuario = usuarioDao.getUsuarioById(usuarioId)

        val listaDoses = registrosComMed.map { item ->
            DoseAgendada(
                registroId = item.registro.id,
                nomeMedicamento = item.medicamento.nome,
                dosagem = item.medicamento.dosagem,
                horarioAgendado = item.registro.horarioAgendado,
                status = item.registro.status,
                imagemUri = item.medicamento.imagemUri
            )
        }

        HomeUiState(
            nomeUser = usuario?.nome ?: "Usuário",
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

    init {
        observarMedicamentos()
    }

    private suspend fun sincronizarDosesDiarias() {
        val medicamentos = medicamentoDao.getMedicamentosByUsuarioIdList(usuarioId)
        val agora = LocalDateTime.now()


        medicamentos.forEach { med ->
            val dosesDoMedHoje = registroDao.verificarSeExisteDoseNoDia(med.id, hoje)

            if (dosesDoMedHoje == 0) {
                val horariosFuturos = med.horario.filter { it.isAfter(agora.toLocalTime()) }

                if (horariosFuturos.isNotEmpty()) {
                    val novosRegistros = horariosFuturos.map { hora ->
                        RegistroConsumoEntity(
                            medicamentoId = med.id,
                            dataAgendada = hoje,
                            horarioAgendado = hora,
                            status = StatusConsumo.PENDENTE
                        )
                    }

                    val idsGerados = registroDao.inserirRegistros(novosRegistros)

                    idsGerados.forEachIndexed { index, idLong ->
                        val registroId = idLong.toInt()
                        val registro = novosRegistros[index]
                        val agora = LocalDateTime.now()
                        val horarioDose = LocalDateTime.of(hoje, registro.horarioAgendado)

                        if (horarioDose.isAfter(agora)) {
                            alarmScheduler.agendarAlarme(
                                registroId = registroId,
                                horarioAgendado = horarioDose,
                                nomeMed = med.nome
                            )
                        }
                    }
                }
            }
        }
    }

    private fun observarMedicamentos() {
        viewModelScope.launch {
            medicamentoDao.getAllMedicamentosByUsuarioId(usuarioId).collect { medicamentos ->
                sincronizarDosesDiarias()
            }
        }
    }

    fun marcarComoTomado(registroId: Int) {
        viewModelScope.launch {
            try {
            registroDao.marcarComoTomado(
                registroId = registroId,
                novoStatus = StatusConsumo.TOMADO,
                horario = LocalTime.now()
            )
                alarmScheduler.cancelarAlarme(registroId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

data class HomeUiState(
    val nomeUser: String = "",
    val isLoading: Boolean = false,
    val dosesPendentes: List<DoseAgendada> = emptyList(),
    val totalDosesDia: Int = 0,
    val dosesTomadas: Int = 0
)

data class DoseAgendada(
    val registroId: Int,
    val nomeMedicamento: String,
    val dosagem: String,
    val horarioAgendado: LocalTime,
    val status: StatusConsumo,
    val imagemUri: String?
)