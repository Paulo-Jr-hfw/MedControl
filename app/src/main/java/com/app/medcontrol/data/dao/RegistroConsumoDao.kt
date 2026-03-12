package com.app.medcontrol.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.app.medcontrol.data.entity.RegistroComMedicamento
import com.app.medcontrol.data.entity.RegistroConsumoEntity
import com.app.medcontrol.data.entity.StatusConsumo
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

@Dao
interface RegistroConsumoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRegistros(registros: List<RegistroConsumoEntity>)

    @Update
    suspend fun atualizarRegistro(registro: RegistroConsumoEntity)

    @Query("UPDATE registros_consumo SET status = :novoStatus, horarioReal = :horario WHERE id = :registroId")
    suspend fun marcarComoTomado(registroId: Int, novoStatus: StatusConsumo, horario: LocalTime)

    @Transaction // Necessário quando usamos @Relation
    @Query("""
    SELECT * FROM registros_consumo 
    WHERE dataAgendada = :data 
    AND status IN ('PENDENTE', 'ATRASADO') 
    ORDER BY horarioAgendado ASC
""")
    fun getDosesPendentesComDetalhes(data: LocalDate): Flow<List<RegistroComMedicamento>>

    // Para o cálculo do progresso
    @Query("SELECT COUNT(*) FROM registros_consumo WHERE dataAgendada = :data")
    fun getTotalDosesDoDia(data: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM registros_consumo WHERE dataAgendada = :data AND status = 'TOMADO'")
    fun getDosesTomadasDoDia(data: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM registros_consumo WHERE dataAgendada = :data")
    suspend fun verificarSeExisteDoseNoDia(data: LocalDate): Int
}