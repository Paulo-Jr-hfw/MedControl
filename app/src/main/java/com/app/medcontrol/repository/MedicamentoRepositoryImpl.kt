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
    private val registroDao: RegistroConsumoDao,
    private val validaAcessoUseCase: com.app.medcontrol.domain.usecase.ValidaAcessoPacienteUseCase
) : MedicamentoRepository {

    override fun getAllMedicamentosAtivosFlow(usuarioId: Int): Flow<List<MedicamentoEntity>> {
        // Nota: Flow de leitura disparado via UI, a ViewModel já deve validar via UseCase antes de coletar
        return medicamentoDao.getAllMedicamentosAtivosFlow(usuarioId)
    }

    override fun getAllMedicamentosByUsuarioIdFlow(usuarioId: Int): Flow<List<MedicamentoEntity>> {
        return medicamentoDao.getAllMedicamentosByUsuarioId(usuarioId)
    }

    override suspend fun getMedicamentosAtivosList(usuarioId: Int): List<MedicamentoEntity> {
        if (!validaAcessoUseCase(usuarioId)) throw SecurityException("Acesso negado")
        return medicamentoDao.getMedicamentosAtivosList(usuarioId)
    }

    override suspend fun getMedicamentoById(id: Int): MedicamentoEntity? {
        val med = medicamentoDao.getMedicamentoById(id)
        if (med != null) {
            // Validar se o usuário atual tem acesso ao dono desse medicamento
            if (!validaAcessoUseCase(med.usuarioId)) throw SecurityException("Acesso negado ao medicamento")
        }
        return med
    }

    override suspend fun updateMedicamento(medicamento: MedicamentoEntity) {
        // EXIGE ESCRITA: Apenas o dono
        if (!validaAcessoUseCase(medicamento.usuarioId, exigeEscrita = true)) {
            throw SecurityException("Apenas o paciente pode alterar seus medicamentos")
        }
        medicamentoDao.updateMedicamento(medicamento)
    }

    override suspend fun desativarMedicamento(id: Int) {
        val med = medicamentoDao.getMedicamentoById(id)
        if (med != null) {
            if (!validaAcessoUseCase(med.usuarioId, exigeEscrita = true)) {
                throw SecurityException("Apenas o paciente pode desativar seus medicamentos")
            }
            medicamentoDao.desativarMedicamento(id)
        }
    }

    override suspend fun getRegistrosPorMedicamentoHoje(medId: Int): List<RegistroConsumoEntity> {
        val med = medicamentoDao.getMedicamentoById(medId)
        if (med != null) {
            if (!validaAcessoUseCase(med.usuarioId)) throw SecurityException("Acesso negado")
            return registroDao.getRegistrosPorMedicamentoHoje(medId)
        }
        return emptyList()
    }

    override suspend fun cancelarDosesPendentesHoje(medId: Int) {
        val med = medicamentoDao.getMedicamentoById(medId)
        if (med != null) {
            if (!validaAcessoUseCase(med.usuarioId, exigeEscrita = true)) {
                throw SecurityException("Operação não permitida para acompanhante")
            }
            registroDao.cancelarDosesPendentesHoje(medId)
        }
    }
}
