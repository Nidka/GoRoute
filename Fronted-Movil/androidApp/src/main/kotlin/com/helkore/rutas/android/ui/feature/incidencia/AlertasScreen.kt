package com.helkore.rutas.android.ui.feature.incidencia

import androidx.compose.foundation.background
import com.helkore.rutas.android.ui.theme.GoColors
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Traffic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoError
import com.helkore.rutas.android.ui.theme.GoErrorContainer
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import com.helkore.rutas.android.ui.theme.GoResolved
import com.helkore.rutas.android.ui.theme.GoResolvedContainer
import com.helkore.rutas.android.ui.theme.GoColors.onSurfaceVariant
import com.helkore.rutas.android.ui.theme.GoColors.surface

import com.helkore.rutas.android.ui.theme.GoWarning
import com.helkore.rutas.android.ui.theme.GoWarningContainer
import com.helkore.rutas.domain.model.incidencia.Incidencia
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AlertasScreen(
    viewModel: IncidenciaViewModel = koinViewModel { parametersOf(1) }
) {
    val state by viewModel.state.collectAsState()
    val activas = state.incidencias.filter { !it.resuelto }
    val resueltas = state.incidencias.filter { it.resuelto }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).statusBarsPadding()
    ) {
        // ── Header ────────────────────────────────────────────────────────
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 20.dp)) {
            Text("Alertas", style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold), color = MaterialTheme.colorScheme.onBackground)
            Text(
                text = if (activas.isEmpty()) "Sin novedades en tus rutas" else "${activas.size} alerta${if (activas.size > 1) "s" else ""} que pueden afectar tu viaje",
                style = MaterialTheme.typography.bodySmall,
                color = if (activas.isEmpty()) GoColors.onSurfaceVariant else GoWarning
            )
        }

        when {
            state.loading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = GoPrimary, modifier = Modifier.size(28.dp))
            }

            state.incidencias.isEmpty() -> SinAlertasEmpty()

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Activas primero
                if (activas.isNotEmpty()) {
                    item {
                        SectionLabel(text = "ACTIVAS · ${activas.size}", color = GoError)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(activas, key = { it.id }) { AlertaPasajeroCard(it) }
                    item { Spacer(modifier = Modifier.height(8.dp)) }
                }

                // Resueltas al final
                if (resueltas.isNotEmpty()) {
                    item {
                        SectionLabel(text = "RESUELTAS", color = GoColors.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    items(resueltas, key = { it.id }) { AlertaPasajeroCard(it) }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun SinAlertasEmpty() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(
                modifier         = Modifier.size(72.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surface),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.NotificationsNone, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(36.dp))
            }
            Text("Todo en orden", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onBackground)
            Text("No hay incidencias activas\nen tus rutas habituales", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionLabel(text: String, color: Color) {
    Text(text, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = color, letterSpacing = 1.sp))
}

// ── Card de alerta desde perspectiva del pasajero ─────────────────────────────

@Composable
private fun AlertaPasajeroCard(incidencia: Incidencia) {
    val resuelta = incidencia.resuelto
    val (icon, iconColor, iconBg, impacto) = incidenciaData(incidencia.tipoId)

    val label = incidencia.tipoNombre.ifBlank {
        when (incidencia.tipoId) { 1 -> "Tráfico pesado" 2 -> "Accidente en ruta" 3 -> "Falla mecánica" 4 -> "Bus lleno" else -> "Incidencia" }
    }

    val tiempoRelativo = remember(incidencia.registradoEn) {
        val diffMin = (System.currentTimeMillis() - incidencia.registradoEn * 1000) / 60_000
        when {
            diffMin < 1    -> "Ahora mismo"
            diffMin < 60   -> "Hace $diffMin min"
            diffMin < 1440 -> "Hace ${diffMin / 60} h"
            else           -> SimpleDateFormat("dd/MM", Locale.getDefault()).format(Date(incidencia.registradoEn * 1000))
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = if (resuelta) 0.6f else 1f))
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(46.dp).clip(CircleShape)
                .background(if (resuelta) GoResolvedContainer else iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (resuelta) Icons.Default.CheckCircle else icon,
                contentDescription = null,
                tint = if (resuelta) GoResolved else iconColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = label,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                color = if (resuelta) MaterialTheme.colorScheme.onSurfaceVariant
                        else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            if (!resuelta && impacto != null) {
                Text(impacto, style = MaterialTheme.typography.bodySmall, color = GoWarning)
                Spacer(modifier = Modifier.height(1.dp))
            }
            Text(tiempoRelativo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Box(
            modifier = Modifier.clip(RoundedCornerShape(50))
                .background(if (resuelta) GoResolvedContainer else GoErrorContainer)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = if (resuelta) "RESUELTA" else "ACTIVA",
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.3.sp),
                color = if (resuelta) GoResolved else GoError
            )
        }
    }
}

private data class IncidenciaData(val icon: ImageVector, val color: Color, val bg: Color, val impacto: String?)

@Composable
private fun incidenciaData(tipoId: Int): IncidenciaData = when (tipoId) {
    1 -> IncidenciaData(Icons.Default.Traffic,       GoWarning,     GoWarningContainer,                    "+5 min estimado en tu recorrido")
    2 -> IncidenciaData(Icons.Default.Warning,        GoError,       GoErrorContainer,                      "Posible demora significativa")
    3 -> IncidenciaData(Icons.Default.Build,          GoPrimaryLight, GoPrimary.copy(alpha = 0.18f),        "Bus puede cambiar de ruta")
    4 -> IncidenciaData(Icons.Default.DirectionsBus,  GoColors.onSurfaceVariant, GoColors.surface,                        "Espera el siguiente bus")
    else -> IncidenciaData(Icons.Default.Warning,     GoColors.onSurfaceVariant, GoColors.surface,                        null)
}
