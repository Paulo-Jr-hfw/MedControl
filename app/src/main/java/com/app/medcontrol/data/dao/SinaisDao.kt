package com.app.medcontrol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.medcontrol.data.entity.SinaisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SinaisDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSinais(sinais: SinaisEntity)

    @Query("SELECT * FROM sinais WHERE pacienteId = :pacienteId ORDER BY dataRegistro DESC")
    fun getAllSinais(pacienteId: Int): Flow<List<SinaisEntity>>

    @Query("DELETE FROM sinais WHERE sinaisId = :sinaisId")
    suspend fun deleteSinalPorId(sinaisId: Int)


}