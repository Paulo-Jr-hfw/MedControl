package com.app.medcontrol.repository

import com.app.medcontrol.data.dao.UsuarioDao
import com.app.medcontrol.data.entity.UsuarioEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor(
    private val usuarioDao: UsuarioDao
) : UsuarioRepository {
    override suspend fun getUsuarioById(id: Int): UsuarioEntity? {
        return usuarioDao.getUsuarioById(id)
    }
}
