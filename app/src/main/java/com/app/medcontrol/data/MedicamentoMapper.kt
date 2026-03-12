package com.app.medcontrol.data

import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.model.Medicamento

fun MedicamentoEntity.toDomain(): Medicamento {
    return Medicamento(
        medicamentoId = this.id,
        nome = this.nome,
        dosagem = this.dosagem,
        instrucoes = this.instrucoes ?: "",
        imagemUri = null,
        horario = this.horario
    )
}