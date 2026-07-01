package com.helkore.rutas.android.ui.feature.incidencia

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.incidencia.Incidencia

data class IncidenciaState(
    val incidencias: List<Incidencia> = emptyList(),
    val tipoSeleccionado: Int = 1,
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val loading: Boolean = false,
    val enviando: Boolean = false,
    val error: String? = null
) : UiState

sealed class IncidenciaIntent : UiIntent {
    object Load : IncidenciaIntent()
    data class SelectTipo(val tipoId: Int) : IncidenciaIntent()
    data class UpdateUbicacion(val lat: Double, val lng: Double) : IncidenciaIntent()
    object Reportar : IncidenciaIntent()
    data class Resolve(val incidenciaId: Long) : IncidenciaIntent()
}

sealed class IncidenciaEffect : UiEffect {
    data class ShowMessage(val message: String) : IncidenciaEffect()
    data class ShowError(val message: String) : IncidenciaEffect()
    object ReportadoExitoso : IncidenciaEffect()
}
