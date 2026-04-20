package com.app.medcontrol.data

import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import com.app.medcontrol.data.entity.SinaisEntity
import com.app.medcontrol.model.Sinais
import com.app.medcontrol.model.ui.SinaisUI
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


fun SinaisEntity.toDomain(): Sinais {
    return Sinais(
        sinaisId = this.sinaisId,
        fc = this.fc,
        paSistolica = this.paSistolica,
        paDiastolica = this.paDiastolica,
        spo2 = this.spo2,
        glicose = this.glicose,
        temperatura = this.temperatura,
        observacoes = this.observacoes,
        dataHora = Instant.ofEpochMilli(this.dataRegistro)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    )
}


fun Sinais.toUI(): SinaisUI{
    val formatter = DateTimeFormatter.ofPattern("d MMM yyyy 'às' HH:mm", Locale("pt", "BR"))

    return SinaisUI(
        sinaisId = this.sinaisId,
        dataFormatada = this.dataHora.format(formatter),
        frequenciaCardiaca = if (this.fc > 0) this.fc.toString() else null,
        pressaoArterial = if (this.paSistolica > 0 && this.paDiastolica > 0) {
            "${this.paSistolica}/${this.paDiastolica}"
        } else null,
        oxigenacaoSanguinea = if (this.spo2 > 0) this.spo2.toString() else null,
        glicose = if (this.glicose > 0.0) this.glicose.toString() else null,
        temperatura = if (this.temperatura > 0.0) "${this.temperatura}°" else null,
        observacoes = if (this.observacoes.isNotBlank()) this.observacoes else null


    )
}

fun mapHealthDataToEntity(
    usuarioId: Int,
    batimento: HeartRateRecord?,
    pressao: BloodPressureRecord?,
    oxigenacao: OxygenSaturationRecord?,
    temperatura: BodyTemperatureRecord?
): SinaisEntity {
    return SinaisEntity(
        pacienteId = usuarioId,
        dataRegistro = System.currentTimeMillis(),
        fc = batimento?.samples?.firstOrNull()?.beatsPerMinute?.toInt() ?: 0,
        paSistolica = pressao?.systolic?.inMillimetersOfMercury?.toInt() ?: 0,
        paDiastolica = pressao?.diastolic?.inMillimetersOfMercury?.toInt() ?: 0,
        spo2 = oxigenacao?.percentage?.value?.toInt() ?: 0,
        temperatura = temperatura?.temperature?.inCelsius ?: 0.0,
        glicose = 0.0,
        observacoes = "Importado via Smartwatch"
    )
}