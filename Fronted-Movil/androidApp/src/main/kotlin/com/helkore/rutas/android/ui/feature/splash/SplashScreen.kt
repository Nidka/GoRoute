package com.helkore.rutas.android.ui.feature.splash

import androidx.compose.animation.core.animateFloatAsState
import com.helkore.rutas.android.ui.theme.GoColors
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.helkore.rutas.android.ui.theme.GoBackground
import com.helkore.rutas.android.ui.theme.GoPrimary
import com.helkore.rutas.android.ui.theme.GoPrimaryLight
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    onNavigateToEstudianteHome: () -> Unit,
    onNavigateToConductorHome: () -> Unit,
    onNavigateToAdminHome: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: SplashViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()

    var alpha by remember { mutableFloatStateOf(0f) }
    val animAlpha by animateFloatAsState(
        targetValue   = alpha,
        animationSpec = tween(600),
        label         = "splash_alpha"
    )
    LaunchedEffect(Unit) { alpha = 1f }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is SplashEffect.NavigateToEstudianteHome -> onNavigateToEstudianteHome()
                is SplashEffect.NavigateToConductorHome  -> onNavigateToConductorHome()
                is SplashEffect.NavigateToAdminHome      -> onNavigateToAdminHome()
                is SplashEffect.NavigateToLogin          -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(GoColors.background),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.radialGradient(listOf(GoPrimary.copy(alpha = 0.3f), Color.Transparent)))
                    .background(GoColors.surface, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("GO!", color = GoColors.onBackground, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 24.sp)
                    Text("RUTE", color = GoPrimaryLight, fontSize = 14.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = buildAnnotatedString {
                    withStyle(SpanStyle(color = GoColors.onBackground, fontWeight = FontWeight.Bold, fontSize = 20.sp)) { append("GO! ") }
                    withStyle(SpanStyle(color = GoPrimaryLight, fontWeight = FontWeight.Bold, fontSize = 20.sp)) { append("RUTE") }
                },
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(modifier = Modifier.height(32.dp))
            CircularProgressIndicator(color = GoPrimary, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
        }
    }
}
