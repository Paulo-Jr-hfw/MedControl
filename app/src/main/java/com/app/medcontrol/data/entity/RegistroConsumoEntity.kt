package com.app.medcontrol.data.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.time.LocalTime

@Entity(
    tableName = "registros_consumo",
    foreignKeys = [
        ForeignKey(
            entity = MedicamentoEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicamentoId"],
            onDelete = ForeignKey.CASCADE // Se deletar o remédio, apaga o histórico
        )
    ],
    indices = [Index("medicamentoId")]
)
data class RegistroConsumoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val medicamentoId: Int,
    val dataAgendada: LocalDate,      // Dia em que deveria tomar (ex: 12/03/2026)
    val horarioAgendado: LocalTime,   // Hora em que deveria tomar (ex: 08:00)
    val horarioReal: LocalTime? = null, // Hora em que REALMENTE tomou (null se não tomou ainda)
    val status: StatusConsumo = StatusConsumo.PENDENTE
)

data class RegistroComMedicamento(
    @Embedded val registro: RegistroConsumoEntity,
    @Relation(
        parentColumn = "medicamentoId",
        entityColumn = "id"
    )
    val medicamento: MedicamentoEntity
)

enum class StatusConsumo {
    PENDENTE, TOMADO, ATRASADO, ESQUECIDO
}
