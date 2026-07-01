package com.app.medcontrol.domain.usecase

import com.app.medcontrol.repository.MedicamentoRepository
import com.app.medcontrol.service.AlarmScheduler
import com.app.medcontrol.util.ImageStorageManager
import javax.inject.Inject

class ExcluirMedicamentoUseCase @Inject constructor(
    private val repository: MedicamentoRepository,
    private val alarmScheduler: AlarmScheduler,
    private val imageStorageManager: ImageStorageManager
) {
    suspend operator fun invoke(medicamentoId: Int) {
        val medicamento = repository.getMedicamentoById(medicamentoId) ?: return

        imageStorageManager.deleteImage(medicamento.imagemUri)

        val mId = medicamento.id
        val medInativoSemFoto = medicamento.copy(ativo = false, imagemUri = null)

        val registrosHoje = repository.getRegistrosPorMedicamentoHoje(mId)
        registrosHoje.forEach { registro ->
            alarmScheduler.cancelarAlarme(registro.id)
        }

        repository.updateMedicamento(medInativoSemFoto)
        repository.desativarMedicamento(mId)
        repository.cancelarDosesPendentesHoje(mId)
    }
}
