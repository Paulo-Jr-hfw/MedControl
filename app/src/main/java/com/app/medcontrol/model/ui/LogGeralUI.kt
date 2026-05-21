package com.app.medcontrol.model.ui

import com.app.medcontrol.data.entity.LogGeralEntity
import java.time.LocalDate

data class LogGeralUI(
    val logsAgrupados: Map<LocalDate, List<LogGeralEntity>> = emptyMap(),
    val isLoading: Boolean = false,
    val mensagemErro: String? = null
)
