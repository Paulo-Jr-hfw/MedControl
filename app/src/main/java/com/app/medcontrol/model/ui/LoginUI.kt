package com.app.medcontrol.model.ui

import com.app.medcontrol.model.Usuario

data class LoginUiState(
    val email: String = "",
    val senha: String = "",
    val isLoading: Boolean = false,
    val loginError: Boolean = false,
    val sucesso: Boolean = false,
    val usuarioLogado: Usuario? = null,
    val mensagemErro: String = ""
)
