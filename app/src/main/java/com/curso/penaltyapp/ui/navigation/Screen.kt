package com.curso.penaltyapp.ui.navigation


/**
 * Sealed class defining all navigation destinations in the app.
 * Follows Jetpack Navigation Compose best practices.
 */
sealed class Screen(val route: String) {

    // ─── Flux d'autenticació ──────────────────────────────────────────────────
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object TeamSetup : Screen("team_setup")

    // ─── Pantalles principals (bottom nav) ────────────────────────────────────
    data object Home : Screen("home")
    data object Fines : Screen("fines")
    data object Ranking : Screen("ranking")
    data object Profile : Screen("profile")
    data object Settings : Screen("settings")

    // ─── Pantalles de detall (reben arguments per navegació) ─────────────────
    data object FineDetail : Screen("fine_detail/{fineId}") {
        fun createRoute(fineId: String) = "fine_detail/$fineId"
    }
    data object AddFine : Screen("add_fine")
    data object NfcPayment : Screen("nfc_payment/{fineId}") {
        fun createRoute(fineId: String) = "nfc_payment/$fineId"
    }
    data object TeamMembers : Screen("team_members")
}

/**
 * Model de dades per als elements de la barra de navegació inferior.
 * Agrupa la destinació, l'etiqueta i la icona de cada pestanya.
 */
data class BottomNavItem(
    val screen: Screen,
    val label: String,
    val iconRes: androidx.compose.ui.graphics.vector.ImageVector
)
