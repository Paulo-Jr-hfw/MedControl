package com.app.medcontrol.domain.usecase

import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.repository.MedicamentoRepository
import com.app.medcontrol.service.AlarmScheduler
import javax.inject.Inject

class ExcluirMedicamentoUseCase @Inject constructor(
    private val repository: MedicamentoRepository,
    private val alarmScheduler: AlarmScheduler
) {
    suspend operator fun invoke(medicamentoId: Int) {
        val medicamento = repository.getMedicamentoById(medicamentoId) ?: return

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
