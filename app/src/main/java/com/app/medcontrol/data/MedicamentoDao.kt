package com.app.medcontrol.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMedicamento(medicamento: MedicamentoEntity)

    @Update
    suspend fun update(medicamento: MedicamentoEntity)

    @Query("SELECT * FROM medicamentos ORDER BY nome ASC")
    fun getAllMedicamentos(): Flow<List<MedicamentoEntity>>

    @Query("SELECT * FROM medicamentos WHERE id = :id")
    suspend fun getMedicamentoById(id: Int): MedicamentoEntity?

    @Query("DELETE FROM medicamentos WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Delete
    suspend fun deleteMedicamento(medicamento: MedicamentoEntity)


}
