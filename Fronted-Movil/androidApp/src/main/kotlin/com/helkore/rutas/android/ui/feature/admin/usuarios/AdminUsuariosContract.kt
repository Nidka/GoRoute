package com.helkore.rutas.android.ui.feature.admin.usuarios

import com.helkore.rutas.android.ui.core.UiEffect
import com.helkore.rutas.android.ui.core.UiIntent
import com.helkore.rutas.android.ui.core.UiState
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.model.usuario.CreateUsuarioInput
import com.helkore.rutas.domain.model.usuario.Usuario

enum class UsuarioFiltro(val label: String) {
    Todos("Todos"),
    Estudiantes("Estudiantes"),
    Conductores("Conductores"),
    Admins("Admins")
}

data class AdminUsuariosState(
    val loading: Boolean = true,
    val error: String? = null,
    val usuarios: List<Usuario> = emptyList(),
    val busqueda: String = "",
    val filtro: UsuarioFiltro = UsuarioFiltro.Todos,
    val showCreateDialog: Boolean = false,
    val createRolId: RolId = RolId.Estudiante,
    val createNombres: String = "",
    val createApellidos: String = "",
    val createCorreo: String = "",
    val createPassword: String = "",
    val createCodigoUpn: String = "",
    val createFacultad: String = "",
    val createLicencia: String = "",
    val createTelefono: String = "",
    val createError: String? = null,
    val saving: Boolean = false,
    val deleteTargetId: String? = null,
    val deleting: Boolean = false
) : UiState {
    val usuariosFiltrados: List<Usuario> get() {
        var list = usuarios
        if (filtro != UsuarioFiltro.Todos) {
            list = list.filter {
                when (filtro) {
                    UsuarioFiltro.Estudiantes -> it.rolId == RolId.Estudiante
                    UsuarioFiltro.Conductores -> it.rolId == RolId.Conductor
                    UsuarioFiltro.Admins      -> it.rolId == RolId.Administrador
                    else                      -> true
                }
            }
        }
        if (busqueda.isNotBlank()) {
            val q = busqueda.lowercase()
            list = list.filter {
                it.nombres.lowercase().contains(q) ||
                it.apellidos.lowercase().contains(q) ||
                it.correo.lowercase().contains(q)
            }
        }
        return list
    }
}

sealed class AdminUsuariosIntent : UiIntent {
    object Load : AdminUsuariosIntent()
    data class BusquedaChanged(val value: String) : AdminUsuariosIntent()
    data class FiltroChanged(val filtro: UsuarioFiltro) : AdminUsuariosIntent()
    object OpenCreateDialog : AdminUsuariosIntent()
    object CloseCreateDialog : AdminUsuariosIntent()
    data class CreateRolChanged(val rolId: RolId) : AdminUsuariosIntent()
    data class CreateNombresChanged(val value: String) : AdminUsuariosIntent()
    data class CreateApellidosChanged(val value: String) : AdminUsuariosIntent()
    data class CreateCorreoChanged(val value: String) : AdminUsuariosIntent()
    data class CreatePasswordChanged(val value: String) : AdminUsuariosIntent()
    data class CreateCodigoUpnChanged(val value: String) : AdminUsuariosIntent()
    data class CreateFacultadChanged(val value: String) : AdminUsuariosIntent()
    data class CreateLicenciaChanged(val value: String) : AdminUsuariosIntent()
    data class CreateTelefonoChanged(val value: String) : AdminUsuariosIntent()
    object SubmitCreate : AdminUsuariosIntent()
    data class DeleteRequested(val usuarioId: String) : AdminUsuariosIntent()
    object CancelDelete : AdminUsuariosIntent()
    object ConfirmDelete : AdminUsuariosIntent()
}

sealed class AdminUsuariosEffect : UiEffect {
    data class ShowError(val message: String) : AdminUsuariosEffect()
    data class ShowMessage(val message: String) : AdminUsuariosEffect()
}
