package com.helkore.rutas.android.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val GoDarkColorScheme = darkColorScheme(
    primary              = GoPrimary,
    onPrimary            = GoOnPrimary,
    primaryContainer     = GoPrimaryContainer,        // 0xFF2D1B69 púrpura muy oscuro
    onPrimaryContainer   = OnPrimaryContainer,
    secondary            = GoSuccess,
    onSecondary          = OnSecondary,
    secondaryContainer   = GoSuccessContainer,        // 0xFF14532D verde oscuro
    onSecondaryContainer = OnSecondaryContainer,
    tertiary             = GoWarning,
    tertiaryContainer    = GoWarningContainer,        // 0xFF431407 naranja oscuro
    onTertiaryContainer  = OnTertiaryContainer,
    background           = GoBackground,              // 0xFF09090F casi negro
    onBackground         = GoOnBackground,
    surface              = GoSurface,                 // 0xFF13131C negro azulado
    onSurface            = GoOnSurface,
    surfaceVariant       = GoSurfaceVariant,          // 0xFF1E1E2B gris azul oscuro
    onSurfaceVariant     = GoOnSurfaceVariant,
    error                = GoError,
    errorContainer       = GoErrorContainer,
    onErrorContainer     = OnErrorContainer,
    outline              = GoSurfaceBorder            // 0xFF252533 borde sutil oscuro
)

private val GoLightColorScheme = lightColorScheme(
    primary              = GoPrimary,
    onPrimary            = GoOnPrimary,
    primaryContainer     = GoPrimaryContainerLight,   // 0xFFEDE9FF púrpura pastel
    onPrimaryContainer   = GoPrimaryDark,
    secondary            = GoSuccess,
    onSecondary          = GoOnPrimary,
    secondaryContainer   = Color(0xFFDCFCE7),         // verde menta claro
    onSecondaryContainer = Color(0xFF14532D),
    tertiary             = GoWarning,
    tertiaryContainer    = Color(0xFFFFEDD5),         // naranja pastel claro
    onTertiaryContainer  = Color(0xFF7C2D12),
    background           = GoBackgroundLight,         // 0xFFF4F4F8 gris lavanda
    onBackground         = GoOnBackgroundLight,
    surface              = GoSurfaceLight,            // 0xFFFFFFFF blanco
    onSurface            = GoOnSurfaceLight,
    surfaceVariant       = GoSurfaceVariantLight,     // 0xFFEEEEF6 gris azulado
    onSurfaceVariant     = GoOnSurfaceVariantLight2,
    error                = Color(0xFFDC2626),
    errorContainer       = Color(0xFFFFE4E4),
    onErrorContainer     = Color(0xFF7F1D1D),
    outline              = GoSurfaceBorderLight       // 0xFFE5E7EB gris neutro
)

private val GoShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small      = RoundedCornerShape(10.dp),
    medium     = RoundedCornerShape(14.dp),
    large      = RoundedCornerShape(20.dp),
    extraLarge = RoundedCornerShape(28.dp)
)

/** themeMode: null = seguir sistema, true = dark, false = light */
@Composable
fun RutasTheme(
    themeMode: Boolean? = null,
    content: @Composable () -> Unit
) {
    val systemDark = isSystemInDarkTheme()
    val useDark = themeMode ?: systemDark

    MaterialTheme(
        colorScheme = if (useDark) GoDarkColorScheme else GoLightColorScheme,
        typography  = RutasTypography,
        shapes      = GoShapes,
        content     = content
    )
}
