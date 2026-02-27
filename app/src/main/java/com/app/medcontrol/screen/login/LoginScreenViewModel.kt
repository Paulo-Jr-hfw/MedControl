package com.app.medcontrol.screen.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.medcontrol.data.UsuarioDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginScreenViewModel @Inject constructor(
    private val UsuarioDao: UsuarioDao
) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var loginError by mutableStateOf(false)


    fun onEmailChange(newEmail: String) {
        email = newEmail
        loginError = false
    }

    fun onPasswordChange(newPassword: String) {
        password = newPassword
        loginError = false
    }

    fun onLogin() {
        if (email.isBlank() || password.isBlank()) return

        viewModelScope.launch {
            val usuarioLogado = UsuarioDao.login(email, password)
            if (usuarioLogado != null) {
                loginError = false
                println("Sucesso!")
            } else {
                loginError = true
            }
        }
    }
}