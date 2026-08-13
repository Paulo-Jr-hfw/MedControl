package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.VinculoDao
import com.app.medcontrol.data.entity.UsuarioEntity
import com.app.medcontrol.data.entity.VinculoAcompanhanteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VinculoRepositoryImpl @Inject constructor(
    private val vinculoDao: VinculoDao
) : VinculoRepository {
    override suspend fun vincular(pacienteId: Int, acompanhanteId: Int) {
        val novoVinculo = VinculoAcompanhanteEntity(
            pacienteId = pacienteId,
            acompanhanteId = acompanhanteId
        )
        vinculoDao.insertVinculo(novoVinculo)
    }

    override suspend fun desvincular(pacienteId: Int, acompanhanteId: Int) {
        vinculoDao.deleteVinculo(pacienteId, acompanhanteId)
    }

    override suspend fun existeVinculo(pacienteId: Int, acompanhanteId: Int): Boolean {
        // Consultar no DAO se o vinculo (pId, aId) existe
        // Como o getVinculoPorAcompanhante retorna Flow, podemos usar first() para uma verificação pontual
        val vinculo = vinculoDao.getVinculoPorAcompanhante(acompanhanteId).first()
        return vinculo != null && vinculo.pacienteId == pacienteId
    }

    override fun getVinculoPorAcompanhante(acompanhanteId: Int): Flow<VinculoAcompanhanteEntity?> =
        vinculoDao.getVinculoPorAcompanhante(acompanhanteId)

    override fun getVinculoPorPaciente(pacienteId: Int): Flow<VinculoAcompanhanteEntity?> =
        vinculoDao.getVinculoPorPaciente(pacienteId)

    override fun getAcompanhanteVinculado(pacienteId: Int): Flow<UsuarioEntity?> =
        vinculoDao.getAcompanhanteVinculado(pacienteId)

    override fun getPacienteVinculado(acompanhanteId: Int): Flow<UsuarioEntity?> =
        vinculoDao.getPacienteVinculado(acompanhanteId)
}
