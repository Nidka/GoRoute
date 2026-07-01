package com.helkore.rutas.android.ui.feature.admin.rutas

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Route
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.core.component.parseColorHex
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.domain.model.ruta.Ruta
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminRutasScreen(
    viewModel: AdminRutasViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()
    val cardBorder = if (isDark) Color.Transparent else Color(0xFFE5E7EB)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AdminRutasEffect.ShowError   -> snackbarHostState.showSnackbar(effect.message)
                is AdminRutasEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { viewModel.process(AdminRutasIntent.OpenCreate) },
                containerColor = GoPrimary,
                contentColor   = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva ruta") }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .statusBarsPadding()
        ) {
            // ── Header ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoPrimary.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Route,
                        contentDescription = null,
                        tint     = GoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "RUTAS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight    = FontWeight.ExtraBold,
                            color         = GoPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        "Gestión de rutas",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                if (!state.loading && state.rutas.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GoPrimary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "${state.rutas.size} rutas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                        )
                    }
                }
            }

            // ── Content ──────────────────────────────────────────────────────
            when {
                state.loading -> Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = GoPrimary)
                }

                state.error != null -> Box(
                    modifier         = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error!!, color = GoError)
                }

                else -> LazyColumn(
                    modifier      = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.rutas, key = { it.id }) { ruta ->
                        RutaCard(
                            ruta       = ruta,
                            cardBorder = cardBorder,
                            onEdit     = { viewModel.process(AdminRutasIntent.OpenEdit(ruta)) },
                            onParaderos = { viewModel.process(AdminRutasIntent.OpenParaderos(ruta)) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        // ── Dialogs ──────────────────────────────────────────────────────────
        if (state.showParaderoDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.process(AdminRutasIntent.CloseParaderoDialog) },
                title = { Text("Paraderos · ${state.selectedRutaNombre}") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value         = state.paraderoNombre,
                            onValueChange = { viewModel.process(AdminRutasIntent.ParaderoNombreChanged(it)) },
                            label         = { Text("Nombre") },
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = state.paraderoDescripcion,
                            onValueChange = { viewModel.process(AdminRutasIntent.ParaderoDescripcionChanged(it)) },
                            label         = { Text("Descripción") },
                            singleLine    = true
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value         = state.paraderoLat,
                                onValueChange = { viewModel.process(AdminRutasIntent.ParaderoLatChanged(it)) },
                                label         = { Text("Lat") },
                                modifier      = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine    = true
                            )
                            OutlinedTextField(
                                value         = state.paraderoLng,
                                onValueChange = { viewModel.process(AdminRutasIntent.ParaderoLngChanged(it)) },
                                label         = { Text("Lng") },
                                modifier      = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                singleLine    = true
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked         = state.paraderoTerminal,
                                onCheckedChange = { viewModel.process(AdminRutasIntent.ParaderoTerminalChanged(it)) }
                            )
                            Text("Es terminal", color = MaterialTheme.colorScheme.onBackground)
                        }
                        if (state.paraderoDialogError != null) {
                            Text(state.paraderoDialogError!!, color = GoError)
                        }
                        Divider(color = MaterialTheme.colorScheme.outlineVariant)
                        Text(
                            "Paraderos existentes",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall
                        )
                        if (state.paraderosLoading) {
                            CircularProgressIndicator(color = GoPrimary)
                        } else if (state.paraderosError != null) {
                            Text(state.paraderosError!!, color = GoError)
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                state.paraderos.take(6).forEach { paradero ->
                                    Button(
                                        onClick  = { viewModel.process(AdminRutasIntent.AttachParadero(paradero)) },
                                        enabled  = !state.paraderoSaving
                                    ) { Text(paradero.nombre) }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick  = { viewModel.process(AdminRutasIntent.SubmitParadero) },
                        enabled  = !state.paraderoSaving
                    ) { Text(if (state.paraderoSaving) "Guardando..." else "Crear y agregar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.process(AdminRutasIntent.CloseParaderoDialog) }) {
                        Text("Cerrar")
                    }
                }
            )
        }

        if (state.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.process(AdminRutasIntent.CloseDialog) },
                title = { Text(if (state.editingId == null) "Nueva ruta" else "Editar ruta") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value         = state.nombre,
                            onValueChange = { viewModel.process(AdminRutasIntent.NombreChanged(it)) },
                            label         = { Text("Nombre") },
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = state.colorHex,
                            onValueChange = { viewModel.process(AdminRutasIntent.ColorChanged(it)) },
                            label         = { Text("Color hex") },
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = state.margenM,
                            onValueChange = { viewModel.process(AdminRutasIntent.MargenChanged(it)) },
                            label         = { Text("Margen (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine    = true
                        )
                        if (state.dialogError != null) {
                            Text(state.dialogError!!, color = GoError)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick  = { viewModel.process(AdminRutasIntent.Submit) },
                        enabled  = !state.saving
                    ) { Text(if (state.saving) "Guardando..." else "Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.process(AdminRutasIntent.CloseDialog) }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// ── Ruta Card ─────────────────────────────────────────────────────────────────

@Composable
private fun RutaCard(
    ruta: Ruta,
    cardBorder: Color,
    onEdit: () -> Unit,
    onParaderos: () -> Unit
) {
    val rutaColor = parseColorHex(ruta.colorHex, GoPrimary)
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
    ) {
        // ── Franja superior de color ──────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(rutaColor)
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ícono círculo con color de ruta
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(rutaColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Route,
                        contentDescription = null,
                        tint     = rutaColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        ruta.nombre,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.onBackground
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Badge activa/inactiva
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (ruta.activa) MaterialTheme.colorScheme.secondaryContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                )
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                if (ruta.activa) "ACTIVA" else "INACTIVA",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (ruta.activa) MaterialTheme.colorScheme.onSecondaryContainer
                                            else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                        // Badge paraderos
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(rutaColor.copy(alpha = 0.12f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                "${ruta.paraderos.size} paradas",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color      = rutaColor
                                )
                            )
                        }
                    }
                }

                // Botones acción
                IconButton(onClick = onParaderos) {
                    Icon(Icons.Default.Place, contentDescription = "Paraderos", tint = GoPrimary)
                }
                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = GoPrimary)
                }
            }

            // ── Meta info ──────────────────────────────────────────────────
            Spacer(Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                MetaChip(label = "Margen", value = "${ruta.margenM} m")
                MetaChip(label = "ID", value = "#${ruta.id}")
            }

            // ── Lista de paraderos colapsable ──────────────────────────────
            if (ruta.paraderos.isNotEmpty()) {
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { expanded = !expanded }
                        .padding(vertical = 6.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "Ver paraderos",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color      = GoPrimary
                        )
                    )
                    Icon(
                        if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint     = GoPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                AnimatedVisibility(
                    visible = expanded,
                    enter   = expandVertically(),
                    exit    = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ruta.paraderos.sortedBy { it.orden }.forEach { rp ->
                            val nombre = rp.paradero?.nombre ?: "Paradero #${rp.paraderoId}"
                            val esTerminal = rp.paradero?.esTerminal == true
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(rutaColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${rp.orden}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            color      = Color.White,
                                            fontSize   = 10.sp
                                        )
                                    )
                                }
                                Text(
                                    nombre,
                                    style  = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color      = MaterialTheme.colorScheme.onBackground
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                                if (esTerminal) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(rutaColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "TERMINAL",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color      = rutaColor,
                                                fontSize   = 9.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetaChip(label: String, value: String) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
        Text(
            value,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground
            )
        )
    }
}
