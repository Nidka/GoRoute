package com.helkore.rutas.android.ui.feature.admin.panel

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState

data class RutaCursoUi(
    val nombre: String,
    val colorHex: String,
    val alumnos: Int,
    val retrasoMin: Int
)

data class IncidenciaUi(
    val descripcion: String,
    val hace: String,
    val colorHex: String
)

data class AdminPanelState(
    val loading: Boolean = true,
    val error: String? = null,
    val rutasActivas: Int = 0,
    val estudiantesEnRuta: Int = 0,
    val incidenciasHoy: Int = 0,
    val rutasEnCurso: List<RutaCursoUi> = emptyList(),
    val incidenciasRecientes: List<IncidenciaUi> = emptyList()
) : UiState

sealed class AdminPanelIntent : UiIntent {
    object Load : AdminPanelIntent()
}

sealed class AdminPanelEffect : UiEffect {
    data class ShowError(val message: String) : AdminPanelEffect()
}
