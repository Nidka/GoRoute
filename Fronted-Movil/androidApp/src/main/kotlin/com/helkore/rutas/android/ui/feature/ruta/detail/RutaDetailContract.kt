package com.helkore.rutas.android.ui.feature.ruta.detail

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.eta.Eta
import com.helkore.rutas.domain.model.flota.Unidad
import com.helkore.rutas.domain.model.incidencia.Incidencia
import com.helkore.rutas.domain.model.jornada.Jornada
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.model.usuario.Usuario

data class BusPosition(val lat: Double, val lng: Double, val velocidad: Double)

data class RutaDetailState(
    val ruta: Ruta? = null,
    val jornadaActiva: Jornada? = null,
    val conductor: Usuario? = null,
    val unidad: Unidad? = null,
    val etas: List<Eta> = emptyList(),
    val incidencias: List<Incidencia> = emptyList(),
    val busPosition: BusPosition? = null,
    val paraderoProximo: String? = null,
    val loading: Boolean = false,
    val error: String? = null
) : UiState

sealed class RutaDetailIntent : UiIntent {
    object Load : RutaDetailIntent()
    data class VerMapa(val jornadaId: Int) : RutaDetailIntent()
}

sealed class RutaDetailEffect : UiEffect {
    data class NavigateToMapa(val jornadaId: Int) : RutaDetailEffect()
    data class ShowError(val message: String) : RutaDetailEffect()
}
