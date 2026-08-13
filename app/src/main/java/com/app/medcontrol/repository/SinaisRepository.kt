package com.app.medcontrol.repository

import com.app.medcontrol.data.entity.SinaisEntity
import kotlinx.coroutines.flow.Flow

interface SinaisRepository {
    suspend fun insertSinais(sinais: SinaisEntity)
    fun getAllSinais(pacienteId: Int): Flow<List<SinaisEntity>>
    suspend fun deleteSinalPorId(sinaisId: Int)
}
