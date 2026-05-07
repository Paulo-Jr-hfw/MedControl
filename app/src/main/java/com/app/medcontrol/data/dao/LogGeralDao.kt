package com.app.medcontrol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.medcontrol.data.entity.LogGeralEntity
import com.app.medcontrol.data.entity.TipoEvento
import kotlinx.coroutines.flow.Flow

@Dao
interface LogGeralDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: LogGeralEntity)

    @Query("SELECT * FROM log_geral WHERE usuarioId = :usuarioId ORDER BY dataHora DESC")
    fun getLogsByUsuarioIdFlow(usuarioId: Int): Flow<List<LogGeralEntity>>

    @Query("SELECT * FROM log_geral WHERE usuarioId = :usuarioId ORDER BY dataHora DESC LIMIT 1")
    suspend fun getUltimoLogByUsuarioId(usuarioId: Int): LogGeralEntity?

    @Query("SELECT * FROM log_geral WHERE usuarioId = :usuarioId AND tipoEvento = :tipoEvento ORDER BY dataHora DESC")
    fun getLogsByTipoEventoFlow(usuarioId: Int, tipoEvento: TipoEvento): Flow<List<LogGeralEntity>>

}