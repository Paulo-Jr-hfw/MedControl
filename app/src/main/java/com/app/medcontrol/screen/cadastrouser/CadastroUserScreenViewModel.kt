package com.app.medcontrol.screen.cadastrouser

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.UsuarioDao
import com.app.medcontrol.data.UsuarioEntity
import com.app.medcontrol.model.TipoUsuario
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CadastroUserUiState(
    val nome: String = "",
    val email: String = "",
    val senha: String = "",
    val tipoSelecionado: TipoUsuario = TipoUsuario.PACIENTE,

    val nomeErro: String? = null,
    val emailErro: String? = null,
    val senhaErro: String? = null,

    val isLoading: Boolean = false,
    val sucesso: Boolean = false
)

@HiltViewModel
class CadastroUserScreenViewModel@Inject constructor(
    private val usuarioDao: UsuarioDao
): ViewModel() {

    private val _uiState = MutableStateFlow(CadastroUserUiState())
    val uiState: StateFlow<CadastroUserUiState> = _uiState

    fun onTipoSelected(novoTipo: TipoUsuario) {
        _uiState.update {
            it.copy(tipoSelecionado = novoTipo)
        }
    }


    fun onSalvarUsuario() {

        if (!validarCampos()) return

        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            val state = _uiState.value

            val usuarioExistente =
                usuarioDao.getUsuarioByEmail(state.email)

            if (usuarioExistente != null) {
                _uiState.update {
                    it.copy(
                        emailErro = "Este e-mail já está em uso",
                        isLoading = false
                    )
                }
                return@launch
            }

            val usuario = UsuarioEntity(
                nome = state.nome,
                email = state.email,
                senha = state.senha,
                tipo = state.tipoSelecionado.name
            )

            usuarioDao.saveUsuario(usuario)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    sucesso = true
                )
            }
        }
    }

    fun onNomeUserChange(novoNome: String) {
        _uiState.update {
            it.copy(
                nome = novoNome,
                nomeErro = null
            )
        }
    }

    fun onEmailChange(novoEmail: String) {
        _uiState.update {
            it.copy(
                email = novoEmail,
                emailErro = null
            )
        }
    }
    fun onSenhaChange(novaSenha: String) {
        _uiState.update {
            it.copy(
                senha = novaSenha,
                senhaErro = null
            )
        }
    }

    fun onSucesso() {
        //navegar para tela de login
    }

    private fun validarCampos(): Boolean {

        val state = _uiState.value

        var nomeErro: String? = null
        var emailErro: String? = null
        var senhaErro: String? = null

        if (state.nome.isBlank())
            nomeErro = "Nome é obrigatório"

        if (state.email.isBlank()) {
            emailErro = "E-mail é obrigatório"
        } else if (!Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            emailErro = "Formato inválido"
        }

        if (state.senha.isBlank())
            senhaErro = "Senha é obrigatória"

        _uiState.update {
            it.copy(
                nomeErro = nomeErro,
                emailErro = emailErro,
                senhaErro = senhaErro
            )
        }

        return nomeErro == null && emailErro == null && senhaErro == null
    }
}