package com.app.medcontrol.di

import com.app.medcontrol.repository.HistoricoRepository
import com.app.medcontrol.repository.HistoricoRepositoryImpl
import com.app.medcontrol.repository.MedicamentoRepository
import com.app.medcontrol.repository.MedicamentoRepositoryImpl
import com.app.medcontrol.repository.RegistroRepository
import com.app.medcontrol.repository.RegistroRepositoryImpl
import com.app.medcontrol.repository.UsuarioRepository
import com.app.medcontrol.repository.UsuarioRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindMedicamentoRepository(
        medicamentoRepositoryImpl: MedicamentoRepositoryImpl
    ): MedicamentoRepository

    @Binds
    @Singleton
    abstract fun bindHistoricoRepository(
        historicoRepositoryImpl: HistoricoRepositoryImpl
    ): HistoricoRepository

    @Binds
    @Singleton
    abstract fun bindRegistroRepository(
        registroRepositoryImpl: RegistroRepositoryImpl
    ): RegistroRepository

    @Binds
    @Singleton
    abstract fun bindUsuarioRepository(
        usuarioRepositoryImpl: UsuarioRepositoryImpl
    ): UsuarioRepository
}
