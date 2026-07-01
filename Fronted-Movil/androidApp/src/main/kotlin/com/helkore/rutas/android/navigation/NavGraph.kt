package com.helkore.rutas.android.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.helkore.rutas.android.ui.feature.admin.AdminMainScreen
import com.helkore.rutas.android.ui.feature.auth.login.LoginScreen
import com.helkore.rutas.android.ui.feature.auth.register.RegisterScreen
import com.helkore.rutas.android.ui.feature.conductor.ConductorMainScreen
import com.helkore.rutas.android.ui.feature.incidencia.IncidenciaScreen
import com.helkore.rutas.android.ui.feature.main.MainScreen
import com.helkore.rutas.android.ui.feature.mapa.MapaScreen
import com.helkore.rutas.android.ui.feature.onboarding.OnboardingScreen
import com.helkore.rutas.android.ui.feature.ruta.detail.RutaDetailScreen
import com.helkore.rutas.android.ui.feature.perfil.MiCodigoScreen
import com.helkore.rutas.android.ui.feature.scanner.EscanerScreen
import com.helkore.rutas.android.ui.feature.splash.SplashScreen
import com.helkore.rutas.domain.model.auth.RolId

@Composable
fun RutasNavGraph(startDestination: String = Screen.Splash.route) {
    val nav = rememberNavController()

    NavHost(navController = nav, startDestination = startDestination) {

        // ── Splash ────────────────────────────────────────────────────────
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToEstudianteHome = {
                    nav.navigate(Screen.Main.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToConductorHome = {
                    nav.navigate(Screen.ConductorMain.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToAdminHome = {
                    nav.navigate(Screen.AdminMain.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    nav.navigate(Screen.Onboarding.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // ── Onboarding ────────────────────────────────────────────────────
        composable(Screen.Onboarding.route) {
            OnboardingScreen(
                onContinuar = { slug -> nav.navigate(Screen.Login.createRoute(slug)) }
            )
        }

        // ── Login ─────────────────────────────────────────────────────────
        composable(
            route = Screen.Login.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType; defaultValue = "" })
        ) { entry ->
            val slug = entry.arguments?.getString("slug") ?: ""
            LoginScreen(
                slug          = slug,
                onLoginSuccess = { rolId ->
                    val dest = when (rolId) {
                        RolId.Conductor.value     -> Screen.ConductorMain.route
                        RolId.Administrador.value -> Screen.AdminMain.route
                        else                      -> Screen.Main.route
                    }
                    nav.navigate(dest) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                onGoToRegister = { nav.navigate(Screen.Register.createRoute(slug)) },
                onBack         = { nav.popBackStack() }
            )
        }

        // ── Register ──────────────────────────────────────────────────────
        composable(
            route = Screen.Register.route,
            arguments = listOf(navArgument("slug") { type = NavType.StringType; defaultValue = "" })
        ) { entry ->
            val slug = entry.arguments?.getString("slug") ?: ""
            RegisterScreen(
                slug              = slug,
                onRegisterSuccess = { nav.popBackStack() },
                onBack            = { nav.popBackStack() }
            )
        }

        // ── Estudiante Main (Mapa|Rutas|Alertas|Perfil) ───────────────────
        composable(Screen.Main.route) {
            MainScreen(
                onRutaClick    = { rutaId -> nav.navigate(Screen.RutaDetail.createRoute(rutaId)) },
                onLogout       = {
                    nav.navigate(Screen.Onboarding.route) { popUpTo(0) { inclusive = true } }
                },
                onJornadaClick = { nav.navigate(Screen.ConductorMain.route) },
                onMiCodigo     = { nav.navigate(Screen.MiCodigo.route) }
            )
        }

        // ── Mi Código (estudiante) ─────────────────────────────────────────
        composable(Screen.MiCodigo.route) {
            MiCodigoScreen(onBack = { nav.popBackStack() })
        }

        // ── Conductor Main (Inicio|Jornada|Historial|Perfil) ─────────────
        composable(Screen.ConductorMain.route) {
            ConductorMainScreen(
                nombreConductor      = "",
                onLogout             = {
                    nav.navigate(Screen.Onboarding.route) { popUpTo(0) { inclusive = true } }
                },
                onReportarIncidencia = { jornadaId ->
                    nav.navigate(Screen.Incidencia.createRoute(jornadaId))
                },
                onEscanear = { jornadaId, rutaNombre ->
                    nav.navigate(Screen.Escaner.createRoute(jornadaId, rutaNombre))
                }
            )
        }

        // ── Escáner QR de estudiantes ─────────────────────────────────────
        composable(
            route = Screen.Escaner.route,
            arguments = listOf(
                navArgument("jornadaId")  { type = NavType.IntType },
                navArgument("rutaNombre") { type = NavType.StringType; defaultValue = "En servicio" }
            )
        ) { entry ->
            EscanerScreen(
                jornadaId  = entry.arguments!!.getInt("jornadaId"),
                rutaNombre = entry.arguments!!.getString("rutaNombre", "En servicio").replace("_", " "),
                onBack     = { nav.popBackStack() }
            )
        }

        // ── Admin Main (Panel|Vivo|Usuarios|Perfil) ───────────────────
        composable(Screen.AdminMain.route) {
            AdminMainScreen(
                onLogout = {
                    nav.navigate(Screen.Onboarding.route) { popUpTo(0) { inclusive = true } }
                }
            )
        }

        // ── Ruta detail ───────────────────────────────────────────────────
        composable(
            route = Screen.RutaDetail.route,
            arguments = listOf(navArgument("rutaId") { type = NavType.IntType })
        ) { entry ->
            RutaDetailScreen(
                rutaId    = entry.arguments!!.getInt("rutaId"),
                onBack    = { nav.popBackStack() },
                onVerMapa = { jornadaId -> nav.navigate(Screen.Mapa.createRoute(jornadaId)) }
            )
        }

        // ── Mapa jornada ──────────────────────────────────────────────────
        composable(
            route = Screen.Mapa.route,
            arguments = listOf(navArgument("jornadaId") { type = NavType.IntType })
        ) { entry ->
            MapaScreen(
                jornadaId = entry.arguments!!.getInt("jornadaId"),
                onBack    = { nav.popBackStack() }
            )
        }

        // ── Incidencia ────────────────────────────────────────────────────
        composable(
            route = Screen.Incidencia.route,
            arguments = listOf(navArgument("jornadaId") { type = NavType.IntType })
        ) { entry ->
            IncidenciaScreen(
                jornadaId = entry.arguments!!.getInt("jornadaId"),
                onBack    = { nav.popBackStack() }
            )
        }
    }
}
