package com.app.medcontrol.model.ui

import com.app.medcontrol.data.entity.MedicamentoEntity

data class MedicamentoUI(
    val id: Int,
    val nome: String,
    val dosagem: String,
    val instrucoes: String?,
    val horariosFormatados: String,
    val imagemUri: String?,
    val entityOriginal: MedicamentoEntity
)
