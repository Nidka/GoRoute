package com.helkore.rutas.android.ui.feature.ruta.list

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.eta.GetEtaUseCase
import com.helkore.rutas.application.usecase.jornada.GetJornadasUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutasUseCase
import com.helkore.rutas.application.usecase.usuario.GetPerfilUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class RutaListViewModel(
    private val getRutasUseCase: GetRutasUseCase,
    private val getJornadasUseCase: GetJornadasUseCase,
    private val getEtaUseCase: GetEtaUseCase,
    private val getPerfilUseCase: GetPerfilUseCase
) : BaseViewModel<RutaListState, RutaListIntent, RutaListEffect>(RutaListState()) {

    init {
        process(RutaListIntent.Load)
    }

    override fun process(intent: RutaListIntent) {
        when (intent) {
            is RutaListIntent.Load -> loadRutas()
            is RutaListIntent.SelectRuta -> emitEffect(RutaListEffect.NavigateToDetail(intent.rutaId))
        }
    }

    private fun loadRutas() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching {
                coroutineScope {
                    val rutasDeferred = async { getRutasUseCase() }
                    val jornadasDeferred = async { runCatching { getJornadasUseCase(soloActivas = true) }.getOrDefault(emptyList()) }
                    val usuarioDeferred = async { runCatching { getPerfilUseCase() }.getOrNull() }

                    val rutas = rutasDeferred.await()
                    val jornadas = jornadasDeferred.await().filter { it.activa }
                    val usuario = usuarioDeferred.await()
                    val jornadasByRuta = jornadas.groupBy { it.rutaId }
                    val etaByJornada = jornadas.associate { jornada ->
                        jornada.id to runCatching { getEtaUseCase(jornada.id) }.getOrDefault(emptyList())
                    }

                    val routeInfo = rutas.map { ruta ->
                        val rutaJornadas = jornadasByRuta[ruta.id].orEmpty()
                        val etaMin = rutaJornadas
                            .flatMap { etaByJornada[it.id].orEmpty() }
                            .minOfOrNull { eta -> ((eta.etaSegundos + 59) / 60).coerceAtLeast(1) }

                        RutaHomeInfo(
                            ruta = ruta,
                            activeBusCount = rutaJornadas.size,
                            etaMin = etaMin
                        )
                    }

                    Triple(rutas, routeInfo, usuario)
                }
            }
                .onSuccess { (rutas, routeInfo, usuario) ->
                    updateState { copy(loading = false, rutas = rutas, routeInfo = routeInfo, usuario = usuario) }
                }
                .onFailure { error ->
                    updateState { copy(loading = false, error = error.message) }
                    emitEffect(RutaListEffect.ShowError(error.message ?: "Error al cargar rutas"))
                }
        }
    }
}
