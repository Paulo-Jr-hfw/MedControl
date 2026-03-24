package com.app.medcontrol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.medcontrol.data.entity.MedicamentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun saveMedicamento(medicamento: MedicamentoEntity)

    @Query("SELECT * FROM medicamentos ORDER BY nome ASC")
    fun getAllMedicamentos(): Flow<List<MedicamentoEntity>>

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun getMedicamentoById(id: Int): MedicamentoEntity?

    @Query("DELETE FROM medicamentos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT * FROM medicamentos WHERE usuarioId = :usuarioId")
    suspend fun getMedicamentosByUsuarioIdList(usuarioId: Int): List<MedicamentoEntity>

    @Query("SELECT * FROM medicamentos WHERE usuarioId = :usuarioId")
    fun getAllMedicamentosByUsuarioId(usuarioId: Int): Flow<List<MedicamentoEntity>>


    @Query("UPDATE medicamentos SET ativo = 0 WHERE id = :id")
    suspend fun desativarMedicamento(id: Int)

    @Query("SELECT * FROM medicamentos WHERE usuarioId = :usuarioId AND ativo = 1")
    fun getAllMedicamentosAtivosFlow(usuarioId: Int): Flow<List<MedicamentoEntity>>

    @Query("SELECT * FROM medicamentos WHERE usuarioId = :usuarioId AND ativo = 1")
    suspend fun getMedicamentosAtivosList(usuarioId: Int): List<MedicamentoEntity>

    @Update
    suspend fun updateMedicamento(medicamento: MedicamentoEntity)


}