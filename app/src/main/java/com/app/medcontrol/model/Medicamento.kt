package com.app.medcontrol.model

import java.time.LocalTime

data class Medicamento(
    val id: Int,
    val nome: String,
    val dosagem: String,
    val instrucoes: String,
    val imagemUri: String? = null,
    val horario: List<LocalTime>,
)
