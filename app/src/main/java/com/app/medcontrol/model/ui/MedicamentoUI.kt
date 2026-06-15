package com.app.medcontrol.model.ui

data class MedicamentoUI(
    val id: Int,
    val nome: String,
    val dosagem: String,
    val instrucoes: String?,
    val horariosFormatados: String,
    val imagemUri: String?
)
