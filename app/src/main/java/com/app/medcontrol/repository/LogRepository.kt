package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.LogGeralDao
import com.app.medcontrol.data.entity.LogGeralEntity
import com.app.medcontrol.data.entity.StatusEvento
import com.app.medcontrol.data.entity.TipoEvento
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
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

    suspend fun registrarLogSinais(
        usuarioId: Int,
        fc: Int,
        paSistolica: Int,
        paDiastolica: Int,
        spo2: Int,
        glicose: Double,
        temperatura: Double
    ) {
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val hora = LocalTime.now().format(formatter)

        val temAlerta = paSistolica > 140 || paDiastolica > 90 || fc > 100 || (spo2 in 1..92) || (glicose in 0.1..70.0) || glicose > 200 || temperatura > 37.8

        val status = if (temAlerta) StatusEvento.ALERTA else StatusEvento.SUCESSO

        val descricao = if (temAlerta) {
            "Alerta: Sinais vitais fora da meta verificados às $hora. (PA: ${paSistolica}x${paDiastolica}, FC: $fc bpm, SpO2: $spo2%)"
        } else {
            "Sinais vitais verificados com sucesso às $hora."
        }

        registrarAcao(
            usuarioId = usuarioId,
            tipo = TipoEvento.SINAL,
            titulo = "Aferição de Sinais",
            descricao = descricao,
            status = status
        )
    }
}