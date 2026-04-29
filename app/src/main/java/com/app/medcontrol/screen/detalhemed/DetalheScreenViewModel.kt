package com.app.medcontrol.screen.detalhemed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.HistoricoMedicamentoDao
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import com.app.medcontrol.data.entity.MedicamentoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate
import javax.inject.Inject


@HiltViewModel
class DetalheScreenViewModel @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
    historicoMedicamentoDao: HistoricoMedicamentoDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val medicamentoId: Int = checkNotNull(savedStateHandle["medicamentoId"])
    private val _abaSelecionada = MutableStateFlow(0)

    val abaSelecionada = _abaSelecionada.asStateFlow()
    val medicamentoState = flow {
        val medicamento = medicamentoDao.getMedicamentoById(medicamentoId)
        emit(medicamento)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val historicoAgrupado = historicoMedicamentoDao.getHistoricoMedicamentosByMedicamentoId(medicamentoId)
        .map { listaBruta ->
            listaBruta.groupBy { registro ->
                registro.dataHoraPrevista.toLocalDate()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap())

    val uiState: StateFlow<DetalheMedicamentoUiState> = combine(
        medicamentoState,
        abaSelecionada,
        historicoAgrupado
    ) { medicamento, aba, historico ->
        DetalheMedicamentoUiState(
            medicamento = medicamento,
            abaSelecionada = aba,
            historico = historico
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetalheMedicamentoUiState()
    )

    fun mudarAba(novaAba: Int) {
        _abaSelecionada.value = novaAba
    }

}

data class DetalheMedicamentoUiState(
    val medicamento: MedicamentoEntity? = null,
    val abaSelecionada: Int = 0,
    val historico: Map<LocalDate, List<HistoricoMedicamentoEntity>> = emptyMap()
)