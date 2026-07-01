package com.helkore.rutas.android.di

import com.helkore.rutas.data.repository.AuthRepositoryImpl
import com.helkore.rutas.data.repository.EtaRepositoryImpl
import com.helkore.rutas.data.repository.FlotaRepositoryImpl
import com.helkore.rutas.data.repository.IncidenciaRepositoryImpl
import com.helkore.rutas.data.repository.JornadaRepositoryImpl
import com.helkore.rutas.data.repository.RutaRepositoryImpl
import com.helkore.rutas.data.repository.TelemetriaRepositoryImpl
import com.helkore.rutas.data.repository.UsuarioRepositoryImpl
import com.helkore.rutas.domain.port.repository.AuthRepository
import com.helkore.rutas.domain.port.repository.EtaRepository
import com.helkore.rutas.domain.port.repository.FlotaRepository
import com.helkore.rutas.domain.port.repository.IncidenciaRepository
import com.helkore.rutas.domain.port.repository.JornadaRepository
import com.helkore.rutas.domain.port.repository.RutaRepository
import com.helkore.rutas.domain.port.repository.TelemetriaRepository
import com.helkore.rutas.domain.port.repository.UsuarioRepository
import org.koin.dsl.module

val repositoryModule = module {
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<RutaRepository> { RutaRepositoryImpl(get()) }
    single<JornadaRepository> { JornadaRepositoryImpl(get()) }
    single<TelemetriaRepository> { TelemetriaRepositoryImpl(get(), get()) }
    single<EtaRepository> { EtaRepositoryImpl(get()) }
    single<IncidenciaRepository> { IncidenciaRepositoryImpl(get()) }
    single<FlotaRepository> { FlotaRepositoryImpl(get()) }
    single<UsuarioRepository> { UsuarioRepositoryImpl(get()) }
}
