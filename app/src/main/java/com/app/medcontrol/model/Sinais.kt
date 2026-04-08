package com.app.medcontrol.model

import java.time.LocalDateTime

data class Sinais(
    val sinaisId: Int,
    val fc: Int,
    val paSistolica: Int,
    val paDiastolica: Int,
    val spo2: Int,
    val glicose: Double,
    val temperatura: Double,
    val observacoes: String,
    val dataHora: LocalDateTime
)
