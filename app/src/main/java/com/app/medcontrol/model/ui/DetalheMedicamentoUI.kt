package com.app.medcontrol.model.ui

import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import com.app.medcontrol.data.entity.MedicamentoEntity
import java.time.LocalDate

data class DetalheMedicamentoUI(
    val medicamento: MedicamentoEntity? = null,
    val imagemArquivo: java.io.File? = null,
    val abaSelecionada: Int = 0,
    val historico: Map<LocalDate, List<HistoricoMedicamentoEntity>> = emptyMap()
)
