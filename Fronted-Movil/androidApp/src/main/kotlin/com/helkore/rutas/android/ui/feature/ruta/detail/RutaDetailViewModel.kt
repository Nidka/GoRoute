package com.helkore.rutas.android.ui.feature.ruta.detail

import android.content.Context
import com.helkore.rutas.android.notification.BusNotificationHelper
import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.eta.GetEtaUseCase
import com.helkore.rutas.application.usecase.flota.GetFlotaUseCase
import com.helkore.rutas.application.usecase.incidencia.GetIncidenciasUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutaDetailUseCase
import com.helkore.rutas.application.usecase.telemetria.ObserveTelemetriaUseCase
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.port.repository.JornadaRepository
import com.helkore.rutas.domain.port.repository.UsuarioRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private const val METROS_CERCA    = 300.0   // notificar "está cerca" dentro de 300m
private const val METROS_LLEGO    = 80.0    // notificar "llegó" dentro de 80m

class RutaDetailViewModel(
    private val getRutaDetailUseCase: GetRutaDetailUseCase,
    private val jornadaRepository: JornadaRepository,
    private val getIncidenciasUseCase: GetIncidenciasUseCase,
    private val getEtaUseCase: GetEtaUseCase,
    private val usuarioRepository: UsuarioRepository,
    private val getFlotaUseCase: GetFlotaUseCase,
    private val observeTelemetriaUseCase: ObserveTelemetriaUseCase,
    private val context: Context,
    private val rutaId: Int
) : BaseViewModel<RutaDetailState, RutaDetailIntent, RutaDetailEffect>(RutaDetailState()) {

    private var wsJob: Job? = null
    // Paraderos ya notificados para no repetir la alerta
    private val paraderosCerca  = mutableSetOf<Int>()
    private val paraderosLlego  = mutableSetOf<Int>()

    init { process(RutaDetailIntent.Load) }

    override fun process(intent: RutaDetailIntent) {
        when (intent) {
            is RutaDetailIntent.Load    -> load()
            is RutaDetailIntent.VerMapa -> emitEffect(RutaDetailEffect.NavigateToMapa(intent.jornadaId))
        }
    }

    override fun onCleared() {
        super.onCleared()
        wsJob?.cancel()
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }

            val ruta = runCatching { getRutaDetailUseCase(rutaId) }
                .onFailure { e -> updateState { copy(loading = false, error = e.message) }; return@launch }
                .getOrNull() ?: return@launch

            updateState { copy(ruta = ruta) }

            // Jornada activa de esta ruta
            val jornada = runCatching { jornadaRepository.getActivaByRuta(rutaId) }.getOrNull()
            updateState { copy(jornadaActiva = jornada) }

            if (jornada != null) {
                // Incidencias
                runCatching { getIncidenciasUseCase.byJornada(jornada.id) }
                    .onSuccess { updateState { copy(incidencias = it) } }

                // ETAs reales por paradero
                runCatching { getEtaUseCase(jornada.id) }
                    .onSuccess { updateState { copy(etas = it) } }

                // Conductor — GET /usuarios/:id abierto a todos
                runCatching { usuarioRepository.getById(jornada.conductorId) }
                    .onSuccess { updateState { copy(conductor = it) } }

                // Unidad — GET /flota abierto a todos
                runCatching { getFlotaUseCase() }
                    .onSuccess { flota ->
                        updateState { copy(unidad = flota.firstOrNull { it.id == jornada.unidadId }) }
                    }

                // WebSocket — posición en tiempo real
                suscribirWebSocket(jornada.id)
            }

            updateState { copy(loading = false) }
        }
    }

    private fun suscribirWebSocket(jornadaId: Int) {
        wsJob?.cancel()
        wsJob = viewModelScope.launch {
            observeTelemetriaUseCase(jornadaId)
                .catch { /* conexión caída — el ViewModel se destruye con la pantalla */ }
                .collect { telemetria ->
                    updateState {
                        copy(busPosition = BusPosition(telemetria.lat, telemetria.lng, telemetria.velocidad ?: 0.0))
                    }
                    verificarProximidadParaderos(telemetria.lat, telemetria.lng)
                }
        }
    }

    private fun verificarProximidadParaderos(busLat: Double, busLng: Double) {
        val ruta = state.value.ruta ?: return
        val rutaNombre = ruta.nombre

        ruta.paraderos.forEach { rp ->
            val paradero = rp.paradero ?: return@forEach
            val dist = distanciaMetros(busLat, busLng, paradero.lat, paradero.lng)

            // Cerca: primera vez que entra a 300m
            if (dist <= METROS_CERCA && paraderosCerca.add(paradero.id)) {
                val etaMin = (dist / 250).toInt().coerceAtLeast(1)
                updateState { copy(paraderoProximo = paradero.nombre) }
                BusNotificationHelper.notifyBusCercano(context, rutaNombre, paradero.nombre, etaMin)
            }

            // Llegó: primera vez que entra a 80m
            if (dist <= METROS_LLEGO && paraderosLlego.add(paradero.id)) {
                BusNotificationHelper.notifyBusLlego(context, rutaNombre, paradero.nombre)
            }
        }
    }

    // Fórmula Haversine — distancia en metros entre dos coordenadas GPS
    private fun distanciaMetros(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val r = 6_371_000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)
        return r * 2 * atan2(sqrt(a), sqrt(1 - a))
    }
}
