package com.app.medcontrol.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalTime

@Entity(tableName = "medicamentos")
data class MedicamentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val dosagem: String,
    val instrucoes: String? = null,
    val imagemUri: String? = null,
    val horario: List<LocalTime>
)
