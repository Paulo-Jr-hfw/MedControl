package com.app.medcontrol.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sinais")
data class SinaisEntity(
    @PrimaryKey(autoGenerate = true)
    val sinaisId: Int = 0,
    val fc: Int,
    val paSistolica: Int,
    val paDiastolica : Int,
    val spo2: Int,
    val glicose: Double,
    val temperatura: Double,
    val observacoes: String,
    val pacienteId: Int,
    val dataRegistro: Long = System.currentTimeMillis()
)
