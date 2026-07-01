package com.helkore.rutas.android.ui.feature.admin.flota

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.domain.model.flota.Unidad
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminFlotaScreen(
    viewModel: AdminFlotaViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val isDark = isSystemInDarkTheme()
    val cardBorder = if (isDark) Color.Transparent else Color(0xFFE5E7EB)

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AdminFlotaEffect.ShowError   -> snackbarHostState.showSnackbar(effect.message)
                is AdminFlotaEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        modifier       = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost   = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick        = { viewModel.process(AdminFlotaIntent.OpenCreate) },
                containerColor = GoPrimary,
                contentColor   = Color.White
            ) { Icon(Icons.Default.Add, contentDescription = "Nueva unidad") }
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
                        Icons.Default.DirectionsBus,
                        contentDescription = null,
                        tint     = GoPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "FLOTA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight    = FontWeight.ExtraBold,
                            color         = GoPrimary,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        "Gestión de unidades",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color      = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
                if (!state.loading && state.unidades.isNotEmpty()) {
                    val activas = state.unidades.count { it.activa }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(GoPrimary)
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Text(
                            "$activas activas",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                        )
                    }
                }
            }

            // ── Resumen chips ────────────────────────────────────────────────
            if (!state.loading && state.unidades.isNotEmpty()) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    val totalCap = state.unidades.sumOf { it.capacidad }
                    ResumenChip(
                        label    = "Total buses",
                        value    = "${state.unidades.size}",
                        modifier = Modifier.weight(1f)
                    )
                    ResumenChip(
                        label    = "Capacidad total",
                        value    = "$totalCap pasajeros",
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
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
                    modifier       = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.unidades, key = { it.id }) { unidad ->
                        UnidadCard(
                            unidad     = unidad,
                            cardBorder = cardBorder,
                            onEdit     = { viewModel.process(AdminFlotaIntent.OpenEdit(unidad)) }
                        )
                    }
                    item { Spacer(Modifier.height(80.dp)) }
                }
            }
        }

        if (state.showDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.process(AdminFlotaIntent.CloseDialog) },
                title = { Text(if (state.editingId == null) "Nueva unidad" else "Editar unidad") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value         = state.numero,
                            onValueChange = { viewModel.process(AdminFlotaIntent.NumeroChanged(it)) },
                            label         = { Text("Número") },
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = state.placa,
                            onValueChange = { viewModel.process(AdminFlotaIntent.PlacaChanged(it)) },
                            label         = { Text("Placa") },
                            singleLine    = true
                        )
                        OutlinedTextField(
                            value         = state.capacidad,
                            onValueChange = { viewModel.process(AdminFlotaIntent.CapacidadChanged(it)) },
                            label         = { Text("Capacidad") },
                            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine    = true
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Activa", modifier = Modifier.weight(1f))
                            Switch(
                                checked         = state.activa,
                                onCheckedChange = { viewModel.process(AdminFlotaIntent.ActivaChanged(it)) }
                            )
                        }
                        if (state.dialogError != null) {
                            Text(state.dialogError!!, color = GoError)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick  = { viewModel.process(AdminFlotaIntent.Submit) },
                        enabled  = !state.saving
                    ) { Text(if (state.saving) "Guardando..." else "Guardar") }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.process(AdminFlotaIntent.CloseDialog) }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

// ── Unidad Card ───────────────────────────────────────────────────────────────

@Composable
private fun UnidadCard(
    unidad: Unidad,
    cardBorder: Color,
    onEdit: () -> Unit
) {
    val activaColor = if (unidad.activa) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, cardBorder, RoundedCornerShape(18.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // ── Ícono de bus con estado activo/inactivo ───────────────────────
        Box(
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (unidad.activa) GoPrimary.copy(alpha = 0.12f)
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.DirectionsBus,
                contentDescription = null,
                tint     = if (unidad.activa) GoPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(26.dp)
            )
        }

        // ── Info ──────────────────────────────────────────────────────────
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    unidad.numero,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = MaterialTheme.colorScheme.onBackground
                    )
                )
                // Badge activa/inactiva
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            if (unidad.activa) MaterialTheme.colorScheme.secondaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        if (unidad.activa) "ACTIVA" else "INACTIVA",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (unidad.activa) MaterialTheme.colorScheme.onSecondaryContainer
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Placa
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Pin,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        unidad.placa,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
                // Capacidad
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        Icons.Default.Groups,
                        contentDescription = null,
                        tint     = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        "${unidad.capacidad} pasajeros",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(Modifier.height(6.dp))

            // Barra de capacidad visual
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.75f)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                val fill = (unidad.capacidad / 50f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth(fill)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (unidad.activa) GoPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(0.4f))
                )
            }
        }

        // ── Botón editar ──────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GoPrimary.copy(alpha = 0.10f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Editar",
                    tint     = GoPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

// ── Resumen chip ──────────────────────────────────────────────────────────────

@Composable
private fun ResumenChip(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, if (isSystemInDarkTheme()) Color.Transparent else Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            value,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color      = MaterialTheme.colorScheme.onBackground
            )
        )
        Text(
            label,
            style = MaterialTheme.typography.bodySmall.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}
