package com.app.medcontrol.screen.Paciente

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.MedicamentoDao
import com.app.medcontrol.data.UsuarioDao
import com.app.medcontrol.data.toDomain
import com.app.medcontrol.model.Medicamento
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HomeUiState(
    val nomeUser: String = "",
    val isLoading: Boolean = false,
    val listaMedicamentos: List<Medicamento> = emptyList()
    //adicionar demais funções com o andamento. (sinais vitais, histórico de medicamentos)
)

@HiltViewModel
class PacienteHomeScreenViewModel@Inject constructor(
    private val usuarioDao: UsuarioDao,
    private val medicamentoDao: MedicamentoDao,
    savedStateHandle: SavedStateHandle
): ViewModel() {
    private val usuarioId: Int = checkNotNull(savedStateHandle["usuarioId"])
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = medicamentoDao.getMedicamentosByUsuarioId(usuarioId)
        .map { listaEntities ->
            val usuario = usuarioDao.getUsuarioById(usuarioId)

            HomeUiState(
                nomeUser = usuario?.nome ?: "Usuário",
                listaMedicamentos = listaEntities.map { it.toDomain() },
                isLoading = false
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeUiState(isLoading = true)
        )
}