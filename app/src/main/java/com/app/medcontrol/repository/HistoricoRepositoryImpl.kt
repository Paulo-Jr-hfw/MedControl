package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.HistoricoMedicamentoDao
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HistoricoRepositoryImpl @Inject constructor(
    private val historicoMedicamentoDao: HistoricoMedicamentoDao
) : HistoricoRepository {
    override fun getHistoricoMedicamentosByMedicamentoId(medicamentoId: Int): Flow<List<HistoricoMedicamentoEntity>> {
        return historicoMedicamentoDao.getHistoricoMedicamentosByMedicamentoId(medicamentoId)
    }

    override suspend fun saveHistoricoMedicamento(historico: HistoricoMedicamentoEntity) {
        historicoMedicamentoDao.saveHistoricoMedicamento(historico)
    }
}
