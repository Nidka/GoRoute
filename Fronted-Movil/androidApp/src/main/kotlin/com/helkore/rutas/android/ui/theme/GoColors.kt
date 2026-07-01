package com.helkore.rutas.android.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

/**
 * Punto único de acceso a los colores del sistema GoRute!
 * Siempre usar GoColors.X en lugar de colores hardcodeados o variables estáticas.
 * Los colores cambian automáticamente entre dark y light según el tema activo.
 */
object GoColors {

    // ── Fondo y superficies ───────────────────────────────────────────────────

    /** Fondo principal de cada pantalla */
    val background: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background

    /** Color de cards, contenedores elevados */
    val surface: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

    /** Color de superficie con mayor elevación (chips, dropdowns) */
    val surfaceVariant: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surfaceVariant

    /** Borde de campos, separadores, contornos */
    val outline: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

    // ── Textos sobre fondos ───────────────────────────────────────────────────

    /** Texto principal sobre background */
    val onBackground: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onBackground

    /** Texto principal sobre surface/cards */
    val onSurface: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

    /** Texto secundario, placeholders, subtítulos */
    val onSurfaceVariant: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant

    // ── Color de marca ────────────────────────────────────────────────────────

    /** Color primario de la marca (botones, activos, acentos) */
    val primary: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary

    /** Texto/íconos sobre superficies de color primario */
    val onPrimary: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimary

    /** Contenedor de badges y chips primarios */
    val primaryContainer: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer

    /** Texto sobre primaryContainer */
    val onPrimaryContainer: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onPrimaryContainer

    // ── Semánticos (constantes — no cambian con el tema) ──────────────────────

    /** Verde: estado activo, éxito, en servicio */
    val success: Color get() = GoSuccess

    /** Contenedor del verde (fondo de badges verdes) */
    val successContainer: Color get() = GoSuccessContainer

    /** Rojo: errores, alertas críticas, cerrar sesión */
    val error: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error

    /** Contenedor del rojo */
    val errorContainer: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.errorContainer

    /** Texto sobre errorContainer */
    val onErrorContainer: Color
        @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onErrorContainer

    /** Naranja: advertencias, incidencias de tráfico */
    val warning: Color get() = GoWarning

    /** Contenedor del naranja */
    val warningContainer: Color get() = GoWarningContainer

    /** Verde azulado: incidencias resueltas */
    val resolved: Color get() = GoResolved

    /** Contenedor del resuelto */
    val resolvedContainer: Color get() = GoResolvedContainer

    // ── Colores de logo / marca fijos ─────────────────────────────────────────

    /** Morado claro para acentos (texto "RUTE", GoPrimaryLight) */
    val primaryLight: Color get() = GoPrimaryLight

    /** Degradado logo: inicio */
    val logoGradientStart: Color get() = Color(0xFF6B5CF6)

    /** Degradado logo: fin */
    val logoGradientEnd: Color get() = Color(0xFF4F46E5)

    /** Color "RUTE" dentro del logo */
    val logoRuteText: Color get() = Color(0xFFC4B5FD)

    // ── Colores de acento azul (panel admin, estadísticas) ────────────────────

    /** Azul informativo: contadores neutros, botones secundarios */
    val blueAccent: Color get() = GoBlueAccent

    /** Contenedor azul: fondo de tarjetas informativas */
    val blueBg: Color get() = GoBlueBg
}
