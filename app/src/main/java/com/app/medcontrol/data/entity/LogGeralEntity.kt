package com.app.medcontrol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "log_geral")
data class LogGeralEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val usuarioId: Int,
    val tipoEvento: TipoEvento,
    val titulo: String,
    val descricao: String,
    val dataHora: LocalDateTime,
    val status: StatusEvento
)

enum class StatusEvento {
    SUCESSO, ATRASADO, ALERTA
}

enum class TipoEvento {
    MEDICAMENTO, SINAL
}