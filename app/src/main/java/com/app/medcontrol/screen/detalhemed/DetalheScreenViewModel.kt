package com.app.medcontrol.screen.detalhemed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.HistoricoMedicamentoDao
import com.app.medcontrol.data.dao.MedicamentoDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class DetalheScreenViewModel @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
    private val historicoMedicamentoDao: HistoricoMedicamentoDao,
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


    fun mudarAba(novaAba: Int) {
        _abaSelecionada.value = novaAba
    }

}