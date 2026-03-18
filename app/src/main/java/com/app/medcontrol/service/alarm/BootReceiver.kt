package com.app.medcontrol.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.service.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var registroDao: com.app.medcontrol.data.dao.RegistroConsumoDao

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Como é um processo de banco, usamos uma Coroutine
            val pendingResult = goAsync()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val hoje = LocalDate.now()
                    val agora = LocalTime.now()

                    val registrosParaReagendar =
                        registroDao.getDosesPendentesParaReagendamento(hoje)
                    val idsParaMarcarComoAtrasado = mutableListOf<Int>()

                    registrosParaReagendar.forEach { item ->
                        val registro = item.registro
                        val medicamento = item.medicamento

                        if (registro.horarioAgendado.isAfter(agora)) {
                            alarmScheduler.agendarAlarme(
                                registroId = registro.id,
                                horarioAgendado = LocalDateTime.of(hoje, registro.horarioAgendado),
                                nomeMed = medicamento.nome
                            )
                        } else {
                            if (registro.status == StatusConsumo.PENDENTE) {
                                idsParaMarcarComoAtrasado.add(registro.id)
                            }
                        }
                    }

                    if (idsParaMarcarComoAtrasado.isNotEmpty()) {
                        registroDao.atualizarStatusEmMassa(
                            ids = idsParaMarcarComoAtrasado,
                            novoStatus = StatusConsumo.ATRASADO
                        )
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}