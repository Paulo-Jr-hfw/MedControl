package com.app.medcontrol.repository

import com.app.medcontrol.data.entity.RegistroComMedicamento
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface RegistroRepository {
    fun getDosesPendentesFlow(data: LocalDate): Flow<List<RegistroComMedicamento>>
    fun getTotalDosesDoDia(data: LocalDate): Flow<Int>
    fun getDosesTomadasDoDia(data: LocalDate): Flow<Int>
    suspend fun verificarSeExisteDoseNoDia(medicamentoId: Int, data: LocalDate): Int
    suspend fun inserirRegistros(registros: List<RegistroConsumoEntity>): List<Long>
    suspend fun getRegistroById(registroId: Int): RegistroConsumoEntity?
    suspend fun marcarComoTomado(registroId: Int, novoStatus: StatusConsumo, horario: LocalTime)
}
