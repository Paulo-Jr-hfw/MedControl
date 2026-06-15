package com.app.medcontrol.repository

import com.app.medcontrol.data.entity.UsuarioEntity

interface UsuarioRepository {
    suspend fun getUsuarioById(id: Int): UsuarioEntity?
}
