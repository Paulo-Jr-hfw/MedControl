package com.app.medcontrol.screen.Paciente

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.data.entity.StatusEvento
import com.app.medcontrol.data.entity.TipoEvento
import com.app.medcontrol.repository.HistoricoRepository
import com.app.medcontrol.repository.LogRepository
import com.app.medcontrol.repository.MedicamentoRepository
import com.app.medcontrol.repository.RegistroRepository
import com.app.medcontrol.repository.UsuarioRepository
import com.app.medcontrol.service.AlarmScheduler
import com.app.medcontrol.util.DateTimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class PacienteHomeScreenViewModel @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val medicamentoRepository: MedicamentoRepository,
    private val registroRepository: RegistroRepository,
    private val historicoRepository: HistoricoRepository,
    private val alarmScheduler: AlarmScheduler,
    private val logRepository: LogRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = savedStateHandle.get<Int>("usuarioId")
        ?: savedStateHandle.get<String>("usuarioId")?.toInt()
        ?: throw IllegalArgumentException("usuarioId é obrigatório e deve ser um Int")
    private val hoje = LocalDate.now()

    private val _uiEvent = Channel<HomeUiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class HomeUiEvent {
        object Logout : HomeUiEvent()
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
        val medicamentos = medicamentoRepository.getMedicamentosAtivosList(usuarioId)
        val agora = LocalDateTime.now()
        val hoje = LocalDate.now()


        medicamentos.forEach { med ->
            val dosesDoMedHoje = registroRepository.verificarSeExisteDoseNoDia(med.id, hoje)

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

                    val idsGerados = registroRepository.inserirRegistros(novosRegistros)

                    idsGerados.forEachIndexed { index, idLong ->
                        val registroId = idLong.toInt()
                        val registro = novosRegistros[index]
                        val agoraObj = LocalDateTime.now()
                        val horarioDose = LocalDateTime.of(hoje, registro.horarioAgendado)

                        if (horarioDose.isAfter(agoraObj)) {
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
            medicamentoRepository.getAllMedicamentosByUsuarioIdFlow(usuarioId).collect {
                sincronizarDosesDiarias()
            }
        }
    }

    fun marcarComoTomado(registroId: Int, medicamentoId: Int, usuarioId: Int) {
        viewModelScope.launch {
            try {
                val registro = registroRepository.getRegistroById(registroId)
                val med = medicamentoRepository.getMedicamentoById(medicamentoId)
                val agora = LocalDateTime.now()

                registro?.let { reg ->

                    registroRepository.marcarComoTomado(
                        registroId = registroId,
                        novoStatus = StatusConsumo.TOMADO,
                        horario = agora.toLocalTime()
                    )

                    alarmScheduler.cancelarAlarme(registroId)

                    val dataHoraPrevistaReal = reg.horarioAgendado.atDate(LocalDate.now())

                    val novoHistorico = HistoricoMedicamentoEntity(
                        medicamento_id = medicamentoId,
                        dataHoraPrevista = dataHoraPrevistaReal,
                        dataHoraTomado = agora,
                        usuarioId = usuarioId
                    )
                    historicoRepository.saveHistoricoMedicamento(novoHistorico)

                    salvarLogMedicamento(
                        usuarioId = usuarioId,
                        nomeMed = med?.nome ?: "Medicamento",
                        horarioAgendado = reg.horarioAgendado,
                        horarioReal = agora.toLocalTime()
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiEvent.send(HomeUiEvent.Logout)
        }
    }

    private suspend fun salvarLogMedicamento(
        usuarioId: Int,
        nomeMed: String,
        horarioAgendado: LocalTime,
        horarioReal: LocalTime
    ) {
        val status = calcularStatusLog(horarioAgendado, horarioReal)
        val descricao = "Agendado: ${horarioAgendado.format(DateTimeUtils.HH_MM)} • Tomado: ${horarioReal.format(DateTimeUtils.HH_MM)}"

        logRepository.registrarAcao(
            usuarioId = usuarioId,
            tipo = TipoEvento.MEDICAMENTO,
            titulo = nomeMed,
            descricao = descricao,
            status = status
        )
    }
    private fun calcularStatusLog(
        horarioAgendado: LocalTime,
        horarioReal: LocalTime
    ): StatusEvento {
        return when {
            horarioReal.isAfter(horarioAgendado.plusHours(8)) -> StatusEvento.ALERTA
            horarioReal.isAfter(horarioAgendado.plusHours(1)) -> StatusEvento.ATRASADO
            else -> StatusEvento.SUCESSO
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
    val imagemUri: String?,
    val medicamentoId: Int,
    val usuarioId: Int
)
