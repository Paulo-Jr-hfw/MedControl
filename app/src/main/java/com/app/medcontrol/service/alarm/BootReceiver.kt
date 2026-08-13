package com.app.medcontrol.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.model.TipoUsuario
import com.app.medcontrol.service.AlarmScheduler
import com.app.medcontrol.service.SessionManager
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

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d("BootReceiver", "Device rebooted. Rescheduling alarms...")

            val pendingResult = goAsync()
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                try {
                    val session = sessionManager.getSessionOnce()
                    
                    // Regra: Somente PACIENTE reagenda alarmes
                    if (session.userType == TipoUsuario.PACIENTE && session.userId != null) {
                        val pacienteId = session.userId
                        val hoje = LocalDate.now()
                        val agora = LocalTime.now()

                        val registrosParaReagendar = registroDao.getDosesPendentesList(pacienteId, hoje)
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
                                if (registro.status == StatusConsumo.PENDENTE || registro.status == StatusConsumo.ATRASADO) {
                                    val proximoToqueAtrasado = LocalDateTime.now().plusHours(1)
                                    alarmScheduler.agendarAlarme(
                                        registroId = registro.id,
                                        horarioAgendado = proximoToqueAtrasado,
                                        nomeMed = medicamento.nome
                                    )
                                }
                            }
                        }

                        if (idsParaMarcarComoAtrasado.isNotEmpty()) {
                            registroDao.atualizarStatusEmMassa(
                                ids = idsParaMarcarComoAtrasado,
                                novoStatus = StatusConsumo.ATRASADO
                            )
                        }
                        Log.i("BootReceiver", "Alarms rescheduled successfully for patient $pacienteId.")
                    } else {
                        Log.i("BootReceiver", "No patient session found. Skipping alarm rescheduling.")
                    }

                } catch (e: Exception) {
                    Log.e("BootReceiver", "Error rescheduling alarms", e)
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
