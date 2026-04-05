package com.curso.penaltyapp.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

// ─── COLORS DE MARCA ──────────────────────────────────────────────────────────

val PenaltyGreen = Color(0xFF1DB954)         // Main brand color (football green)
val PenaltyGreenDark = Color(0xFF158A3C)     // Darker variant
val PenaltyGreenLight = Color(0xFF5EE89A)    // Lighter variant
val PenaltyRed = Color(0xFFE53935)           // Fines / danger
val PenaltyYellow = Color(0xFFFDD835)        // Yellow card / warnings
val PenaltyDark = Color(0xFF121212)          // Dark background
val PenaltyDarkSurface = Color(0xFF1E1E1E)  // Dark surface
val PenaltyGray = Color(0xFF9E9E9E)          // Muted text
val PenaltyCardDark = Color(0xFF2C2C2C)     // Cards in dark mode

// ─── ESQUEMES DE COLOR ────────────────────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary = PenaltyGreen,
    onPrimary = Color.White,
    primaryContainer = PenaltyGreenDark,
    onPrimaryContainer = Color.White,
    secondary = PenaltyYellow,
    onSecondary = PenaltyDark,
    error = PenaltyRed,
    background = PenaltyDark,
    onBackground = Color.White,
    surface = PenaltyDarkSurface,
    onSurface = Color.White,
    surfaceVariant = PenaltyCardDark,
    onSurfaceVariant = Color(0xFFCCCCCC),
    outline = Color(0xFF444444)
)

private val LightColorScheme = lightColorScheme(
    primary = PenaltyGreenDark,
    onPrimary = Color.White,
    primaryContainer = PenaltyGreenLight,
    onPrimaryContainer = Color(0xFF003918),
    secondary = Color(0xFFF9A825),
    onSecondary = Color.White,
    error = PenaltyRed,
    background = Color(0xFFF5F5F5),
    onBackground = Color(0xFF1C1C1C),
    surface = Color.White,
    onSurface = Color(0xFF1C1C1C),
    surfaceVariant = Color(0xFFEEEEEE),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFFCCCCCC)
)

// ─── TIPOGRAFIA ───────────────────────────────────────────────────────────────

val PenaltyTypography = Typography(
    headlineLarge = TextStyle(
        fontWeight = FontWeight.ExtraBold,
        fontSize = 28.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    headlineSmall = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp
    ),
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp
    ),
    titleMedium = TextStyle(
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp
    ),
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        letterSpacing = 0.5.sp
    )
)

// ─── COMPOSABLE DEL TEMA ──────────────────────────────────────────────────────

@Composable
fun PenaltyTheme(
    themePreference: String = "system",   // "light" | "dark" | "system"
    content: @Composable () -> Unit
) {
    val darkTheme = when (themePreference) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }

    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && themePreference == "system" -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PenaltyTypography,
        content = content
    )
}