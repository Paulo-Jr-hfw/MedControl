package com.app.medcontrol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.app.medcontrol.data.dao.MedicamentoDao
import com.app.medcontrol.data.dao.RegistroConsumoDao
import com.app.medcontrol.data.dao.SinaisDao
import com.app.medcontrol.data.dao.UsuarioDao
import com.app.medcontrol.data.entity.MedicamentoEntity
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.SinaisEntity
import com.app.medcontrol.data.entity.UsuarioEntity

@Database(entities = [
    MedicamentoEntity::class,
    UsuarioEntity::class,
    RegistroConsumoEntity::class,
    SinaisEntity::class],
    version = 7, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun medicamentoDao(): MedicamentoDao
    abstract fun usuarioDao(): UsuarioDao
    abstract fun registroConsumoDao(): RegistroConsumoDao

    abstract fun sinaisDao(): SinaisDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "med_control_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}