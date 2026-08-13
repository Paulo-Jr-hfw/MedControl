package com.app.medcontrol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.medcontrol.data.entity.UsuarioEntity
import com.app.medcontrol.data.entity.VinculoAcompanhanteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VinculoDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertVinculo(vinculo: VinculoAcompanhanteEntity)

    @Query("DELETE FROM vinculos_acompanhante WHERE pacienteId = :pacienteId OR acompanhanteId = :acompanhanteId")
    suspend fun deleteVinculo(pacienteId: Int, acompanhanteId: Int)

    @Query("SELECT * FROM vinculos_acompanhante WHERE acompanhanteId = :acompanhanteId LIMIT 1")
    fun getVinculoPorAcompanhante(acompanhanteId: Int): Flow<VinculoAcompanhanteEntity?>

    @Query("SELECT * FROM vinculos_acompanhante WHERE pacienteId = :pacienteId LIMIT 1")
    fun getVinculoPorPaciente(pacienteId: Int): Flow<VinculoAcompanhanteEntity?>

    @Query("""
        SELECT u.* FROM usuarios u
        INNER JOIN vinculos_acompanhante v ON u.id = v.acompanhanteId
        WHERE v.pacienteId = :pacienteId
    """)
    fun getAcompanhanteVinculado(pacienteId: Int): Flow<UsuarioEntity?>

    @Query("""
        SELECT u.* FROM usuarios u
        INNER JOIN vinculos_acompanhante v ON u.id = v.pacienteId
        WHERE v.acompanhanteId = :acompanhanteId
    """)
    fun getPacienteVinculado(acompanhanteId: Int): Flow<UsuarioEntity?>
}
