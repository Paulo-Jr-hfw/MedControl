package com.app.medcontrol.service.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.app.medcontrol.service.AlarmScheduler
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    @Inject
    lateinit var alarmScheduler: AlarmScheduler

    @Inject
    lateinit var registroDao: com.app.medcontrol.data.dao.RegistroConsumoDao

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Como é um processo de banco, usamos uma Coroutine
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                // Lógica: Buscar todas as doses PENDENTES de hoje
                // e chamar o alarmScheduler.agendarAlarme para cada uma
                // (Podemos refinar isso depois)
            }
        }
    }
}