package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.LogGeralDao
import com.app.medcontrol.data.entity.LogGeralEntity
import com.app.medcontrol.data.entity.StatusEvento
import com.app.medcontrol.data.entity.TipoEvento
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject constructor(
    private val logGeralDao: LogGeralDao
) {
    suspend fun registrarAcao(
        usuarioId: Int,
        tipo: TipoEvento,
        titulo: String,
        descricao: String,
        status: StatusEvento
    ) {
        val novoLog = LogGeralEntity(
            usuarioId = usuarioId,
            tipoEvento = tipo,
            titulo = titulo,
            descricao = descricao,
            dataHora = LocalDateTime.now(),
            status = status
        )
        logGeralDao.insertLog(novoLog)
    }

}