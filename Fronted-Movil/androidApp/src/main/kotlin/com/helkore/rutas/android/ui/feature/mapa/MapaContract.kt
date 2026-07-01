package com.helkore.rutas.android.ui.feature.mapa

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.eta.Eta
import com.helkore.rutas.domain.model.telemetria.Telemetria

data class MapaState(
    val telemetria: Telemetria? = null,
    val etas: List<Eta> = emptyList(),
    val conectado: Boolean = false,
    val error: String? = null
) : UiState

sealed class MapaIntent : UiIntent {
    object StartObserving : MapaIntent()
    object StopObserving : MapaIntent()
}

sealed class MapaEffect : UiEffect {
    data class ShowError(val message: String) : MapaEffect()
}
