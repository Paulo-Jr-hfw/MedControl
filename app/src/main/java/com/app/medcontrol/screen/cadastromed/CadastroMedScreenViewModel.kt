package com.app.medcontrol.screen.cadastromed

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.app.medcontrol.model.Medicamento
import java.time.LocalTime

@RequiresApi(Build.VERSION_CODES.O)
class CadastroMedScreenViewModel: ViewModel() {
    var nome by mutableStateOf("")
    var dosagem by mutableStateOf("")
    var instrucoes by mutableStateOf("")
    var imagemUri by mutableStateOf<Uri?>(null)

    var listaHorarios by mutableStateOf(listOf(LocalTime.of(8,0)))
        private set

    var indexHorarioEditando by mutableStateOf<Int?>(null)
        private set

    var nomeErro by mutableStateOf(false)
    var dosagemErro by mutableStateOf(false)


    fun onNomeChange(novoNome: String) {
        nome = novoNome
        if (novoNome.isNotBlank()) nomeErro = false
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
        nomeErro = nome.isBlank()
        dosagemErro = dosagem.isBlank()
        if (nomeErro || dosagemErro) return

        val novoMedicamento = Medicamento(
            id = 0,
            nome = nome,
            dosagem = dosagem,
            instrucoes = instrucoes,
            imagemUri = imagemUri.toString(),
            horario = listaHorarios
        )
        println( "Medicamento salvo: $novoMedicamento")
    }
}

