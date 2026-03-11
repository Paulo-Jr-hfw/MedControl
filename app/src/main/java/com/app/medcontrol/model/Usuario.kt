package com.app.medcontrol.model

data class Usuario(
    val usuarioId: Int,
    val nome: String,
    val email: String,
    val senha: String,
    val tipo: TipoUsuario
)

enum class TipoUsuario { PACIENTE, ACOMPANHANTE}
