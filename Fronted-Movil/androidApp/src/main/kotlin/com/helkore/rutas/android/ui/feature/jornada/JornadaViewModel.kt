package com.helkore.rutas.android.ui.feature.jornada

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.flota.GetFlotaUseCase
import com.helkore.rutas.application.usecase.jornada.EndJornadaUseCase
import com.helkore.rutas.application.usecase.jornada.GetJornadaActivaUseCase
import com.helkore.rutas.application.usecase.jornada.StartJornadaUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutasUseCase
import com.helkore.rutas.application.usecase.telemetria.SendTelemetriaUseCase
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.jornada.StartJornadaInput
import com.helkore.rutas.domain.model.ruta.Coordenada
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.model.ruta.RutaParadero
import com.helkore.rutas.domain.model.telemetria.SendTelemetriaInput

private val MOCK_RUTAS = listOf(
    Ruta(1, "Ruta A", "#5B4FCF", emptyList(), 200, true, listOf(
        RutaParadero(1, 1, 0, Paradero(1, "UPN Campus", null, -8.1091, -79.0215, true, true)),
        RutaParadero(1, 2, 1, Paradero(2, "Plaza Mayor", null, -8.1120, -79.0280, false, true)),
        RutaParadero(1, 3, 2, Paradero(3, "Terminal Norte", null, -8.0950, -79.0190, true, true))
    )),
    Ruta(2, "Ruta B", "#F97316", emptyList(), 200, true, listOf(
        RutaParadero(2, 4, 0, Paradero(4, "UPN Ingeniería", null, -8.1100, -79.0230, true, true)),
        RutaParadero(2, 5, 1, Paradero(5, "Ovalo Grau", null, -8.1150, -79.0310, false, true)),
        RutaParadero(2, 6, 2, Paradero(6, "Mercado Central", null, -8.1200, -79.0350, true, true))
    )),
    Ruta(3, "Ruta C", "#22C55E", emptyList(), 200, true, listOf(
        RutaParadero(3, 7, 0, Paradero(7, "Residencia UPN", null, -8.1080, -79.0200, true, true)),
        RutaParadero(3, 8, 1, Paradero(8, "Centro Comercial", null, -8.1170, -79.0290, false, true)),
        RutaParadero(3, 9, 2, Paradero(9, "Terminal Sur", null, -8.1250, -79.0400, true, true))
    ))
)

private val MOCK_UNIDADES = listOf(
    Unidad(1, "B-01", "ABC-123", 40, true),
    Unidad(2, "B-02", "DEF-456", 35, true),
    Unidad(3, "B-03", "GHI-789", 40, true)
)

