package com.helkore.rutas.android.ui.core.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoSuccess
import com.helkore.rutas.domain.model.ruta.Ruta

@Composable
fun RutaCard(
    ruta    : Ruta,
    onClick : () -> Unit,
    modifier: Modifier = Modifier,
    activeBusCount: Int = 0,
    etaMin: Int? = null
) {
    val routeColor = runCatching {
        Color(android.graphics.Color.parseColor(ruta.colorHex))
    }.getOrDefault(GoPrimary)

    val destino = ruta.paraderos.lastOrNull()?.paradero?.nombre ?: ""
    val subtext  = buildString {
        if (destino.isNotBlank()) append(destino)
        if (ruta.paraderos.isNotEmpty()) append(" · ${ruta.paraderos.size} paraderos")
    }.ifBlank { "${ruta.paraderos.size} paraderos" }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Ícono bus ─────────────────────────────────────────────────
            Box(
                modifier         = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(routeColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🚌", fontSize = 20.sp)
            }

            // ── Nombre + subtítulo ────────────────────────────────────────
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text  = ruta.nombre,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    // Badge cantidad buses
                    val (badgeColor, badgeBg, badgeText) = if (activeBusCount > 0)
                        Triple(GoSuccess, GoSuccess.copy(alpha = 0.12f), "$activeBusCount ${if (activeBusCount == 1) "BUS" else "BUSES"}")
                    else
                        Triple(
                            MaterialTheme.colorScheme.onSurfaceVariant,
                            MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.12f),
                            "0 BUSES"
                        )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(badgeBg)
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text  = badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight    = FontWeight.Bold,
                                fontSize      = 9.sp,
                                letterSpacing = 0.3.sp,
                                color         = badgeColor
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text  = subtext,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // ── Estado + ETA ──────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(if (activeBusCount > 0) GoSuccess else MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                    Text(
                        text  = if (activeBusCount > 0) "Activo" else "Sin bus",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                if (etaMin != null) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text  = "$etaMin",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color      = GoPrimary
                            )
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text     = "min",
                            style    = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Medium,
                                color      = GoPrimary
                            ),
                            modifier = Modifier.padding(bottom = 3.dp)
                        )
                    }
                } else {
                    Text(
                        text  = "---",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color      = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Icon(
                imageVector        = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier           = Modifier.size(18.dp)
            )
        }

    }
}
