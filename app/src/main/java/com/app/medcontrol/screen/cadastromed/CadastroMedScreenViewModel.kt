package com.app.medcontrol.screen.cadastromed

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.MedicamentoDao
import com.app.medcontrol.data.MedicamentoEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@HiltViewModel
class CadastroMedScreenViewModel @Inject constructor(
    private val medicamentoDao: MedicamentoDao
): ViewModel() {

    private val _uiEvent = Channel<UiEvent>()
    val uiEvent = _uiEvent.receiveAsFlow()
    sealed class UiEvent {
        object CadastroSucesso : UiEvent()
    }

    var nomeMed by mutableStateOf("")
    var dosagem by mutableStateOf("")
    var instrucoes by mutableStateOf("")
    var imagemUri by mutableStateOf<Uri?>(null)

    var listaHorarios by mutableStateOf(listOf(LocalTime.of(8,0)))
        private set

    var indexHorarioEditando by mutableStateOf<Int?>(null)
        private set

    var nomeMedErro by mutableStateOf(false)
    var dosagemErro by mutableStateOf(false)


    fun onNomeMedChange(novoNome: String) {
        nomeMed = novoNome
        if (novoNome.isNotBlank()) nomeMedErro = false
    }
    fun onDosagemChange(novaDosagem: String) {
        dosagem = novaDosagem
        if (novaDosagem.isNotBlank()) dosagemErro = false
    }
    fun onInstrucoesChange(novasInstrucoes: String) {
        instrucoes = novasInstrucoes
    }
    fun onImagemUriChange(novaUri: Uri?) {
        imagemUri = novaUri
    }
    fun onAdicionarHorario() {
        val ultimoHorario = listaHorarios.lastOrNull() ?: LocalTime.of(8,0)
        val novoHorario = ultimoHorario.plusHours(1)
        listaHorarios = listaHorarios + novoHorario
    }

    fun onAtualizarHorario(index: Int, novoHorario: LocalTime) {
        listaHorarios = listaHorarios.toMutableList().also {
            it[index] = novoHorario
        }
    }

    fun onRemoverHorario(index: Int) {
        if (listaHorarios.size > 1) {
            listaHorarios = listaHorarios.toMutableList().also {
                it.removeAt(index)
            }
        }
    }
    fun setEditando(index: Int?) {
        indexHorarioEditando = index
    }

    fun onSalvaMedicamento(){
        nomeMedErro = nomeMed.isBlank()
        dosagemErro = dosagem.isBlank()
        if (nomeMedErro || dosagemErro) return

        val entity = MedicamentoEntity(
            nome = nomeMed,
            dosagem = dosagem,
            instrucoes = instrucoes,
            imagemUri = imagemUri?.toString(),
            horario = listaHorarios
        )
        viewModelScope.launch {
            try {
                medicamentoDao.saveMedicamento(entity)
                _uiEvent.send(UiEvent.CadastroSucesso)

                // Colocar Aqui codigo para limpar os campos ou avisar a View para fechar a telaA
            } catch (e: Exception) {
                println("Erro ao salvar: ${e.message}")
            }
        }
    }
}

