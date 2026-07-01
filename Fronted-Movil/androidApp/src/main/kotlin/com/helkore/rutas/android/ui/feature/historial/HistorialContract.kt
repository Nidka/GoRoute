package com.helkore.rutas.android.ui.feature.historial

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.jornada.Jornada

enum class HistorialFiltro { Todas, Activas, Finalizadas }

data class HistorialState(
    val jornadas: List<Jornada> = emptyList(),
    val filtro: HistorialFiltro = HistorialFiltro.Todas,
    val loading: Boolean = false,
    val error: String? = null
) : UiState {
    val jornadasFiltradas: List<Jornada> get() = when (filtro) {
        HistorialFiltro.Todas      -> jornadas
        HistorialFiltro.Activas    -> jornadas.filter { it.activa }
        HistorialFiltro.Finalizadas -> jornadas.filter { !it.activa }
    }
}

sealed class HistorialIntent : UiIntent {
    object Load : HistorialIntent()
    data class SetFiltro(val filtro: HistorialFiltro) : HistorialIntent()
}

sealed class HistorialEffect : UiEffect
