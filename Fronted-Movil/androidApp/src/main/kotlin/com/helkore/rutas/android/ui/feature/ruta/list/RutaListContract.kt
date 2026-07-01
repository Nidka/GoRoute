package com.helkore.rutas.android.ui.feature.ruta.list

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.ruta.Ruta
import com.helkore.rutas.domain.model.usuario.Usuario

data class RutaHomeInfo(
    val ruta: Ruta,
    val activeBusCount: Int = 0,
    val etaMin: Int? = null
) {
    val hasActiveBus: Boolean get() = activeBusCount > 0
}

data class RutaListState(
    val rutas: List<Ruta> = emptyList(),
    val routeInfo: List<RutaHomeInfo> = emptyList(),
    val usuario: Usuario? = null,
    val loading: Boolean = false,
    val error: String? = null
) : UiState

sealed class RutaListIntent : UiIntent {
    object Load : RutaListIntent()
    data class SelectRuta(val rutaId: Int) : RutaListIntent()
}

sealed class RutaListEffect : UiEffect {
    data class NavigateToDetail(val rutaId: Int) : RutaListEffect()
    data class ShowError(val message: String) : RutaListEffect()
}
