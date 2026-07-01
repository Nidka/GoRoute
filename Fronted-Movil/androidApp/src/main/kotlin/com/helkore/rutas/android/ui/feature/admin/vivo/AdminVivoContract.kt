package com.helkore.rutas.android.ui.feature.admin.vivo

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.jornada.Jornada

enum class BusEstado { Normal, Advertencia, Detenido }

data class BusEnVivo(
    val jornadaId: Int,
    val busNumero: String,     // "Bus #12"
    val rutaLabel: String,     // "Ruta 3"
    val velocidad: Int,        // km/h
    val estado: BusEstado,
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class AdminVivoState(
    val loading: Boolean = true,
    val buses: List<BusEnVivo> = emptyList(),
    val error: String? = null
) : UiState

sealed class AdminVivoIntent : UiIntent {
    object Load : AdminVivoIntent()
}

sealed class AdminVivoEffect : UiEffect {
    data class ShowError(val message: String) : AdminVivoEffect()
}
