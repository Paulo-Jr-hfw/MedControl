package com.app.medcontrol.di

import android.content.Context
import androidx.room.Room
import com.app.medcontrol.data.AppDataBase
import com.app.medcontrol.data.MedicamentoDao
import com.app.medcontrol.data.UsuarioDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {
    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "med_control_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideMedicamentoDao(dataBase: AppDataBase) : MedicamentoDao{
        return dataBase.medicamentoDao()

    }

    @Provides
    @Singleton
    fun provideUsuarioDao(dataBase: AppDataBase) : UsuarioDao {
        return dataBase.usuarioDao()
    }
}