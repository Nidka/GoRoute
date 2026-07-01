package com.helkore.rutas.android.ui.theme

import androidx.compose.ui.graphics.Color

// ── Paleta principal GO! RUTE ──────────────────────────────────────────────
val GoPrimary        = Color(0xFF5B4FCF)   // Púrpura vibrante (botones, activos)
val GoPrimaryLight   = Color(0xFFA78BFA)   // Púrpura claro (texto "RUTE", acentos)
val GoPrimaryDark    = Color(0xFF3D2FA8)   // Púrpura oscuro (press state)
val GoPrimaryContainer = Color(0xFF2D1B69) // Contenedor púrpura oscuro (badges dark)
val GoPrimaryContainerLight = Color(0xFFEDE9FF) // Contenedor púrpura claro (badges light)
val GoOnPrimary      = Color(0xFFFFFFFF)

// ── Fondos DARK ────────────────────────────────────────────────────────────
val GoBackground     = Color(0xFF09090F)
val GoSurface        = Color(0xFF13131C)
val GoSurfaceVariant = Color(0xFF1E1E2B)
val GoSurfaceBorder  = Color(0xFF252533)
val GoOnBackground   = Color(0xFFFFFFFF)
val GoOnSurface      = Color(0xFFFFFFFF)
val GoOnSurfaceVariant = Color(0xFF9CA3AF)

// ── Fondos LIGHT ───────────────────────────────────────────────────────────
val GoBackgroundLight        = Color(0xFFF4F4F8)   // gris lavanda muy sutil — da contraste a las cards
val GoSurfaceLight           = Color(0xFFFFFFFF)   // cards blancas sobre fondo gris
val GoSurfaceVariantLight    = Color(0xFFEEEEF6)   // superficie elevada (chips, dropdowns)
val GoSurfaceBorderLight     = Color(0xFFE5E7EB)   // borde gris neutro estándar
val GoOnBackgroundLight      = Color(0xFF0D0B1A)   // casi negro para texto principal
val GoOnSurfaceLight         = Color(0xFF0D0B1A)
val GoOnSurfaceVariantLight2 = Color(0xFF4B5563)   // gris medio — subtítulos, metadatos

// ── Semánticos ─────────────────────────────────────────────────────────────
val GoSuccess          = Color(0xFF22C55E)
val GoSuccessContainer = Color(0xFF14532D)
val GoError            = Color(0xFFEF4444)
val GoErrorContainer   = Color(0xFF450A0A)
val GoWarning          = Color(0xFFF97316)
val GoWarningContainer = Color(0xFF431407)
val GoResolved         = Color(0xFF10B981)
val GoResolvedContainer= Color(0xFF064E3B)

// ── Acento azul (admin panel, info stats) ─────────────────────────────────
val GoBlueAccent = Color(0xFF3B82F6)
val GoBlueBg     = Color(0xFF1E3A5F)

// ── Legacy aliases ─────────────────────────────────────────────────────────
val Primary              = GoPrimary
val PrimaryVariant       = GoPrimaryDark
val PrimaryContainer     = GoPrimaryContainer
val OnPrimaryContainer   = Color(0xFFD9BBFF)
val Secondary            = GoSuccess
val SecondaryVariant     = Color(0xFF16A34A)
val SecondaryContainer   = GoSuccessContainer
val OnSecondaryContainer = Color(0xFFBBF7D0)
val Tertiary             = GoWarning
val TertiaryContainer    = GoWarningContainer
val OnTertiaryContainer  = Color(0xFFFFDDBB)
// Legacy aliases corrected to point to light/dark variants appropriately
val BackgroundLight      = GoBackgroundLight
val SurfaceLight         = GoSurfaceLight
val OnPrimary            = GoOnPrimary
val OnSecondary          = Color(0xFF052E16)
val OnBackground         = GoOnBackgroundLight
val OnSurface            = GoOnSurfaceLight
val BackgroundDark       = GoBackground
val SurfaceDark          = GoSurface
val SurfaceVariantDark   = GoSurfaceVariant
val OnBackgroundDark     = GoOnBackground
val OnSurfaceDark        = GoOnSurface
val OnSurfaceVariantDark = GoOnSurfaceVariant
val Error                = GoError
val ErrorContainer       = GoErrorContainer
val OnErrorContainer     = Color(0xFFFFDAD6)
val Warning              = GoWarning
val Success              = GoSuccess
val BusLive              = GoSuccess
val BusLiveContainer     = GoSuccessContainer
val StatusActive         = GoSuccess
val StatusInactive       = GoOnSurfaceVariant
