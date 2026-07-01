package com.helkore.rutas.android.ui.feature.jornada

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.ruta.Ruta

data class JornadaState(
    val jornadaActiva: Jornada? = null,
    val rutas: List<Ruta> = emptyList(),
    val unidades: List<Unidad> = emptyList(),
    val rutaSeleccionada: Int? = null,
    val unidadSeleccionada: Int? = null,
    val gpsActivo: Boolean = false,
    val ultimaLat: Double? = null,
    val ultimaLng: Double? = null,
    val velocidadKmh: Double = 0.0,
    val loading: Boolean = false,
    val error: String? = null
) : UiState

sealed class JornadaIntent : UiIntent {
    object Load : JornadaIntent()
    data class SelectRuta(val rutaId: Int) : JornadaIntent()
    data class SelectUnidad(val unidadId: Int) : JornadaIntent()
    object IniciarJornada : JornadaIntent()
    object IniciarJornadaMock : JornadaIntent()
    object FinalizarJornada : JornadaIntent()
    object ToggleGps : JornadaIntent()
    data class ReportarIncidencia(val jornadaId: Int) : JornadaIntent()
    data class NuevaUbicacion(val lat: Double, val lng: Double, val velocidad: Double) : JornadaIntent()
}

sealed class JornadaEffect : UiEffect {
    data class NavigateToIncidencia(val jornadaId: Int) : JornadaEffect()
    data class ShowError(val message: String) : JornadaEffect()
    data class ShowMessage(val message: String) : JornadaEffect()
    object SolicitarPermisosGps : JornadaEffect()
}
