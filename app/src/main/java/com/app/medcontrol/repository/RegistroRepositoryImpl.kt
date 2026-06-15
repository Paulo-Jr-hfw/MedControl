package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.data.entity.RegistroComMedicamento
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegistroRepositoryImpl @Inject constructor(
    private val registroDao: RegistroConsumoDao
) : RegistroRepository {
    override fun getDosesPendentesFlow(data: LocalDate): Flow<List<RegistroComMedicamento>> =
        registroDao.getDosesPendentesFlow(data)

    override fun getTotalDosesDoDia(data: LocalDate): Flow<Int> =
        registroDao.getTotalDosesDoDia(data)

    override fun getDosesTomadasDoDia(data: LocalDate): Flow<Int> =
        registroDao.getDosesTomadasDoDia(data)

    override suspend fun verificarSeExisteDoseNoDia(medicamentoId: Int, data: LocalDate): Int =
        registroDao.verificarSeExisteDoseNoDia(medicamentoId, data)

    override suspend fun inserirRegistros(registros: List<RegistroConsumoEntity>): List<Long> =
        registroDao.inserirRegistros(registros)

    override suspend fun getRegistroById(registroId: Int): RegistroConsumoEntity =
        registroDao.getRegistroById(registroId)

    override suspend fun marcarComoTomado(registroId: Int, novoStatus: StatusConsumo, horario: LocalTime) =
        registroDao.marcarComoTomado(registroId, novoStatus, horario)
}