class JornadaViewModel(
    private val getJornadaActivaUseCase: GetJornadaActivaUseCase,
    private val startJornadaUseCase: StartJornadaUseCase,
    private val endJornadaUseCase: EndJornadaUseCase,
    private val getRutasUseCase: GetRutasUseCase,
    private val getFlotaUseCase: GetFlotaUseCase,
    private val sendTelemetriaUseCase: SendTelemetriaUseCase
) : BaseViewModel<JornadaState, JornadaIntent, JornadaEffect>(JornadaState()) {

    init { process(JornadaIntent.Load) }

    override fun process(intent: JornadaIntent) {
        when (intent) {
            is JornadaIntent.Load               -> loadAll()
            is JornadaIntent.SelectRuta         -> updateState { copy(rutaSeleccionada = intent.rutaId) }
            is JornadaIntent.SelectUnidad       -> updateState { copy(unidadSeleccionada = intent.unidadId) }
            is JornadaIntent.IniciarJornada     -> iniciarJornada()
            is JornadaIntent.IniciarJornadaMock -> iniciarJornadaMock()
            is JornadaIntent.FinalizarJornada   -> finalizarJornada()
            is JornadaIntent.ToggleGps          -> toggleGps()
            is JornadaIntent.ReportarIncidencia -> emitEffect(JornadaEffect.NavigateToIncidencia(intent.jornadaId))
            is JornadaIntent.NuevaUbicacion     -> onNuevaUbicacion(intent.lat, intent.lng, intent.velocidad)
        }
    }

    private fun loadAll() {
        launch {
            updateState { copy(loading = true) }
            runCatching { getJornadaActivaUseCase() }
                .onSuccess { jornada -> updateState { copy(jornadaActiva = jornada) } }
            val rutasResult = runCatching { getRutasUseCase() }
            val rutasBackend = rutasResult.getOrNull()?.filter { it.activa } ?: emptyList()
            updateState { copy(rutas = rutasBackend.ifEmpty { MOCK_RUTAS }) }
            val unidadesResult = runCatching { getFlotaUseCase() }
            val unidadesBackend = unidadesResult.getOrNull()?.filter { it.activa } ?: emptyList()
            updateState { copy(unidades = unidadesBackend.ifEmpty { MOCK_UNIDADES }) }
            updateState { copy(loading = false) }
        }
    }

    private fun iniciarJornadaMock() {
        val s = state.value
        val rutaId = s.rutaSeleccionada ?: MOCK_RUTAS.first().id
        val unidadId = s.unidadSeleccionada ?: MOCK_UNIDADES.first().id
        val jornadaMock = Jornada(
            id = 999,
            conductorId = "mock-conductor",
            unidadId = unidadId,
            rutaId = rutaId,
            inicioEn = System.currentTimeMillis(),
            finEn = null,
            activa = true
        )
        val rutasMock = if (s.rutas.isEmpty()) MOCK_RUTAS else s.rutas
        val unidadesMock = if (s.unidades.isEmpty()) MOCK_UNIDADES else s.unidades
        updateState {
            copy(
                jornadaActiva = jornadaMock,
                rutas = rutasMock,
                unidades = unidadesMock,
                loading = false,
                error = null
            )
        }
        emitEffect(JornadaEffect.ShowMessage("¡Jornada iniciada! Activa el GPS para empezar."))
    }

    private fun iniciarJornada() {
        val s = state.value
        val rutaId = s.rutaSeleccionada ?: run { emitEffect(JornadaEffect.ShowError("Selecciona una ruta")); return }
        val unidadId = s.unidadSeleccionada ?: run { emitEffect(JornadaEffect.ShowError("Selecciona una unidad")); return }
        launch {
            updateState { copy(loading = true) }
            runCatching { startJornadaUseCase(StartJornadaInput(unidadId, rutaId)) }
                .onSuccess { jornada ->
                    updateState { copy(loading = false, jornadaActiva = jornada) }
                    emitEffect(JornadaEffect.ShowMessage("¡Jornada iniciada! Activa el GPS para empezar."))
                }
                .onFailure { error ->
                    updateState { copy(loading = false) }
                    emitEffect(JornadaEffect.ShowError(error.message ?: "Error al iniciar"))
                }
        }
    }

    private fun finalizarJornada() {
        val jornadaId = state.value.jornadaActiva?.id ?: return
        // Jornada mock (id=999): finalizar localmente sin llamar al backend
        if (jornadaId == 999) {
            updateState { copy(jornadaActiva = null, gpsActivo = false) }
            emitEffect(JornadaEffect.ShowMessage("Jornada finalizada"))
            return
        }
        launch {
            updateState { copy(loading = true) }
            runCatching { endJornadaUseCase(jornadaId) }
                .onSuccess {
                    updateState { copy(loading = false, jornadaActiva = null, gpsActivo = false) }
                    emitEffect(JornadaEffect.ShowMessage("Jornada finalizada"))
                }
                .onFailure { error ->
                    updateState { copy(loading = false) }
                    emitEffect(JornadaEffect.ShowError(error.message ?: "Error al finalizar"))
                }
        }
    }

    private fun toggleGps() {
        val nuevoEstado = !state.value.gpsActivo
        updateState { copy(gpsActivo = nuevoEstado) }
        if (nuevoEstado) {
            emitEffect(JornadaEffect.SolicitarPermisosGps)
        }
    }

    private fun onNuevaUbicacion(lat: Double, lng: Double, velocidad: Double) {
        val jornadaId = state.value.jornadaActiva?.id ?: return
        if (!state.value.gpsActivo) return
        updateState { copy(ultimaLat = lat, ultimaLng = lng, velocidadKmh = velocidad) }
        launch {
            runCatching {
                sendTelemetriaUseCase(SendTelemetriaInput(jornadaId, lat, lng, velocidad, 4.0))
            }
        }
    }
}
