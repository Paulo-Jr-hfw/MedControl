package com.app.medcontrol.repository

import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import kotlinx.coroutines.flow.Flow

interface HistoricoRepository {
    fun getHistoricoMedicamentosByMedicamentoId(medicamentoId: Int): Flow<List<HistoricoMedicamentoEntity>>
    suspend fun saveHistoricoMedicamento(historico: HistoricoMedicamentoEntity)
}
