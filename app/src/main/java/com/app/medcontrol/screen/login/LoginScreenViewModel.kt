package com.app.medcontrol.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.dao.UsuarioDao
import com.app.medcontrol.model.TipoUsuario
import com.app.medcontrol.model.Usuario
import com.app.medcontrol.model.ui.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val usuarioDao: UsuarioDao
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState


    fun onEmailChange(newEmail: String) {
        _uiState.update {
            it.copy(
                email = newEmail,
                loginError = false
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                senha = newPassword,
                loginError = false
            )
        }
    }

    fun onLogin(tipoSelecionado: TipoUsuario) {
        val currentState = _uiState.value
        if (currentState.email.isBlank() || currentState.senha.isBlank()) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true)}

            val usuario = usuarioDao.login(currentState.email, currentState.senha)

            if (usuario != null && usuario.tipo == tipoSelecionado.name) {
                val usuarioLogadoParaApp = Usuario(
                    usuarioId = usuario.id,
                    nome = usuario.nome,
                    email = usuario.email,
                    senha = "",
                    tipo = TipoUsuario.valueOf(usuario.tipo)
                )
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = false,
                        sucesso = true,
                        usuarioLogado = usuarioLogadoParaApp
                    )
                }
            } else if (usuario != null) {
                val mensagem = if (tipoSelecionado == TipoUsuario.PACIENTE)
                    "Esta conta está cadastrada como Acompanhante"
                else
                    "Esta conta está cadastrada como Paciente"

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = true,
                        mensagemErro = mensagem
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loginError = true,
                        mensagemErro = "E-mail ou senha incorretos"
                    )
                }
            }
        }
    }
    fun resetSucessoLogin() {
        _uiState.update { it.copy(sucesso = false) }
    }
}