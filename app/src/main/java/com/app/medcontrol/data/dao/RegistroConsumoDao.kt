package com.app.medcontrol.data.dao

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
    SELECT r.* FROM registros_consumo r
    INNER JOIN medicamentos m ON r.medicamentoId = m.id
    WHERE m.usuarioId = :pacienteId 
    AND r.dataAgendada = :data 
    AND r.status IN ('PENDENTE', 'ATRASADO') 
    ORDER BY r.horarioAgendado ASC
""")
    fun getDosesPendentesFlow(pacienteId: Int, data: LocalDate): Flow<List<RegistroComMedicamento>>

    // Para o cálculo do progresso
    @Query("""
        SELECT COUNT(*) FROM registros_consumo r
        INNER JOIN medicamentos m ON r.medicamentoId = m.id
        WHERE m.usuarioId = :pacienteId AND r.dataAgendada = :data
    """)
    fun getTotalDosesDoDia(pacienteId: Int, data: LocalDate): Flow<Int>

    @Query("""
        SELECT COUNT(*) FROM registros_consumo r
        INNER JOIN medicamentos m ON r.medicamentoId = m.id
        WHERE m.usuarioId = :pacienteId AND r.dataAgendada = :data AND r.status = 'TOMADO'
    """)
    fun getDosesTomadasDoDia(pacienteId: Int, data: LocalDate): Flow<Int>

    @Query("SELECT COUNT(*) FROM registros_consumo WHERE medicamentoId = :medicamentoId AND dataAgendada = :data")
    suspend fun verificarSeExisteDoseNoDia(medicamentoId: Int, data: LocalDate): Int

    @Transaction
    @Query("""
    SELECT r.* FROM registros_consumo r
    INNER JOIN medicamentos m ON r.medicamentoId = m.id
    WHERE m.usuarioId = :pacienteId 
    AND r.dataAgendada = :data 
    AND r.status IN ('PENDENTE', 'ATRASADO')
""")
    suspend fun getDosesPendentesList(pacienteId: Int, data: LocalDate): List<RegistroComMedicamento>

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

    suspend fun cancelarDosesPendentesHoje(medId: Int) =
        cancelarDosesPendentesPorData(medId, LocalDate.now())

    suspend fun getRegistrosPorMedicamentoHoje(medId: Int) =
        getRegistrosPorMedicamentoEData(medId, LocalDate.now())

    @Query ( """
        UPDATE registros_consumo 
        SET status = 'ESQUECIDO' 
        WHERE status = 'PENDENTE' AND id = :registroId
    """)
        suspend fun marcarComoEsquecido (registroId: Int)

}