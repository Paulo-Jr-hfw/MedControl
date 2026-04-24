package com.app.medcontrol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.medcontrol.data.entity.HistoricoMedicamentoEntity
import kotlinx.coroutines.flow.Flow
@Dao
interface HistoricoMedicamentoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveHistoricoMedicamento(historicoMedicamento: HistoricoMedicamentoEntity)

    @Query("SELECT * FROM historico_medicamentos WHERE medicamento_id = :medicamentoId")
    fun getHistoricoMedicamentosByMedicamentoId(medicamentoId: Int): Flow<List<HistoricoMedicamentoEntity>>

}