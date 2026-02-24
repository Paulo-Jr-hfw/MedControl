package com.app.medcontrol.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MedicamentoEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDataBase: RoomDatabase() {
    abstract fun medicamentoDao(): MedicamentoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDataBase? = null

        fun getDatabase(context: Context): AppDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDataBase::class.java,
                    "med_control_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}