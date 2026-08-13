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
    private val registroDao: RegistroConsumoDao,
    private val medicamentoDao: com.app.medcontrol.data.dao.MedicamentoDao,
    private val validaAcessoUseCase: com.app.medcontrol.domain.usecase.ValidaAcessoPacienteUseCase
) : RegistroRepository {
    override fun getDosesPendentesFlow(pacienteId: Int, data: LocalDate): Flow<List<RegistroComMedicamento>> =
        registroDao.getDosesPendentesFlow(pacienteId, data)

    override fun getTotalDosesDoDia(pacienteId: Int, data: LocalDate): Flow<Int> =
        registroDao.getTotalDosesDoDia(pacienteId, data)

    override fun getDosesTomadasDoDia(pacienteId: Int, data: LocalDate): Flow<Int> =
        registroDao.getDosesTomadasDoDia(pacienteId, data)

    override suspend fun verificarSeExisteDoseNoDia(medicamentoId: Int, data: LocalDate): Int =
        registroDao.verificarSeExisteDoseNoDia(medicamentoId, data)

    override suspend fun inserirRegistros(registros: List<RegistroConsumoEntity>): List<Long> {
        if (registros.isEmpty()) return emptyList()
        
        // Validar se o usuário tem permissão de escrita para o dono do medicamento
        val med = medicamentoDao.getMedicamentoById(registros.first().medicamentoId)
        if (med != null) {
            if (!validaAcessoUseCase(med.usuarioId, exigeEscrita = true)) {
                throw SecurityException("Apenas o paciente pode agendar doses")
            }
        }
        return registroDao.inserirRegistros(registros)
    }

    override suspend fun getRegistroById(registroId: Int): RegistroConsumoEntity {
        val registro = registroDao.getRegistroById(registroId)
        val med = medicamentoDao.getMedicamentoById(registro.medicamentoId)
        if (med != null) {
            if (!validaAcessoUseCase(med.usuarioId)) throw SecurityException("Acesso negado ao registro")
        }
        return registro
    }

    override suspend fun marcarComoTomado(registroId: Int, novoStatus: StatusConsumo, horario: LocalTime) {
        val registro = registroDao.getRegistroById(registroId)
        val med = medicamentoDao.getMedicamentoById(registro.medicamentoId)
        if (med != null) {
            if (!validaAcessoUseCase(med.usuarioId, exigeEscrita = true)) {
                throw SecurityException("Apenas o paciente pode marcar doses como tomadas")
            }
        }
        registroDao.marcarComoTomado(registroId, novoStatus, horario)
    }
}
