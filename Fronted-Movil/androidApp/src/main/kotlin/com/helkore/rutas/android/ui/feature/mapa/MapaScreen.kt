package com.helkore.rutas.android.ui.feature.mapa

import androidx.compose.foundation.background
import com.helkore.rutas.android.ui.theme.GoColors
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import com.helkore.rutas.android.ui.core.component.MapMarker
import com.helkore.rutas.android.ui.core.component.OsmMap
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.android.ui.theme.GoColors.surface
import com.helkore.rutas.android.ui.theme.GoColors.surfaceVariant
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

@Composable
fun MapaScreen(
    jornadaId: Int,
    onBack: () -> Unit,
    viewModel: MapaViewModel = koinViewModel { parametersOf(jornadaId) }
) {
    val state by viewModel.state.collectAsState()

    val initialCenter = remember { GeoPoint(-8.1091, -79.0215) }
    var mapViewRef by remember { mutableStateOf<MapView?>(null) }

    // Posición del bus como GeoPoint
    val busGeoPoint = state.telemetria?.let { GeoPoint(it.lat, it.lng) }

    // Animar cámara cuando llega nueva telemetría
    LaunchedEffect(busGeoPoint) {
        if (busGeoPoint != null) {
            mapViewRef?.controller?.animateTo(busGeoPoint)
        }
    }

    // Marcador del bus
    val markers = remember(busGeoPoint) {
        if (busGeoPoint != null) {
            listOf(
                MapMarker(
                    position = busGeoPoint,
                    title    = "Bus en vivo",
                    snippet  = state.telemetria?.let { "${it.velocidad.toInt()} km/h" } ?: "",
                    color    = GoPrimary
                )
            )
        } else emptyList()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Mapa osmdroid ─────────────────────────────────────────────────
        OsmMap(
            modifier  = Modifier.fillMaxSize(),
            center    = initialCenter,
            zoom      = 15.0,
            routes    = emptyList(),
            markers   = markers,
            onMapReady = { mv -> mapViewRef = mv }
        )

        // ── Back button ───────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .size(44.dp)
                .clip(CircleShape)
                .background(GoColors.surface.copy(alpha = 0.95f)),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = GoColors.onBackground
                )
            }
        }

        // ── Estado conexión (top-right) ───────────────────────────────────
        Row(
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .align(Alignment.TopEnd),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(GoColors.surface.copy(alpha = 0.95f))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment    = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (state.conectado) GoSuccess
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                    )
                    Text(
                        text  = if (state.conectado) "En vivo" else "Sin señal",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.conectado) GoSuccess
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }

        // ── Bottom card ────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(16.dp),
            shape     = MaterialTheme.shapes.extraLarge,
            colors    = CardDefaults.cardColors(containerColor = GoColors.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // Bus info row
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(GoPrimary.copy(alpha = 0.18f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Default.DirectionsBus,
                            contentDescription = null,
                            tint               = GoPrimaryLight,
                            modifier           = Modifier.size(26.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text  = "Bus en tiempo real",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GoColors.onBackground
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        val telemetria = state.telemetria
                        if (telemetria != null) {
                            Text(
                                text  = "Velocidad: ${telemetria.velocidad.toInt()} km/h",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        } else {
                            Text(
                                text  = if (state.conectado) "Esperando señal GPS…" else "Bus desconectado",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Icon(
                        imageVector        = Icons.Default.GpsFixed,
                        contentDescription = null,
                        tint               = if (state.conectado) GoSuccess else GoColors.surfaceVariant,
                        modifier           = Modifier.size(20.dp)
                    )
                }

                // ETAs
                if (state.etas.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text  = "Próximas paradas",
                        style = MaterialTheme.typography.titleSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    state.etas.take(3).forEach { eta ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment     = Alignment.CenterVertically
                        ) {
                            Text(
                                text     = "Paradero ${eta.paraderoId}",
                                style    = MaterialTheme.typography.bodySmall.copy(color = GoColors.onBackground),
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(GoPrimary.copy(alpha = 0.18f))
                                    .padding(horizontal = 10.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text  = "${eta.etaSegundos / 60}min",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color      = GoPrimaryLight,
                                        fontWeight = FontWeight.Bold
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
