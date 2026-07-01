package com.helkore.rutas.android.ui.feature.admin.rutas

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.ruta.Paradero
import com.helkore.rutas.domain.model.ruta.Ruta

data class AdminRutasState(
    val loading: Boolean = true,
    val error: String? = null,
    val rutas: List<Ruta> = emptyList(),
    val showDialog: Boolean = false,
    val editingId: Int? = null,
    val nombre: String = "",
    val colorHex: String = "#2F80ED",
    val margenM: String = "200",
    val dialogError: String? = null,
    val saving: Boolean = false,
    val showParaderoDialog: Boolean = false,
    val selectedRutaId: Int? = null,
    val selectedRutaNombre: String = "",
    val paraderos: List<Paradero> = emptyList(),
    val paraderosLoading: Boolean = false,
    val paraderosError: String? = null,
    val paraderoNombre: String = "",
    val paraderoDescripcion: String = "",
    val paraderoLat: String = "",
    val paraderoLng: String = "",
    val paraderoTerminal: Boolean = false,
    val paraderoSaving: Boolean = false,
    val paraderoDialogError: String? = null
) : UiState

sealed class AdminRutasIntent : UiIntent {
    object Load : AdminRutasIntent()
    object OpenCreate : AdminRutasIntent()
    data class OpenEdit(val ruta: Ruta) : AdminRutasIntent()
    object CloseDialog : AdminRutasIntent()
    data class NombreChanged(val value: String) : AdminRutasIntent()
    data class ColorChanged(val value: String) : AdminRutasIntent()
    data class MargenChanged(val value: String) : AdminRutasIntent()
    object Submit : AdminRutasIntent()
    data class OpenParaderos(val ruta: Ruta) : AdminRutasIntent()
    object CloseParaderoDialog : AdminRutasIntent()
    data class ParaderoNombreChanged(val value: String) : AdminRutasIntent()
    data class ParaderoDescripcionChanged(val value: String) : AdminRutasIntent()
    data class ParaderoLatChanged(val value: String) : AdminRutasIntent()
    data class ParaderoLngChanged(val value: String) : AdminRutasIntent()
    data class ParaderoTerminalChanged(val value: Boolean) : AdminRutasIntent()
    object SubmitParadero : AdminRutasIntent()
    data class AttachParadero(val paradero: Paradero) : AdminRutasIntent()
}

sealed class AdminRutasEffect : UiEffect {
    data class ShowError(val message: String) : AdminRutasEffect()
    data class ShowMessage(val message: String) : AdminRutasEffect()
}