package com.helkore.rutas.android.ui.feature.scanner

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.port.repository.UsuarioRepository

data class EscanerState(
    val estudianteConfirmado: EstudianteScaneado? = null,
    val totalAbordo: Int = 0,
    val loading: Boolean = false,
    val error: String? = null
) : UiState

sealed class EscanerIntent : UiIntent {
    data class ValidarCodigo(val codigo: String) : EscanerIntent()
    object Reset : EscanerIntent()
}

sealed class EscanerEffect : UiEffect

private val MOCK_ESTUDIANTES = mapOf(
    "N00345487" to EstudianteScaneado(
        nombreCompleto = "Francisco Kadin Valera Lizarraga",
        universidad    = "UPN",
        codigoUpn      = "N00345487",
        facultad       = "Ing. Sistemas",
        codigoAcceso   = "N00345487"
    ),
    "UPN-2021-04821" to EstudianteScaneado(
        nombreCompleto = "Juan Pérez",
        universidad    = "UPN",
        codigoUpn      = "2021-04821",
        facultad       = "Ing. Sistemas",
        codigoAcceso   = "UPN-2021-04821"
    )
)

class EscanerViewModel(
    private val usuarioRepository: UsuarioRepository
) : BaseViewModel<EscanerState, EscanerIntent, EscanerEffect>(EscanerState()) {

    private var jornadaId: Int = 0

    fun setJornadaId(id: Int) { jornadaId = id }

    override fun process(intent: EscanerIntent) {
        when (intent) {
            is EscanerIntent.ValidarCodigo -> validar(intent.codigo)
            is EscanerIntent.Reset         -> updateState { copy(estudianteConfirmado = null, error = null) }
        }
    }

    fun validarCodigo(codigo: String) = process(EscanerIntent.ValidarCodigo(codigo))
    fun resetScan()                    = process(EscanerIntent.Reset)

    private fun validar(codigo: String) {
        launch {
            updateState { copy(loading = true, error = null) }
            val codigoNormalizado = codigo.trim().uppercase()

            // Buscar primero en datos mock (demo sin backend)
            val mockKey = MOCK_ESTUDIANTES.keys.firstOrNull {
                it.equals(codigoNormalizado, ignoreCase = true) ||
                it.replace("-", "").equals(codigoNormalizado.replace("-", ""), ignoreCase = true)
            }
            val mockEstudiante = mockKey?.let { MOCK_ESTUDIANTES[it] }

            if (mockEstudiante != null) {
                updateState {
                    copy(loading = false, estudianteConfirmado = mockEstudiante, totalAbordo = totalAbordo + 1, error = null)
                }
                return@launch
            }

            // Si no está en mock, intenta con el backend
            runCatching {
                val estudiante = runCatching {
                    usuarioRepository.getEstudianteByCodigo(codigoNormalizado)
                }.getOrElse {
                    val usuarios = usuarioRepository.list()
                    usuarios.firstNotNullOfOrNull { usuario ->
                        runCatching {
                            usuarioRepository.getEstudiante(usuario.id)
                                .takeIf { it.codigoAcceso == codigoNormalizado }
                        }.getOrNull()
                    } ?: throw Exception("Código no encontrado: $codigoNormalizado")
                }
                val usuario = estudiante.usuario
                EstudianteScaneado(
                    nombreCompleto = usuario?.nombreCompleto ?: "Estudiante verificado",
                    universidad    = "UPN",
                    codigoUpn      = estudiante.codigoUpn,
                    facultad       = estudiante.facultad,
                    codigoAcceso   = estudiante.codigoAcceso.ifBlank { codigoNormalizado }
                )
            }.onSuccess { est ->
                updateState { copy(loading = false, estudianteConfirmado = est, totalAbordo = totalAbordo + 1) }
            }.onFailure { e ->
                updateState { copy(loading = false, error = e.message ?: "Código no reconocido") }
            }
        }
    }
}
