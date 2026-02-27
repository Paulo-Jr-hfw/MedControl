package com.app.medcontrol.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE email = :email AND senha = :senha")
    suspend fun login(email: String, senha: String): UsuarioEntity?

    //Para verificar se email ja existe no banco de dados
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getUsuarioById(id: Int): UsuarioEntity

    //Para conectar paciente com acompanhante no futuro
    @Query("SELECT * FROM usuarios WHERE email = :email")
    suspend fun getUsuarioByEmail(email: String): UsuarioEntity

}