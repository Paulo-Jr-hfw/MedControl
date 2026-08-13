package com.app.medcontrol.repository

import com.app.medcontrol.data.entity.UsuarioEntity
import com.app.medcontrol.data.entity.VinculoAcompanhanteEntity
import kotlinx.coroutines.flow.Flow

interface VinculoRepository {
    suspend fun vincular(pacienteId: Int, acompanhanteId: Int)
    suspend fun desvincular(pacienteId: Int, acompanhanteId: Int)
    suspend fun existeVinculo(pacienteId: Int, acompanhanteId: Int): Boolean
    fun getVinculoPorAcompanhante(acompanhanteId: Int): Flow<VinculoAcompanhanteEntity?>
    fun getVinculoPorPaciente(pacienteId: Int): Flow<VinculoAcompanhanteEntity?>
    fun getAcompanhanteVinculado(pacienteId: Int): Flow<UsuarioEntity?>
    fun getPacienteVinculado(acompanhanteId: Int): Flow<UsuarioEntity?>
}
