package com.app.medcontrol.service.healthconnect

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.Record
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.BloodPressureRecord
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.OxygenSaturationRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Duration
import java.time.Instant

class HealthConnectManager(private val context: Context) {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    val permissoesNecessarias = setOf(
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(OxygenSaturationRecord::class),
        HealthPermission.getReadPermission(BloodPressureRecord::class),
        HealthPermission.getReadPermission(BodyTemperatureRecord::class)
    )

    fun verificarDisponibilidade(): Int {
        return HealthConnectClient.getSdkStatus(context)
    }

    suspend fun temPermissoes(): Boolean {
        val concedidas = healthConnectClient.permissionController.getGrantedPermissions()
        return concedidas.containsAll(permissoesNecessarias)
    }


    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    private suspend inline fun <reified T : Record> lerUltimoRegistro(): T? {

        if (!temPermissoes()) return null

        return try {
            val request = ReadRecordsRequest(
                recordType = T::class,

                timeRangeFilter = TimeRangeFilter.after(
                    Instant.now().minus(Duration.ofDays(1))
                ),
                ascendingOrder = false,
                pageSize = 1
            )
            val response = healthConnectClient.readRecords(request)
            response.records.firstOrNull() as? T
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun lerUltimoBatimento() = lerUltimoRegistro<HeartRateRecord>()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun lerUltimaPressao() = lerUltimoRegistro<BloodPressureRecord>()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun lerUltimaTemperatura() = lerUltimoRegistro<BodyTemperatureRecord>()

    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    suspend fun lerUltimaOxigenacao() = lerUltimoRegistro<OxygenSaturationRecord>()
}