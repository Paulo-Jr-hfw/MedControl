package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.SinaisDao
import com.app.medcontrol.data.entity.SinaisEntity
import com.app.medcontrol.domain.usecase.ValidaAcessoPacienteUseCase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SinaisRepositoryImpl @Inject constructor(
    private val sinaisDao: SinaisDao,
    private val validaAcessoUseCase: ValidaAcessoPacienteUseCase
) : SinaisRepository {
    override suspend fun insertSinais(sinais: SinaisEntity) {
        if (!validaAcessoUseCase(sinais.pacienteId, exigeEscrita = true)) {
            throw SecurityException("Apenas o paciente pode registrar sinais vitais")
        }
        sinaisDao.insertSinais(sinais)
    }

    override fun getAllSinais(pacienteId: Int): Flow<List<SinaisEntity>> {
        return sinaisDao.getAllSinais(pacienteId)
    }

    override suspend fun deleteSinalPorId(sinaisId: Int) {
        val sinal = sinaisDao.getSinalPorId(sinaisId)
        if (sinal != null) {
            if (!validaAcessoUseCase(sinal.pacienteId, exigeEscrita = true)) {
                throw SecurityException("Apenas o paciente pode excluir registros de sinais")
            }
            sinaisDao.deleteSinalPorId(sinaisId)
        }
    }
}
