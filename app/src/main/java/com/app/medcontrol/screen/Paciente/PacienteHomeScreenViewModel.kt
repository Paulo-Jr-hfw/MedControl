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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class PacienteHomeScreenViewModel @Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val medicamentoDao: MedicamentoDao,
    private val registroDao: RegistroConsumoDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val hoje = LocalDate.now()

    val uiState: StateFlow<HomeUiState> = combine(
        registroDao.getDosesPendentesComDetalhes(hoje),
        registroDao.getTotalDosesDoDia(hoje),
        registroDao.getDosesTomadasDoDia(hoje)
    ) { registrosComMed, total, tomadas -> // CORREÇÃO: Nome da variável batendo com o map abaixo
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
        sincronizarDosesDiarias()
    }

    private fun sincronizarDosesDiarias() {
        viewModelScope.launch {
            val existeDose = registroDao.verificarSeExisteDoseNoDia(hoje)

            if (existeDose == 0) {
                val medicamentos = medicamentoDao.getMedicamentosByUsuarioIdList(usuarioId)

                val novosRegistros = medicamentos.flatMap { med ->
                    med.horario.map { hora ->
                        RegistroConsumoEntity(
                            medicamentoId = med.id,
                            dataAgendada = hoje,
                            horarioAgendado = hora,
                            status = StatusConsumo.PENDENTE
                        )
                    }
                }

                if (novosRegistros.isNotEmpty()) {
                    registroDao.inserirRegistros(novosRegistros)
                }
            }
        }
    }

    fun marcarComoTomado(registroId: Int) {
        viewModelScope.launch {
            registroDao.marcarComoTomado(
                registroId = registroId,
                novoStatus = StatusConsumo.TOMADO,
                horario = LocalTime.now() // CORREÇÃO: java.time.LocalTime
            )
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