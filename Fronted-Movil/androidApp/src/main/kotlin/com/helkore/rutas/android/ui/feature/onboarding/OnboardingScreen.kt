package com.helkore.rutas.android.ui.feature.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoColors
import com.helkore.rutas.android.ui.theme.GoPrimary



@Composable
fun OnboardingScreen(onContinuar: (slug: String) -> Unit) {
    var slug by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // ── Logo ──────────────────────────────────────────────────────
            GoRuteLogo(size = 100.dp)

            Spacer(modifier = Modifier.height(28.dp))

            // ── Tagline ───────────────────────────────────────────────────
            Text(
                text = "TRANSPORTE UNIVERSITARIO",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // ── Hero "Tu bus, en vivo." ────────────────────────────────────
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.onBackground)) {
                        append("Tu bus, ")
                    }
                    withStyle(SpanStyle(color = GoColors.logoGradientStart)) {
                        append("en vivo.")
                    }
                },
                style = MaterialTheme.typography.displaySmall.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 34.sp,
                    lineHeight = 40.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Rastrea rutas, paraderos y\ntiempo de llegada en tiempo real.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(52.dp))

            // ── Campo ─────────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "CÓDIGO DE UNIVERSIDAD",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.5.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = slug,
                    onValueChange = { slug = it.filter { c -> c.isLetter() }.take(10) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "upn",
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor      = GoPrimary,
                        unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
                        focusedTextColor        = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor      = MaterialTheme.colorScheme.onSurface,
                        cursorColor             = GoPrimary
                    ),
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(
                        capitalization = KeyboardCapitalization.None,
                        imeAction      = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                            if (slug.isNotBlank()) onContinuar(slug.lowercase().trim())
                        }
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        focusManager.clearFocus()
                        if (slug.isNotBlank()) onContinuar(slug.lowercase().trim())
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = slug.isNotBlank(),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor         = GoPrimary,
                        disabledContainerColor = GoPrimary.copy(alpha = 0.35f)
                    )
                ) {
                    Text(
                        text = "Continuar",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = GoColors.onBackground
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "¿No tienes código? Consulta con tu institución.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

/**
 * Logo GO! RUTE — degradado morado→índigo, esquinas redondeadas, sombra morada.
 * Tipografía Black (900) igual al Figma.
 */
@Composable
fun GoRuteLogo(size: Dp = 88.dp) {
    val px            = size.value
    val cornerRadius  = size * 0.24f
    val goSize        = (px * 0.28f).sp
    val ruteSize      = (px * 0.155f).sp
    val goLineHeight  = (px * 0.30f).sp
    val ruteLineHeight = (px * 0.17f).sp

    Box(
        modifier = Modifier
            .size(size)
            .shadow(
                elevation    = 20.dp,
                shape        = RoundedCornerShape(cornerRadius),
                ambientColor = GoColors.logoGradientStart.copy(alpha = 0.45f),
                spotColor    = GoColors.logoGradientStart.copy(alpha = 0.6f)
            )
            .clip(RoundedCornerShape(cornerRadius))
            .background(
                Brush.linearGradient(
                    colorStops = arrayOf(
                        0f to GoColors.logoGradientStart,
                        1f to GoColors.logoGradientEnd
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text          = "GO!",
                color = GoColors.onBackground,
                fontSize      = goSize,
                fontWeight    = FontWeight.Black,
                lineHeight    = goSize,      // lineHeight = fontSize elimina espacio extra arriba/abajo
                letterSpacing = 0.sp,
                modifier      = Modifier.padding(0.dp)
            )
            Spacer(modifier = Modifier.height(1.dp))
            Text(
                text          = "RUTE",
                color         = GoColors.logoRuteText,
                fontSize      = ruteSize,
                fontWeight    = FontWeight.Black,
                letterSpacing = 1.5.sp,
                lineHeight    = ruteSize,   // lineHeight = fontSize elimina espacio extra
                modifier      = Modifier.padding(0.dp)
            )
        }
    }
}
