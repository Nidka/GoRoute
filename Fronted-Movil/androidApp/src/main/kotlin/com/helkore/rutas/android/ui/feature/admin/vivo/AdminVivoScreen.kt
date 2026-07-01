package com.helkore.rutas.android.ui.feature.admin.vivo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.core.component.MapMarker
import com.helkore.rutas.android.ui.core.component.OsmMap
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.android.ui.theme.GoWarning
import org.koin.androidx.compose.koinViewModel
import org.osmdroid.util.GeoPoint

@Composable
fun AdminVivoScreen(
    viewModel: AdminVivoViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    var sheetExpanded by remember { mutableStateOf(true) }

    val center = remember { GeoPoint(-8.1091, -79.0215) }

    val markers = remember(state.buses) {
        state.buses.mapIndexed { idx, bus ->
            MapMarker(
                position = GeoPoint(
                    center.latitude  + (idx - state.buses.size / 2) * 0.002,
                    center.longitude + (idx % 3 - 1) * 0.002
                ),
                title   = bus.busNumero,
                snippet = bus.rutaLabel,
                color   = when (bus.estado) {
                    BusEstado.Normal      -> GoSuccess
                    BusEstado.Advertencia -> GoWarning
                    BusEstado.Detenido    -> GoError
                }
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmMap(
            modifier = Modifier.fillMaxSize(),
            center   = center,
            zoom     = 14.0,
            routes   = emptyList(),
            markers  = markers
        )

        // ── Pill header ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopCenter)
                .padding(top = 12.dp)
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(GoSuccess)
                )
                Text(
                    text  = "Monitoreo",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        // ── Botón filtro ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 12.dp)
        ) {
            IconButton(
                onClick = { /* TODO: filter */ },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.92f))
            ) {
                Icon(Icons.Default.FilterList, contentDescription = "Filtrar", tint = GoPrimaryLight)
            }
        }

        // ── Bottom sheet ───────────────────────────────────────────────
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .navigationBarsPadding()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { sheetExpanded = !sheetExpanded }
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = "${state.buses.size} BUSES EN SERVICIO",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text  = "jornadas activas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector        = if (sheetExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(22.dp)
                )
            }

            AnimatedVisibility(
                visible = sheetExpanded,
                enter   = expandVertically(),
                exit    = shrinkVertically()
            ) {
                if (state.loading) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(160.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp))
                    }
                } else if (state.buses.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "No hay jornadas activas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier            = Modifier.fillMaxWidth().height(260.dp),
                        contentPadding      = androidx.compose.foundation.layout.PaddingValues(bottom = 12.dp)
                    ) {
                        items(state.buses) { bus ->
                            BusRow(bus)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BusRow(bus: BusEnVivo) {
    val dotColor = when (bus.estado) {
        BusEstado.Normal      -> GoSuccess
        BusEstado.Advertencia -> GoWarning
        BusEstado.Detenido    -> GoError
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(width = 4.dp, height = 40.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(GoPrimary)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = bus.busNumero,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text  = "${bus.rutaLabel} · ${bus.velocidad} km/h",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
    }
}
