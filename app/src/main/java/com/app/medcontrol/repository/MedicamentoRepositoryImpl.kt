package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MedicamentoRepositoryImpl @Inject constructor(
    private val medicamentoDao: MedicamentoDao,
    private val registroDao: RegistroConsumoDao
) : MedicamentoRepository {

    override fun getAllMedicamentosAtivosFlow(usuarioId: Int): Flow<List<MedicamentoEntity>> {
        return medicamentoDao.getAllMedicamentosAtivosFlow(usuarioId)
    }

    override fun getAllMedicamentosByUsuarioIdFlow(usuarioId: Int): Flow<List<MedicamentoEntity>> {
        return medicamentoDao.getAllMedicamentosByUsuarioId(usuarioId)
    }

    override suspend fun getMedicamentosAtivosList(usuarioId: Int): List<MedicamentoEntity> {
        return medicamentoDao.getMedicamentosAtivosList(usuarioId)
    }

    override suspend fun getMedicamentoById(id: Int): MedicamentoEntity? {
        return medicamentoDao.getMedicamentoById(id)
    }

    override suspend fun updateMedicamento(medicamento: MedicamentoEntity) {
        medicamentoDao.updateMedicamento(medicamento)
    }

    override suspend fun desativarMedicamento(id: Int) {
        medicamentoDao.desativarMedicamento(id)
    }

    override suspend fun getRegistrosPorMedicamentoHoje(medId: Int): List<RegistroConsumoEntity> {
        return registroDao.getRegistrosPorMedicamentoHoje(medId)
    }

    override suspend fun cancelarDosesPendentesHoje(medId: Int) {
        registroDao.cancelarDosesPendentesHoje(medId)
    }
}
