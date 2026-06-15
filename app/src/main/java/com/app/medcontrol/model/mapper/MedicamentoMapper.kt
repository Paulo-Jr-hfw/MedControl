package com.app.medcontrol.model.mapper

import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.model.ui.MedicamentoUI
import com.app.medcontrol.util.DateTimeUtils

fun MedicamentoEntity.toUI(): MedicamentoUI {
    return MedicamentoUI(
        id = id,
        nome = nome,
        dosagem = dosagem,
        instrucoes = instrucoes,
        horariosFormatados = horario.joinToString(" • ") {
            it.format(DateTimeUtils.HH_MM)
        },
        imagemUri = imagemUri
    )
}
