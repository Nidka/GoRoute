package com.helkore.rutas.android.di

import com.helkore.rutas.application.usecase.auth.CheckSessionUseCase
import com.helkore.rutas.application.usecase.auth.GetMeUseCase
import com.helkore.rutas.application.usecase.auth.LoginUseCase
import com.helkore.rutas.application.usecase.auth.LogoutUseCase
import com.helkore.rutas.application.usecase.auth.RegisterUseCase
import com.helkore.rutas.application.usecase.flota.CreateFlotaUseCase
import com.helkore.rutas.application.usecase.eta.GetEtaUseCase
import com.helkore.rutas.application.usecase.flota.GetFlotaUseCase
import com.helkore.rutas.application.usecase.flota.UpdateFlotaUseCase
import com.helkore.rutas.application.usecase.incidencia.GetIncidenciasUseCase
import com.helkore.rutas.application.usecase.incidencia.ReportIncidenciaUseCase
import com.helkore.rutas.application.usecase.incidencia.ResolveIncidenciaUseCase
import com.helkore.rutas.application.usecase.jornada.EndJornadaUseCase
import com.helkore.rutas.application.usecase.jornada.GetJornadaActivaUseCase
import com.helkore.rutas.application.usecase.jornada.GetJornadasUseCase
import com.helkore.rutas.application.usecase.jornada.StartJornadaUseCase
import com.helkore.rutas.application.usecase.ruta.AddParaderoToRutaUseCase
import com.helkore.rutas.application.usecase.ruta.CreateParaderoUseCase
import com.helkore.rutas.application.usecase.ruta.CreateRutaUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutaDetailUseCase
import com.helkore.rutas.application.usecase.ruta.GetParaderosUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutasUseCase
import com.helkore.rutas.application.usecase.ruta.RemoveParaderoFromRutaUseCase
import com.helkore.rutas.application.usecase.ruta.UpdateRutaUseCase
import com.helkore.rutas.application.usecase.telemetria.ObserveTelemetriaUseCase
import com.helkore.rutas.application.usecase.telemetria.SendTelemetriaUseCase
import com.helkore.rutas.application.usecase.usuario.CreateUsuarioUseCase
import com.helkore.rutas.application.usecase.usuario.GetPerfilUseCase
import com.helkore.rutas.application.usecase.usuario.GetUsuariosUseCase
import com.helkore.rutas.application.usecase.usuario.DeleteUsuarioUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { LoginUseCase(get(), get()) }
    factory { RegisterUseCase(get(), get()) }
    factory { LogoutUseCase(get(), get()) }
    factory { GetMeUseCase(get()) }
    factory { CheckSessionUseCase(get()) }
    factory { GetRutasUseCase(get()) }
    factory { GetRutaDetailUseCase(get()) }
    factory { GetParaderosUseCase(get()) }
    factory { CreateRutaUseCase(get()) }
    factory { UpdateRutaUseCase(get()) }
    factory { CreateParaderoUseCase(get()) }
    factory { AddParaderoToRutaUseCase(get()) }
    factory { RemoveParaderoFromRutaUseCase(get()) }
    factory { GetJornadaActivaUseCase(get()) }
    factory { GetJornadasUseCase(get()) }
    factory { StartJornadaUseCase(get()) }
    factory { EndJornadaUseCase(get()) }
    factory { ObserveTelemetriaUseCase(get()) }
    factory { SendTelemetriaUseCase(get()) }
    factory { GetEtaUseCase(get()) }
    factory { ReportIncidenciaUseCase(get()) }
    factory { ResolveIncidenciaUseCase(get()) }
    factory { GetIncidenciasUseCase(get()) }
    factory { GetPerfilUseCase(get()) }
    factory { GetFlotaUseCase(get()) }
    factory { CreateFlotaUseCase(get()) }
    factory { UpdateFlotaUseCase(get()) }
    factory { GetUsuariosUseCase(get()) }
    factory { CreateUsuarioUseCase(get()) }
    factory { DeleteUsuarioUseCase(get()) }
}
