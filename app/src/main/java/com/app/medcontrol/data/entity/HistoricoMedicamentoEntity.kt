package com.app.medcontrol.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(
    tableName = "historico_medicamentos",
    foreignKeys = [
        ForeignKey(
            entity = MedicamentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicamento_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index("medicamento_id")]
)
data class HistoricoMedicamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val medicamento_id: Int,
    val dataHoraPrevista: LocalDateTime,
    val dataHoraTomado: LocalDateTime? = null,
    val usuarioId: Int
)