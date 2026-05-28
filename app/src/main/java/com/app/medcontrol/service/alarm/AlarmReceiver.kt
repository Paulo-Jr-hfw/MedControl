package com.app.medcontrol.service.alarm

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.app.medcontrol.MainActivity
import com.app.medcontrol.data.dao.HistoricoMedicamentoDao
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import com.app.medcontrol.data.entity.StatusEvento
import com.app.medcontrol.data.entity.TipoEvento
import com.app.medcontrol.repository.LogRepository
import com.app.medcontrol.service.AlarmScheduler
import com.app.medcontrol.service.notification.NotificationHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@AndroidEntryPoint
class AlarmReceiver : BroadcastReceiver() {

    @Inject
    lateinit var registroDao: RegistroConsumoDao

    @Inject
    lateinit var medicamentoDao: MedicamentoDao

    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var logRepository: LogRepository

    @Inject
    lateinit var historicoMedicamentoDao: HistoricoMedicamentoDao


    override fun onReceive(context: Context, intent: Intent) {
        val medicamentoNome = intent.getStringExtra("MED_NOME") ?: "Medicamento"
        val registroId = intent.getIntExtra("REGISTRO_ID", 0)
        val pendingResult = goAsync()

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (registroId != 0) {
                    val registroAtual = registroDao.getRegistroById(registroId)

                    if (registroAtual.status == StatusConsumo.PENDENTE || registroAtual.status == StatusConsumo.ATRASADO) {
                        val dataHoraOriginal = LocalDateTime.of(registroAtual.dataAgendada, registroAtual.horarioAgendado)
                        val agora = LocalDateTime.now()

                        if (!agora.isBefore(dataHoraOriginal)) {
                            val horasDeAtraso = java.time.Duration.between(dataHoraOriginal, agora).toHours()


                            if (horasDeAtraso >= 9) {
                                registroDao.atualizarStatus(registroId, StatusConsumo.ESQUECIDO)

                                val med = medicamentoDao.getMedicamentoById(registroAtual.medicamentoId)
                                val idDoDono = med?.usuarioId ?: 0

                                val historicoEsquecido = HistoricoMedicamentoEntity(
                                    medicamento_id = registroAtual.medicamentoId,
                                    dataHoraPrevista = dataHoraOriginal,
                                    dataHoraTomado = null,
                                    usuarioId = idDoDono
                                )
                                historicoMedicamentoDao.saveHistoricoMedicamento(historicoEsquecido)

                                logRepository.registrarAcao(
                                    usuarioId = idDoDono,
                                    tipo = TipoEvento.MEDICAMENTO,
                                    titulo = medicamentoNome,
                                    descricao = "Dose das ${registroAtual.horarioAgendado} esquecida (limite de 9h excedido).",
                                    status = StatusEvento.ALERTA
                                )

                                notificationManager.cancel(registroId)

                                val dataHoraAmanha = dataHoraOriginal.plusDays(1)
                                alarmScheduler.agendarAlarme(
                                    registroId = registroId,
                                    horarioAgendado = dataHoraAmanha,
                                    nomeMed = medicamentoNome
                                )


                            } else {
                                if (registroAtual.status == StatusConsumo.PENDENTE && horasDeAtraso >= 1) {
                                    registroDao.atualizarStatus(registroId, StatusConsumo.ATRASADO)
                                }

                                val proximoToque = LocalDateTime.now().plusHours(1)
                                alarmScheduler.agendarAlarme(
                                    registroId = registroId,
                                    horarioAgendado = proximoToque,
                                    nomeMed = medicamentoNome
                                )

                                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                                val activityIntent = Intent(context, MainActivity::class.java).apply {
                                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                }

                                val pendingIntent = PendingIntent.getActivity(
                                    context,
                                    registroId,
                                    activityIntent,
                                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                                )

                                val builder = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
                                    .setSmallIcon(android.R.drawable.ic_dialog_info)
                                    .setContentTitle("Hora de tomar seu remédio!")
                                    .setContentText("Está na hora do: $medicamentoNome")
                                    .setPriority(NotificationCompat.PRIORITY_MAX)
                                    .setCategory(NotificationCompat.CATEGORY_ALARM)
                                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                                    .setDefaults(NotificationCompat.DEFAULT_ALL)
                                    .setFullScreenIntent(pendingIntent, true)
                                    .setAutoCancel(true)
                                    .setContentIntent(pendingIntent)

                                notificationManager.notify(registroId, builder.build())
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }
    }
}