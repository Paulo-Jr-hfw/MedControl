package com.app.medcontrol.domain.usecase

import com.app.medcontrol.repository.UsuarioRepository
import com.app.medcontrol.repository.VinculoRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class VincularAcompanhanteUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val vinculoRepository: VinculoRepository
) {
    suspend operator fun invoke(pacienteId: Int, acompanhanteId: Int): Result<Unit> {
        return try {
            // 1. Valida se o paciente existe
            val paciente = usuarioRepository.getUsuarioById(pacienteId)
                ?: return Result.failure(Exception("Código de paciente inválido."))

            // 2. Verificar se o paciente já possui acompanhante
            val vinculoExistentePaciente = vinculoRepository.getVinculoPorPaciente(pacienteId).first()
            if (vinculoExistentePaciente != null) {
                return Result.failure(Exception("Este paciente já possui um acompanhante vinculado."))
            }

            // 3. Verificar se o acompanhante já possui paciente vinculado
            val vinculoExistenteAcompanhante = vinculoRepository.getVinculoPorAcompanhante(acompanhanteId).first()
            if (vinculoExistenteAcompanhante != null) {
                return Result.failure(Exception("Você já está vinculado a um paciente."))
            }

            // 4. Criar o vínculo
            vinculoRepository.vincular(pacienteId, acompanhanteId)
            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
