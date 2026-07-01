package com.app.medcontrol.screen.detalhemed

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.model.ui.DetalheMedicamentoUI
import com.app.medcontrol.repository.HistoricoRepository
import com.app.medcontrol.repository.MedicamentoRepository
import com.app.medcontrol.util.ImageStorageManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject


@HiltViewModel
class DetalheScreenViewModel @Inject constructor(
    private val repository: MedicamentoRepository,
    private val historicoRepository: HistoricoRepository,
    private val imageStorageManager: ImageStorageManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val medicamentoId: Int = checkNotNull(savedStateHandle["medicamentoId"])
    private val _abaSelecionada = MutableStateFlow(0)

    val abaSelecionada = _abaSelecionada.asStateFlow()
    val medicamentoState = flow {
        val medicamento = repository.getMedicamentoById(medicamentoId)
        emit(medicamento)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val imagemArquivoState = medicamentoState
        .map { medicamento ->
            imageStorageManager.getFileFromName(medicamento?.imagemUri)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

    val historicoAgrupado = historicoRepository.getHistoricoMedicamentosByMedicamentoId(medicamentoId)
        .map { listaBruta ->
            listaBruta.groupBy { registro ->
                registro.dataHoraPrevista.toLocalDate()
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyMap())

    val uiState: StateFlow<DetalheMedicamentoUI> = combine(
        medicamentoState,
        imagemArquivoState,
        abaSelecionada,
        historicoAgrupado
    ) { medicamento, imagemArquivo, aba, historico ->
        DetalheMedicamentoUI(
            medicamento = medicamento,
            imagemArquivo = imagemArquivo,
            abaSelecionada = aba,
            historico = historico
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DetalheMedicamentoUI()
    )

    fun mudarAba(novaAba: Int) {
        _abaSelecionada.value = novaAba
    }

}