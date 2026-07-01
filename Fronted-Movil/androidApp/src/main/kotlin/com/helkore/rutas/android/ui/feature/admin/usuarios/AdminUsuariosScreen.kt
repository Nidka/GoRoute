package com.helkore.rutas.android.ui.feature.admin.usuarios

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.domain.model.auth.RolId
import com.helkore.rutas.domain.model.usuario.Usuario
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUsuariosScreen(
    viewModel: AdminUsuariosViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val effect = viewModel.effect
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()
    val cardBorder = if (isDark) Color.Transparent else Color(0xFFE5E7EB)

    LaunchedEffect(Unit) {
        effect.collect { uiEffect ->
            when (uiEffect) {
                is AdminUsuariosEffect.ShowError -> snackbarHostState.showSnackbar(uiEffect.message)
                is AdminUsuariosEffect.ShowMessage -> snackbarHostState.showSnackbar(uiEffect.message)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { viewModel.process(AdminUsuariosIntent.OpenCreateDialog) },
                containerColor = GoPrimary,
                contentColor   = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo usuario")
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                // ── Header ──────────────────────────────────────────────────
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text     = "Usuarios",
                        style    = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                // ── Search ──────────────────────────────────────────────────
                OutlinedTextField(
                    value         = state.busqueda,
                    onValueChange = { viewModel.process(AdminUsuariosIntent.BusquedaChanged(it)) },
                    modifier      = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    placeholder   = { Text("Buscar por nombre o correo…", color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)) },
                    leadingIcon   = { Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    singleLine    = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.Search),
                    shape         = RoundedCornerShape(14.dp),
                    colors        = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor      = GoPrimary,
                        unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                        focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                        cursorColor             = GoPrimary
                    )
                )

                Spacer(Modifier.height(12.dp))

                // ── Filter chips ────────────────────────────────────────────
                LazyRow(
                    contentPadding        = PaddingValues(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(UsuarioFiltro.entries) { filtro ->
                        val selected = filtro == state.filtro
                        FilterChip(
                            selected = selected,
                            onClick  = { viewModel.process(AdminUsuariosIntent.FiltroChanged(filtro)) },
                            label    = { Text(filtro.label) },
                            colors   = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = GoPrimary.copy(alpha = 0.18f),
                                selectedLabelColor     = GoPrimaryLight,
                                containerColor         = MaterialTheme.colorScheme.surface,
                                labelColor             = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            border   = FilterChipDefaults.filterChipBorder(
                                enabled             = true,
                                selected            = selected,
                                selectedBorderColor = GoPrimary,
                                borderColor         = MaterialTheme.colorScheme.outline
                            )
                        )
                    }
                }

                Spacer(Modifier.height(8.dp))

                // ── List ────────────────────────────────────────────────────
                when {
                    state.loading -> Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(32.dp))
                    }

                    state.error != null -> Box(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = state.error!!,
                            style = MaterialTheme.typography.bodyMedium,
                            color = GoError
                        )
                    }

                    else -> LazyColumn(
                        modifier            = Modifier.weight(1f),
                        contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(state.usuariosFiltrados, key = { it.id }) { usuario ->
                            UsuarioCard(
                                usuario  = usuario,
                                cardBorder = cardBorder,
                                onDelete = { viewModel.process(AdminUsuariosIntent.DeleteRequested(usuario.id)) }
                            )
                        }
                        if (state.usuariosFiltrados.isEmpty()) {
                            item {
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(200.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "Sin resultados",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (state.showCreateDialog) {
                CreateUserDialog(
                    state    = state,
                    onIntent = viewModel::process
                )
            }

            if (state.deleteTargetId != null) {
                AlertDialog(
                    onDismissRequest = { viewModel.process(AdminUsuariosIntent.CancelDelete) },
                    title = { Text("Desactivar usuario") },
                    text  = { Text("¿Seguro que quieres desactivar este usuario?") },
                    confirmButton = {
                        TextButton(
                            onClick = { viewModel.process(AdminUsuariosIntent.ConfirmDelete) },
                            enabled = !state.deleting
                        ) { Text(if (state.deleting) "Eliminando..." else "Desactivar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { viewModel.process(AdminUsuariosIntent.CancelDelete) }) {
                            Text("Cancelar")
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun CreateUserDialog(
    state: AdminUsuariosState,
    onIntent: (AdminUsuariosIntent) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onIntent(AdminUsuariosIntent.CloseCreateDialog) },
        title = { Text("Nuevo usuario") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UsuarioFiltro.entries.take(3).forEachIndexed { index, _ ->
                        val rol = when (index) {
                            0    -> RolId.Estudiante
                            1    -> RolId.Conductor
                            else -> RolId.Administrador
                        }
                        Button(onClick = { onIntent(AdminUsuariosIntent.CreateRolChanged(rol)) }) {
                            Text(rol.name.lowercase().replaceFirstChar { it.uppercase() })
                        }
                    }
                }
                OutlinedTextField(
                    value = state.createNombres,
                    onValueChange = { onIntent(AdminUsuariosIntent.CreateNombresChanged(it)) },
                    label = { Text("Nombres") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.createApellidos,
                    onValueChange = { onIntent(AdminUsuariosIntent.CreateApellidosChanged(it)) },
                    label = { Text("Apellidos") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.createCorreo,
                    onValueChange = { onIntent(AdminUsuariosIntent.CreateCorreoChanged(it)) },
                    label = { Text("Correo") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = state.createPassword,
                    onValueChange = { onIntent(AdminUsuariosIntent.CreatePasswordChanged(it)) },
                    label = { Text("Contraseña") },
                    singleLine = true
                )
                if (state.createRolId == RolId.Estudiante) {
                    OutlinedTextField(
                        value = state.createCodigoUpn,
                        onValueChange = { onIntent(AdminUsuariosIntent.CreateCodigoUpnChanged(it)) },
                        label = { Text("Código UPN") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.createFacultad,
                        onValueChange = { onIntent(AdminUsuariosIntent.CreateFacultadChanged(it)) },
                        label = { Text("Facultad") },
                        singleLine = true
                    )
                }
                if (state.createRolId == RolId.Conductor) {
                    OutlinedTextField(
                        value = state.createLicencia,
                        onValueChange = { onIntent(AdminUsuariosIntent.CreateLicenciaChanged(it)) },
                        label = { Text("Licencia") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = state.createTelefono,
                        onValueChange = { onIntent(AdminUsuariosIntent.CreateTelefonoChanged(it)) },
                        label = { Text("Teléfono") },
                        singleLine = true
                    )
                }
                if (state.createError != null) {
                    Text(text = state.createError, color = GoError)
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onIntent(AdminUsuariosIntent.SubmitCreate) },
                enabled = !state.saving
            ) { Text(if (state.saving) "Guardando..." else "Crear") }
        },
        dismissButton = {
            TextButton(onClick = { onIntent(AdminUsuariosIntent.CloseCreateDialog) }) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
private fun UsuarioCard(
    usuario: Usuario,
    cardBorder: Color,
    onDelete: () -> Unit
) {
    val initials = buildString {
        usuario.nombres.firstOrNull()?.let { append(it.uppercaseChar()) }
        usuario.apellidos.firstOrNull()?.let { append(it.uppercaseChar()) }
    }.ifBlank { "?" }

    val rolLabel = when (usuario.rolId) {
        RolId.Estudiante    -> "ESTUDIANTE"
        RolId.Conductor     -> "CONDUCTOR"
        RolId.Administrador -> "ADMIN"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(GoPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = initials,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = usuario.nombreCompleto,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text     = usuario.correo,
                style    = MaterialTheme.typography.bodySmall,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoPrimary)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text  = rolLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = Color.White
                    )
                }
                val (activoColor, activoBg, activoText) = if (usuario.activo) {
                    Triple(MaterialTheme.colorScheme.onSecondaryContainer, MaterialTheme.colorScheme.secondaryContainer, "ACTIVO")
                } else {
                    Triple(MaterialTheme.colorScheme.onSurfaceVariant, MaterialTheme.colorScheme.surfaceVariant, "INACTIVO")
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(activoBg)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text  = activoText,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        ),
                        color = activoColor
                    )
                }
            }
        }

        IconButton(onClick = onDelete) {
            Icon(Icons.Default.Delete, contentDescription = "Desactivar usuario", tint = GoError)
        }
    }
}
