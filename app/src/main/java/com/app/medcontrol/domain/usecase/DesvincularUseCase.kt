package com.app.medcontrol.domain.usecase

import com.app.medcontrol.repository.VinculoRepository
import javax.inject.Inject

class DesvincularUseCase @Inject constructor(
    private val vinculoRepository: VinculoRepository
) {
    suspend operator fun invoke(pacienteId: Int, acompanhanteId: Int) {
        vinculoRepository.desvincular(pacienteId, acompanhanteId)
    }
}
