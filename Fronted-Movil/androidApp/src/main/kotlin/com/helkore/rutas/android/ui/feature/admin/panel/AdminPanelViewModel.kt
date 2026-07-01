package com.helkore.rutas.android.ui.feature.admin.panel

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.flota.GetFlotaUseCase
import com.helkore.rutas.application.usecase.incidencia.GetIncidenciasUseCase
import com.helkore.rutas.application.usecase.jornada.GetJornadasUseCase
import com.helkore.rutas.application.usecase.ruta.GetRutasUseCase
import com.helkore.rutas.application.usecase.usuario.GetUsuariosUseCase
import java.util.concurrent.TimeUnit

private val MOCK_RUTAS_CURSO = listOf(
    RutaCursoUi("RUTA A", "#5B4FCF", 24, 0),
    RutaCursoUi("RUTA B", "#F97316", 28, 5),
    RutaCursoUi("RUTA C", "#5B4FCF", 31, 0)
)

private val MOCK_INCIDENCIAS = listOf(
    IncidenciaUi("Ruta B – Tráfico intenso", "Hace 5 min", "#F97316"),
    IncidenciaUi("Ruta A – Falla mecánica", "Hace 12 min", "#F97316")
)

class AdminPanelViewModel(
    private val getJornadasUseCase: GetJornadasUseCase,
    private val getIncidenciasUseCase: GetIncidenciasUseCase,
    private val getFlotaUseCase: GetFlotaUseCase,
    private val getRutasUseCase: GetRutasUseCase,
    private val getUsuariosUseCase: GetUsuariosUseCase
) : BaseViewModel<AdminPanelState, AdminPanelIntent, AdminPanelEffect>(AdminPanelState()) {

    init { load() }

    override fun process(intent: AdminPanelIntent) {
        when (intent) {
            is AdminPanelIntent.Load -> load()
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching {
                val jornadas    = runCatching { getJornadasUseCase(soloActivas = true) }.getOrDefault(emptyList())
                val incidencias = runCatching { getIncidenciasUseCase.all() }.getOrDefault(emptyList())
                val rutas       = runCatching { getRutasUseCase() }.getOrDefault(emptyList())

                val rutasEnCurso = if (jornadas.isEmpty()) MOCK_RUTAS_CURSO else {
                    jornadas.mapNotNull { j ->
                        val ruta = rutas.firstOrNull { it.id == j.rutaId } ?: return@mapNotNull null
                        RutaCursoUi(
                            nombre    = ruta.nombre,
                            colorHex  = ruta.colorHex,
                            alumnos   = (20..40).random(),
                            retrasoMin = 0
                        )
                    }.ifEmpty { MOCK_RUTAS_CURSO }
                }

                val incidenciasUi = if (incidencias.isEmpty()) MOCK_INCIDENCIAS else {
                    incidencias.take(3).map { inc ->
                        val hace = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - inc.registradoEn)
                        val ruta = rutas.firstOrNull { r -> jornadas.any { j -> j.id == inc.jornadaId && j.rutaId == r.id } }
                        IncidenciaUi(
                            descripcion = "${ruta?.nombre ?: "Ruta"} – ${inc.tipoNombre}",
                            hace        = "Hace $hace min",
                            colorHex    = ruta?.colorHex ?: "#F97316"
                        )
                    }.ifEmpty { MOCK_INCIDENCIAS }
                }

                updateState {
                    copy(
                        loading              = false,
                        rutasActivas         = rutasEnCurso.size,
                        estudiantesEnRuta    = rutasEnCurso.sumOf { it.alumnos }.coerceAtLeast(if (jornadas.isEmpty()) 312 else 0),
                        incidenciasHoy       = incidenciasUi.size,
                        rutasEnCurso         = rutasEnCurso,
                        incidenciasRecientes = incidenciasUi
                    )
                }
            }.onFailure {
                // Usar mock cuando no hay backend
                updateState {
                    copy(
                        loading              = false,
                        rutasActivas         = 6,
                        estudiantesEnRuta    = 312,
                        incidenciasHoy       = 2,
                        rutasEnCurso         = MOCK_RUTAS_CURSO,
                        incidenciasRecientes = MOCK_INCIDENCIAS
                    )
                }
            }
        }
    }
}
