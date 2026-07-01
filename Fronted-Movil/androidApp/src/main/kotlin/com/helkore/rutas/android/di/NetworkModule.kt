package com.helkore.rutas.android.di

import com.google.firebase.auth.FirebaseAuth
import com.helkore.rutas.android.feature.auth.FirebaseEmailAuthService
import com.helkore.rutas.data.local.DataStoreSessionStore
import com.helkore.rutas.data.remote.api.AuthApiService
import com.helkore.rutas.data.remote.api.EtaApiService
import com.helkore.rutas.data.remote.api.FlotaApiService
import com.helkore.rutas.data.remote.api.IncidenciaApiService
import com.helkore.rutas.data.remote.api.JornadaApiService
import com.helkore.rutas.data.remote.api.RutaApiService
import com.helkore.rutas.data.remote.api.TelemetriaApiService
import com.helkore.rutas.data.remote.api.UsuarioApiService
import com.helkore.rutas.data.remote.network.buildHttpClient
import com.helkore.rutas.data.remote.websocket.TelemetriaWebSocketClient
import com.helkore.rutas.domain.port.local.SessionStore
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val networkModule = module {
    single<SessionStore> { DataStoreSessionStore(androidContext()) }
    single { FirebaseAuth.getInstance() }
    single { FirebaseEmailAuthService(get()) }
    single { buildHttpClient(get()) }
    single { AuthApiService(get(), get()) }
    single { RutaApiService(get(), get()) }
    single { JornadaApiService(get(), get()) }
    single { TelemetriaApiService(get(), get()) }
    single { EtaApiService(get(), get()) }
    single { IncidenciaApiService(get(), get()) }
    single { FlotaApiService(get(), get()) }
    single { TelemetriaWebSocketClient(get(), get()) }
    single { UsuarioApiService(get(), get()) }
}
