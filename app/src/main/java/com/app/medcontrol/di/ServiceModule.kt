package com.app.medcontrol.di

import android.content.Context
import com.app.medcontrol.service.AlarmScheduler
import com.app.medcontrol.service.healthconnect.HealthConnectManager
import com.app.medcontrol.service.notification.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {

    @Provides
    @Singleton
    fun provideAlarmScheduler(@ApplicationContext context: Context): AlarmScheduler {
        return AlarmScheduler(context)
    }

    @Provides
    @Singleton
    fun provideHealthConnectManager(@ApplicationContext context: Context): HealthConnectManager {
        return HealthConnectManager(context)
    }

    @Provides
    @Singleton
    fun provideNotificationHelper(@ApplicationContext context: Context): NotificationHelper {
        return NotificationHelper(context)
    }
}