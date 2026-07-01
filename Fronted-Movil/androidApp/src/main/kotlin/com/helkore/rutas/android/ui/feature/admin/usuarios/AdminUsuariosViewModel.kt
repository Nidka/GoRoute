package com.helkore.rutas.android.ui.feature.admin.usuarios

import com.helkore.rutas.android.ui.core.BaseViewModel
import com.helkore.rutas.application.usecase.usuario.CreateUsuarioUseCase
import com.helkore.rutas.application.usecase.usuario.DeleteUsuarioUseCase
import com.helkore.rutas.application.usecase.usuario.GetUsuariosUseCase
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.model.usuario.CreateUsuarioInput

class AdminUsuariosViewModel(
    private val getUsuariosUseCase: GetUsuariosUseCase,
    private val createUsuarioUseCase: CreateUsuarioUseCase,
    private val deleteUsuarioUseCase: DeleteUsuarioUseCase
) : BaseViewModel<AdminUsuariosState, AdminUsuariosIntent, AdminUsuariosEffect>(AdminUsuariosState()) {

    init { load() }

    override fun process(intent: AdminUsuariosIntent) {
        when (intent) {
            is AdminUsuariosIntent.Load           -> load()
            is AdminUsuariosIntent.BusquedaChanged -> updateState { copy(busqueda = intent.value) }
            is AdminUsuariosIntent.FiltroChanged   -> updateState { copy(filtro = intent.filtro) }
            AdminUsuariosIntent.OpenCreateDialog   -> updateState { copy(showCreateDialog = true, createError = null) }
            AdminUsuariosIntent.CloseCreateDialog  -> updateState { copy(showCreateDialog = false, createError = null) }
            is AdminUsuariosIntent.CreateRolChanged -> updateState { copy(createRolId = intent.rolId) }
            is AdminUsuariosIntent.CreateNombresChanged -> updateState { copy(createNombres = intent.value) }
            is AdminUsuariosIntent.CreateApellidosChanged -> updateState { copy(createApellidos = intent.value) }
            is AdminUsuariosIntent.CreateCorreoChanged -> updateState { copy(createCorreo = intent.value) }
            is AdminUsuariosIntent.CreatePasswordChanged -> updateState { copy(createPassword = intent.value) }
            is AdminUsuariosIntent.CreateCodigoUpnChanged -> updateState { copy(createCodigoUpn = intent.value) }
            is AdminUsuariosIntent.CreateFacultadChanged -> updateState { copy(createFacultad = intent.value) }
            is AdminUsuariosIntent.CreateLicenciaChanged -> updateState { copy(createLicencia = intent.value) }
            is AdminUsuariosIntent.CreateTelefonoChanged -> updateState { copy(createTelefono = intent.value) }
            AdminUsuariosIntent.SubmitCreate      -> submitCreate()
            is AdminUsuariosIntent.DeleteRequested -> updateState { copy(deleteTargetId = intent.usuarioId) }
            AdminUsuariosIntent.CancelDelete      -> updateState { copy(deleteTargetId = null) }
            AdminUsuariosIntent.ConfirmDelete     -> confirmDelete()
        }
    }

    private fun load() {
        launch {
            updateState { copy(loading = true, error = null) }
            runCatching { getUsuariosUseCase() }
                .onSuccess { list -> updateState { copy(loading = false, usuarios = list) } }
                .onFailure { err ->
                    val msg = err.message ?: "Error cargando usuarios"
                    updateState { copy(loading = false, error = msg) }
                    emitEffect(AdminUsuariosEffect.ShowError(msg))
                }
        }
    }

    private fun submitCreate() {
        val current = state.value
        val errores = buildList {
            if (current.createNombres.isBlank()) add("nombres")
            if (current.createApellidos.isBlank()) add("apellidos")
            if (current.createCorreo.isBlank()) add("correo")
            if (current.createPassword.length < 6) add("password")
            if (current.createRolId == RolId.Estudiante && current.createCodigoUpn.isBlank()) add("codigo_upn")
            if (current.createRolId == RolId.Conductor && current.createLicencia.isBlank()) add("licencia")
        }
        if (errores.isNotEmpty()) {
            updateState { copy(createError = "Completa los campos obligatorios") }
            return
        }

        launch {
            updateState { copy(saving = true, createError = null) }
            runCatching {
                createUsuarioUseCase(
                    CreateUsuarioInput(
                        rolId = current.createRolId,
                        nombres = current.createNombres,
                        apellidos = current.createApellidos,
                        correo = current.createCorreo,
                        password = current.createPassword,
                        codigoUpn = current.createCodigoUpn,
                        facultad = current.createFacultad.ifBlank { null },
                        licencia = current.createLicencia,
                        telefono = current.createTelefono.ifBlank { null }
                    )
                )
            }.onSuccess {
                updateState {
                    copy(
                        saving = false,
                        showCreateDialog = false,
                        createRolId = RolId.Estudiante,
                        createNombres = "",
                        createApellidos = "",
                        createCorreo = "",
                        createPassword = "",
                        createCodigoUpn = "",
                        createFacultad = "",
                        createLicencia = "",
                        createTelefono = ""
                    )
                }
                emitEffect(AdminUsuariosEffect.ShowMessage("Usuario creado"))
                load()
            }.onFailure { err ->
                val msg = err.message ?: "Error creando usuario"
                updateState { copy(saving = false, createError = msg) }
                emitEffect(AdminUsuariosEffect.ShowError(msg))
            }
        }
    }

    private fun confirmDelete() {
        val usuarioId = state.value.deleteTargetId ?: return
        launch {
            updateState { copy(deleting = true) }
            runCatching { deleteUsuarioUseCase(usuarioId) }
                .onSuccess {
                    updateState { copy(deleting = false, deleteTargetId = null) }
                    emitEffect(AdminUsuariosEffect.ShowMessage("Usuario desactivado"))
                    load()
                }
                .onFailure { err ->
                    val msg = err.message ?: "Error eliminando usuario"
                    updateState { copy(deleting = false) }
                    emitEffect(AdminUsuariosEffect.ShowError(msg))
                }
        }
    }
}
