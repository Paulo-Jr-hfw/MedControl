package com.app.medcontrol.screen.cadastromed

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.entity.MedicamentoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject
import androidx.core.net.toUri

data class CadastroMedUiState(
    val nomeMed: String = "",
    val dosagem: String = "",
    val instrucoes: String = "",
    val imagemUri: Uri? = null,
    val listaHorarios: List<LocalTime> = listOf(LocalTime.of(8, 0)),
    val nomeMedErro: Boolean = false,
    val dosagemErro: Boolean = false,
    val isLoading: Boolean = false,
    val indexHorarioEditando: Int? = null,
    val mostrarDialogoPermissao: Boolean = false
)

@HiltViewModel
class CadastroMedScreenViewModel @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val medicamentoId: Int = savedStateHandle["medicamentoId"] ?: 0
    private val _uiState = MutableStateFlow(CadastroMedUiState())
    val uiState: StateFlow<CadastroMedUiState> = _uiState.asStateFlow()
    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()

    sealed class UiEvent {
        object CadastroSucesso : UiEvent()
        data class ShowError(val message: String) : UiEvent()
        object SolicitarPermissaoAlarme : UiEvent()
        object SalvarMedicamento : UiEvent()
    }


    fun onNomeMedChange(novoNome: String) {
        _uiState.update { it.copy(nomeMed = novoNome, nomeMedErro = false) }
    }

    fun onDosagemChange(novaDosagem: String) {
        _uiState.update { it.copy(dosagem = novaDosagem, dosagemErro = false) }
    }

    fun onInstrucoesChange(novasInstrucoes: String) {
        _uiState.update { it.copy(instrucoes = novasInstrucoes) }
    }

    fun onImagemUriChange(novaUri: Uri?) {
        _uiState.update { it.copy(imagemUri = novaUri) }
    }

    fun setEditando(index: Int?) {
        _uiState.update { it.copy(indexHorarioEditando = index) }
    }

    fun onAdicionarHorario() {
        _uiState.update { state ->
            val ultimoHorario = state.listaHorarios.lastOrNull() ?: LocalTime.of(8, 0)
            val novoHorario = ultimoHorario.plusHours(1)
            state.copy(listaHorarios = state.listaHorarios + novoHorario)
        }
    }

    fun onAtualizarHorario(index: Int, novoHorario: LocalTime) {
        _uiState.update { state ->
            val novaLista = state.listaHorarios.toMutableList().apply {
                this[index] = novoHorario
            }
            state.copy(listaHorarios = novaLista)
        }
    }

    fun onRemoverHorario(index: Int) {
        _uiState.update { state ->
            if (state.listaHorarios.size > 1) {
                val novaLista = state.listaHorarios.toMutableList().apply {
                    this.removeAt(index)
                }
                state.copy(listaHorarios = novaLista)
            } else {
                state
            }
        }
    }


    fun onSalvaMedicamento() {
        val currentState = _uiState.value

        val nomeInvalido = currentState.nomeMed.isBlank()
        val dosagemInvalida = currentState.dosagem.isBlank()

        if (nomeInvalido || dosagemInvalida) {
            _uiState.update { it.copy(nomeMedErro = nomeInvalido, dosagemErro = dosagemInvalida) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val entity = MedicamentoEntity(
                    id = medicamentoId,
                    usuarioId = usuarioId,
                    nome = currentState.nomeMed,
                    dosagem = currentState.dosagem,
                    instrucoes = currentState.instrucoes,
                    imagemUri = currentState.imagemUri?.toString(),
                    horario = currentState.listaHorarios
                )

                medicamentoDao.saveMedicamento(entity)

                _uiEvent.send(UiEvent.CadastroSucesso)
                _uiState.update { CadastroMedUiState() }

            } catch (e: Exception) {
                _uiEvent.send(UiEvent.ShowError("Erro ao salvar: ${e.message}"))
            } finally {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun onSalvarClick() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.SolicitarPermissaoAlarme)
        }
    }

    fun mostrarDialogoPermissao() {
        _uiState.update { it.copy(mostrarDialogoPermissao = true) }
    }

    fun confirmarSalvar() {
        viewModelScope.launch {
            _uiEvent.send(UiEvent.SalvarMedicamento)
        }
    }

    fun fecharDialogoPermissao() {
        _uiState.update { it.copy(mostrarDialogoPermissao = false) }
    }

    fun carregarDadosMedicamento() {
        viewModelScope.launch {
            val medicamento = medicamentoDao.getMedicamentoById(medicamentoId)
            if (medicamento != null) {
                _uiState.update { state ->
                    state.copy(
                        nomeMed = medicamento.nome,
                        dosagem = medicamento.dosagem,
                        instrucoes = medicamento.instrucoes ?: "",
                        imagemUri = medicamento.imagemUri?.toUri(),
                        listaHorarios = medicamento.horario
                    )
                }
            }
        }
    }

    init {
        if (medicamentoId != 0) {
            carregarDadosMedicamento()
        }
    }
}

