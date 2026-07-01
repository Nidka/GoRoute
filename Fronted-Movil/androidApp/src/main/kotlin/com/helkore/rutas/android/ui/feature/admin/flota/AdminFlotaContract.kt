package com.helkore.rutas.android.ui.feature.admin.flota

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.flota.Unidad

data class AdminFlotaState(
    val loading: Boolean = true,
    val error: String? = null,
    val unidades: List<Unidad> = emptyList(),
    val showDialog: Boolean = false,
    val editingId: Int? = null,
    val numero: String = "",
    val placa: String = "",
    val capacidad: String = "",
    val activa: Boolean = true,
    val dialogError: String? = null,
    val saving: Boolean = false
) : UiState

sealed class AdminFlotaIntent : UiIntent {
    object Load : AdminFlotaIntent()
    object OpenCreate : AdminFlotaIntent()
    data class OpenEdit(val unidad: Unidad) : AdminFlotaIntent()
    object CloseDialog : AdminFlotaIntent()
    data class NumeroChanged(val value: String) : AdminFlotaIntent()
    data class PlacaChanged(val value: String) : AdminFlotaIntent()
    data class CapacidadChanged(val value: String) : AdminFlotaIntent()
    data class ActivaChanged(val value: Boolean) : AdminFlotaIntent()
    object Submit : AdminFlotaIntent()
}

sealed class AdminFlotaEffect : UiEffect {
    data class ShowError(val message: String) : AdminFlotaEffect()
    data class ShowMessage(val message: String) : AdminFlotaEffect()
}