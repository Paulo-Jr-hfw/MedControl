package com.app.medcontrol.data.dao

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.app.medcontrol.data.entity.RegistroComMedicamento
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface RegistroConsumoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRegistros(registros: List<RegistroConsumoEntity>) : List<Long>

    @Query("UPDATE registros_consumo SET status = :novoStatus, horarioReal = :horario WHERE id = :registroId")
    suspend fun marcarComoTomado(registroId: Int, novoStatus: StatusConsumo, horario: LocalTime)

    @Transaction // Necessário quando usamos @Relation
    @Query("""
    SELECT * FROM registros_consumo 
    WHERE dataAgendada = :data 
    AND status IN ('PENDENTE', 'ATRASADO') 
    ORDER BY horarioAgendado ASC
""")
    fun getDosesPendentesFlow(data: LocalDate): Flow<List<RegistroComMedicamento>>

    // Para o cálculo do progresso
    @Query("SELECT COUNT(*) FROM registros_consumo WHERE dataAgendada = :data")
    fun getTotalDosesDoDia(data: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM registros_consumo WHERE dataAgendada = :data AND status = 'TOMADO'")
    fun getDosesTomadasDoDia(data: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM registros_consumo WHERE medicamentoId = :medicamentoId AND dataAgendada = :data")
    suspend fun verificarSeExisteDoseNoDia(medicamentoId: Int, data: LocalDate): Int

    @Transaction
    @Query("""
    SELECT * FROM registros_consumo 
    WHERE dataAgendada = :data 
    AND status IN ('PENDENTE', 'ATRASADO')
""")
    suspend fun getDosesPendentesList(data: LocalDate): List<RegistroComMedicamento>

    @Query("""
    UPDATE registros_consumo 
    SET status = :novoStatus 
    WHERE id IN (:ids)
""")
    suspend fun atualizarStatusEmMassa(ids: List<Int>, novoStatus: StatusConsumo)

    @Query("UPDATE registros_consumo SET status = :novoStatus WHERE id = :registroId")
    suspend fun atualizarStatus(registroId: Int, novoStatus: StatusConsumo)

    @Query("SELECT * FROM registros_consumo WHERE id = :registroId")
    suspend fun getRegistroById(registroId: Int): RegistroConsumoEntity

    @Query("SELECT * FROM registros_consumo WHERE medicamentoId = :medId AND dataAgendada = :data")
    suspend fun getRegistrosPorMedicamentoEData(medId: Int, data: LocalDate): List<RegistroConsumoEntity>

    @Query("""
    UPDATE registros_consumo 
    SET status = 'ESQUECIDO' 
    WHERE medicamentoId = :medId 
    AND dataAgendada = :data 
    AND status IN ('PENDENTE', 'ATRASADO')
""")
    suspend fun cancelarDosesPendentesPorData(medId: Int, data: LocalDate)

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun cancelarDosesPendentesHoje(medId: Int) =
        cancelarDosesPendentesPorData(medId, LocalDate.now())

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun getRegistrosPorMedicamentoHoje(medId: Int) =
        getRegistrosPorMedicamentoEData(medId, LocalDate.now())

}