package com.app.medcontrol.data

import android.os.Build
import androidx.annotation.RequiresApi
import com.app.medcontrol.data.entity.SinaisEntity
import com.app.medcontrol.model.Sinais
import com.app.medcontrol.model.ui.SinaisUI
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
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