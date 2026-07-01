package com.helkore.rutas.android.ui.feature.perfil

import android.graphics.Bitmap
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.domain.model.usuario.Estudiante
import org.koin.androidx.compose.koinViewModel

@Composable
fun MiCodigoScreen(
    onBack   : () -> Unit,
    viewModel: MiCodigoViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Fondo siempre morado, igual en light y dark (como el Figma)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GoPrimary)
            .statusBarsPadding()
    ) {
        // ── Top bar — texto blanco siempre (sobre fondo morado) ───────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint               = Color.White
                )
            }
            Text(
                text     = "Mi Código",
                style    = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color    = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // ── Card — ocupa todo el espacio restante con padding lateral ────
        Box(
            modifier         = Modifier
                .fillMaxSize()
                .padding(top = 56.dp, start = 20.dp, end = 20.dp, bottom = 28.dp),
            contentAlignment = Alignment.Center
        ) {
            when {
                state.loading -> CircularProgressIndicator(
                    color    = Color.White,
                    modifier = Modifier.size(36.dp)
                )

                state.estudiante != null -> CodigoCard(
                    estudiante     = state.estudiante!!,
                    nombreCompleto = state.nombreCompleto,
                    correo         = state.correo,
                    universidad    = state.universidad
                )

                state.error != null -> Text(
                    text      = state.error!!,
                    color     = Color.White.copy(alpha = 0.8f),
                    style     = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier  = Modifier.padding(24.dp)
                )
            }
        }
    }
}

@Composable
private fun CodigoCard(
    estudiante    : Estudiante,
    nombreCompleto: String,
    correo        : String,
    universidad   : String
) {
    val isDark = isSystemInDarkTheme()
    val codigo = estudiante.codigoAcceso.ifBlank { estudiante.codigoUpn }.ifBlank { "UPN-STUDENT" }

    // Light: barras negras sobre blanco. Dark: barras blancas sobre negro.
    val barcodeBitmap = remember(codigo, isDark) { generateBarcode(codigo, lightBackground = !isDark) }

    // Card blanca en light, muy oscura en dark (como el Figma)
    val cardBg   = if (isDark) Color(0xFF0D0D0D) else Color.White
    val textMain = if (isDark) Color.White       else Color(0xFF0D0B1A)
    val textSub  = if (isDark) Color(0xFF9CA3AF) else Color(0xFF6B7280)
    // Badge ESTUDIANTE: fondo oscuro semitransparente en light, más claro en dark
    val badgeBg  = if (isDark) Color(0xFF1E1E2B) else Color(0xFFE8E8EE)
    val badgeTxt = if (isDark) Color(0xFFD1D5DB) else Color(0xFF374151)

    // La card llena todo el espacio disponible (fillMaxSize heredado del padre)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(24.dp))
            .background(cardBg),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier            = Modifier.padding(horizontal = 28.dp, vertical = 48.dp)
        ) {
            // ── Nombre ────────────────────────────────────────────────────
            Text(
                text      = nombreCompleto,
                style     = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                color     = textMain,
                textAlign = TextAlign.Center
            )

            // ── Correo ────────────────────────────────────────────────────
            Text(
                text      = correo,
                style     = MaterialTheme.typography.bodyMedium,
                color     = textSub,
                textAlign = TextAlign.Center
            )

            // ── Badge ESTUDIANTE ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(badgeBg)
                    .padding(horizontal = 14.dp, vertical = 5.dp)
            ) {
                Text(
                    text  = "ESTUDIANTE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight    = FontWeight.ExtraBold,
                        letterSpacing = 1.2.sp
                    ),
                    color = badgeTxt
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // ── Barcode ───────────────────────────────────────────────────
            if (barcodeBitmap != null) {
                Image(
                    bitmap             = barcodeBitmap.asImageBitmap(),
                    contentDescription = "Código de barras",
                    contentScale       = ContentScale.FillWidth,
                    modifier           = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            } else {
                Text(
                    "||||||||||||||||||||||||",
                    style = MaterialTheme.typography.headlineSmall,
                    color = textMain
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Instrucción ───────────────────────────────────────────────
            Text(
                text      = "Muestra este código al conductor al subir",
                style     = MaterialTheme.typography.bodySmall,
                color     = textSub,
                textAlign = TextAlign.Center
            )

            // ── Universidad ───────────────────────────────────────────────
            Text(
                text      = "${universidad.uppercase()} – Universidad Privada del Norte",
                style     = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                color     = textSub,
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun generateBarcode(text: String, lightBackground: Boolean): Bitmap? {
    return try {
        val hints = mapOf(EncodeHintType.MARGIN to 0)
        val matrix = MultiFormatWriter().encode(text, BarcodeFormat.CODE_128, 600, 150, hints)
        val w = matrix.width
        val h = matrix.height
        val pixels = IntArray(w * h) { i ->
            val x = i % w; val y = i / w
            if (matrix[x, y])
                if (lightBackground) AndroidColor.BLACK else AndroidColor.WHITE
            else
                if (lightBackground) AndroidColor.WHITE else AndroidColor.BLACK
        }
        Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888).also { it.setPixels(pixels, 0, w, 0, 0, w, h) }
    } catch (e: Exception) { null }
}
