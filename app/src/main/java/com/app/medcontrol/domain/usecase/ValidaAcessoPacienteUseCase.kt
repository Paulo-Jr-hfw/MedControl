package com.app.medcontrol.domain.usecase

import com.app.medcontrol.model.TipoUsuario
import com.app.medcontrol.repository.VinculoRepository
import com.app.medcontrol.service.SessionManager
import javax.inject.Inject

class ValidaAcessoPacienteUseCase @Inject constructor(
    private val sessionManager: SessionManager,
    private val vinculoRepository: VinculoRepository
) {
    /**
     * @param pacienteId ID do paciente que se deseja acessar
     * @param exigeEscrita Se true, valida se o usuário tem permissão para MODIFICAR dados (apenas o próprio dono)
     */
    suspend operator fun invoke(pacienteId: Int, exigeEscrita: Boolean = false): Boolean {
        val session = sessionManager.getSessionOnce()
        val userId = session.userId ?: return false
        val userType = session.userType ?: return false

        return when (userType) {
            TipoUsuario.PACIENTE -> {
                // PACIENTE: Pode ler e escrever SOMENTE nos próprios dados
                userId == pacienteId
            }
            TipoUsuario.ACOMPANHANTE -> {
                // ACOMPANHANTE: 
                // 1. Se exigir escrita -> NEGADO (nunca escreve em dados de paciente)
                if (exigeEscrita) return false

                // 2. Se for leitura -> Validar vínculo no banco
                vinculoRepository.existeVinculo(pacienteId = pacienteId, acompanhanteId = userId)
            }
        }
    }
}
