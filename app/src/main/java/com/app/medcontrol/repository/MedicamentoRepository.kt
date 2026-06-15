package com.app.medcontrol.repository

import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import kotlinx.coroutines.flow.Flow

interface MedicamentoRepository {
    fun getAllMedicamentosAtivosFlow(usuarioId: Int): Flow<List<MedicamentoEntity>>
    fun getAllMedicamentosByUsuarioIdFlow(usuarioId: Int): Flow<List<MedicamentoEntity>>
    suspend fun getMedicamentosAtivosList(usuarioId: Int): List<MedicamentoEntity>
    suspend fun getMedicamentoById(id: Int): MedicamentoEntity?
    suspend fun updateMedicamento(medicamento: MedicamentoEntity)
    suspend fun desativarMedicamento(id: Int)
    suspend fun getRegistrosPorMedicamentoHoje(medId: Int): List<RegistroConsumoEntity>
    suspend fun cancelarDosesPendentesHoje(medId: Int)
}
