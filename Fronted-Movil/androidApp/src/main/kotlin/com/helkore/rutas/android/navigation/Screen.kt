package com.helkore.rutas.android.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Login       : Screen("login/{slug}") {
        fun createRoute(slug: String) = "login/$slug"
    }
    object Main         : Screen("main")           // estudiante: Mapa|Rutas|Alertas|Perfil
    object ConductorMain: Screen("conductor_main") // conductor: Inicio|Jornada|Historial|Perfil
    object AdminMain    : Screen("admin_main")     // admin: Panel|Vivo|Usuarios|Perfil
    object RutaDetail  : Screen("rutas/{rutaId}") {
        fun createRoute(rutaId: Int) = "rutas/$rutaId"
    }
    object Mapa        : Screen("mapa/{jornadaId}") {
        fun createRoute(jornadaId: Int) = "mapa/$jornadaId"
    }
    object JornadaDashboard : Screen("jornada")
    object Incidencia  : Screen("incidencia/{jornadaId}") {
        fun createRoute(jornadaId: Int) = "incidencia/$jornadaId"
    }
    // Mantenidos por compatibilidad interna
    object MiCodigo    : Screen("mi_codigo")
    object Register    : Screen("register/{slug}") {
        fun createRoute(slug: String) = "register/$slug"
    }
    object Escaner     : Screen("escaner/{jornadaId}/{rutaNombre}") {
        fun createRoute(jornadaId: Int, rutaNombre: String) = "escaner/$jornadaId/${rutaNombre.replace(" ", "_")}"
    }
}
