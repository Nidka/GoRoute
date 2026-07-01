package com.helkore.rutas.android.ui.feature.historial

import androidx.compose.foundation.background
import com.helkore.rutas.android.ui.theme.GoColors
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoColors.background
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryContainer
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.android.ui.theme.GoSuccessContainer
import com.helkore.rutas.android.ui.theme.GoColors.surface
import com.helkore.rutas.android.ui.theme.GoColors.surfaceVariant
import com.helkore.rutas.domain.model.jornada.Jornada
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistorialScreen(
    viewModel: HistorialViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(GoColors.background)
            .statusBarsPadding()
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Text(
            text = "Historial",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = GoColors.onBackground
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)
        )

        // ── Filtros ───────────────────────────────────────────────────────
        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(HistorialFiltro.entries) { filtro ->
                FiltroChip(
                    label = when (filtro) {
                        HistorialFiltro.Todas       -> "Todas"
                        HistorialFiltro.Activas     -> "Activas"
                        HistorialFiltro.Finalizadas -> "Finalizadas"
                    },
                    selected = state.filtro == filtro,
                    onClick  = { viewModel.process(HistorialIntent.SetFiltro(filtro)) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Lista ─────────────────────────────────────────────────────────
        when {
            state.loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp))
            }

            state.jornadasFiltradas.isEmpty() -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Sin jornadas",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.jornadasFiltradas, key = { it.id }) { jornada ->
                    JornadaHistorialCard(jornada = jornada)
                }
                item { Spacer(modifier = Modifier.height(8.dp)) }
            }
        }
    }
}

@Composable
private fun FiltroChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) GoPrimary else GoColors.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                color = if (selected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        )
    }
}

@Composable
private fun JornadaHistorialCard(jornada: Jornada) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(GoColors.surface)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Color strip
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(44.dp)
                .clip(RoundedCornerShape(50))
                .background(GoPrimaryLight)
        )
        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Fecha
            Text(
                text = formatFecha(jornada.inicioEn),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Ruta #${jornada.rutaId}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = GoColors.onBackground
                )
            )
            Text(
                text = buildDuracion(jornada),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Badge activa / finalizada
        if (jornada.activa) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(GoSuccessContainer)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ACTIVA",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = GoSuccess
                    )
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(GoColors.surfaceVariant)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "FIN.",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }
        }
    }
}

private fun formatFecha(epochMs: Long): String {
    val now = System.currentTimeMillis()
    val dayMs = 86_400_000L
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    val startTime = sdf.format(Date(epochMs))
    return when {
        epochMs > now - dayMs     -> "Hoy, $startTime"
        epochMs > now - 2 * dayMs -> "Ayer, $startTime"
        else -> {
            val dateSdf = SimpleDateFormat("EEE d MMM", Locale("es"))
            "${dateSdf.format(Date(epochMs))}, $startTime"
        }
    }
}

private fun buildDuracion(jornada: Jornada): String {
    val fin = jornada.finEn ?: System.currentTimeMillis()
    val durMs = fin - jornada.inicioEn
    val totalMin = durMs / 60_000
    val h = totalMin / 60
    val m = totalMin % 60
    return if (jornada.activa) "En curso" else "${h}h ${m}m"
}
