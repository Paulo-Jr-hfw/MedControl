package com.app.medcontrol.model.ui

data class SinaisUI(
    val sinaisId: Int,
    val dataFormatada: String,
    val frequenciaCardiaca: String?,
    val pressaoArterial: String?,
    val oxigenacaoSanguinea: String?,
    val glicose: String?,
    val temperatura: String?,
    val observacoes: String?,
)
