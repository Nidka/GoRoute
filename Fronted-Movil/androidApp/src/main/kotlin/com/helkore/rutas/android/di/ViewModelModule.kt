package com.helkore.rutas.android.di

import com.helkore.rutas.android.ui.feature.admin.flota.AdminFlotaViewModel
import com.helkore.rutas.android.ui.feature.admin.panel.AdminPanelViewModel
import com.helkore.rutas.android.ui.feature.admin.rutas.AdminRutasViewModel
import com.helkore.rutas.android.ui.feature.admin.usuarios.AdminUsuariosViewModel
import com.helkore.rutas.android.ui.feature.admin.vivo.AdminVivoViewModel
import com.helkore.rutas.android.ui.feature.auth.login.LoginViewModel
import com.helkore.rutas.android.ui.feature.auth.register.RegisterViewModel
import com.helkore.rutas.android.ui.feature.historial.HistorialViewModel
import com.helkore.rutas.android.ui.feature.incidencia.IncidenciaViewModel
import com.helkore.rutas.android.ui.feature.jornada.JornadaViewModel
import com.helkore.rutas.android.ui.feature.mapa.MapaViewModel
import com.helkore.rutas.android.ui.feature.perfil.MiCodigoViewModel
import com.helkore.rutas.android.ui.feature.perfil.PerfilViewModel
import com.helkore.rutas.android.ui.feature.ruta.detail.RutaDetailViewModel
import com.helkore.rutas.android.ui.feature.ruta.list.RutaListViewModel
import com.helkore.rutas.android.ui.feature.scanner.EscanerViewModel
import com.helkore.rutas.android.ui.feature.splash.SplashViewModel
import com.helkore.rutas.android.ui.theme.ThemeRepository
import com.helkore.rutas.android.ui.theme.ThemeViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val viewModelModule = module {
    single { ThemeRepository(androidContext()) }
    viewModel { ThemeViewModel(get()) }
    viewModel { SplashViewModel(get(), get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get(), get()) }
    viewModel { RutaListViewModel(get(), get(), get(), get()) }
    viewModel { (rutaId: Int) -> RutaDetailViewModel(get(), get(), get(), get(), get(), get(), get(), androidContext(), rutaId) }
    viewModel { (jornadaId: Int) -> MapaViewModel(get(), get(), jornadaId) }
    viewModel { JornadaViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { (jornadaId: Int) -> IncidenciaViewModel(get(), get(), get(), jornadaId) }
    viewModel { PerfilViewModel(get(), get()) }
    viewModel { MiCodigoViewModel(get(), get()) }
    viewModel { HistorialViewModel(get()) }
    viewModel { EscanerViewModel(get()) }
    // Admin
    viewModel { AdminPanelViewModel(get(), get(), get(), get(), get()) }
    viewModel { AdminVivoViewModel(get(), get(), get()) }
    viewModel { AdminFlotaViewModel(get(), get(), get()) }
    viewModel { AdminRutasViewModel(get(), get(), get(), get(), get(), get(), get()) }
    viewModel { AdminUsuariosViewModel(get(), get(), get()) }
}
