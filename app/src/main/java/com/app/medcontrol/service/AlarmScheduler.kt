package com.app.medcontrol.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import com.app.medcontrol.service.alarm.AlarmReceiver
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmScheduler( private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ScheduleExactAlarm")
    fun agendarAlarme(registroId: Int, horarioAgendado: LocalDateTime, nomeMed: String) {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra("MED_NOME", nomeMed)
            putExtra("REGISTRO_ID", registroId)
        }
        // O PendingIntent é o que o Android vai "disparar" depois
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            registroId, // ID único para cada alarme não sobrescrever o outro
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        // Converter LocalDateTime para milissegundos
        val tempoMillis = horarioAgendado.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Se não tem permissão, agenda como "não exato" para não fechar o app
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    tempoMillis,
                    pendingIntent
                )
                return
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            tempoMillis,
            pendingIntent
        )
    }

    fun cancelarAlarme(registroId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            registroId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(AlarmManager::class.java)
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
}