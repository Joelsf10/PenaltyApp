package com.curso.penaltyapp

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.curso.penaltyapp.ui.navigation.PenaltyNavHost
import com.curso.penaltyapp.ui.navigation.Screen
import com.curso.penaltyapp.ui.theme.PenaltyGreen
import com.curso.penaltyapp.ui.theme.PenaltyTheme
import com.curso.penaltyapp.viewmodel.FinesViewModel
import com.curso.penaltyapp.viewmodel.SettingsViewModel
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                1001
            )
        }
        setContent {
            PenaltyApp(settingsViewModel = settingsViewModel)
        }
    }

    override fun attachBaseContext(newBase: Context) {
        val prefs = newBase.getSharedPreferences("penalty_lang", Context.MODE_PRIVATE)
        val lang = prefs.getString("language", "ca") ?: "ca"
        val locale = java.util.Locale(lang)
        java.util.Locale.setDefault(locale)
        val config = android.content.res.Configuration(newBase.resources.configuration)
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PenaltyApp(settingsViewModel: SettingsViewModel) {
    val settings by settingsViewModel.uiState.collectAsStateWithLifecycle()
    val isLoggedIn by settingsViewModel.isLoggedIn.collectAsStateWithLifecycle(false)

    // FinesViewModel compartit entre totes les pantalles de multes
    val finesViewModel: FinesViewModel = viewModel()

    PenaltyTheme(themePreference = settings.theme) {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // La bottom nav només apareix a les quatre pantalles principals
        val bottomNavScreens = listOf(
            Screen.Home, Screen.Fines, Screen.Ranking, Screen.Profile
        )
        val showBottomBar = currentDestination?.route in bottomNavScreens.map { it.route }

        // Definició dels elements de la bottom nav: destinació, etiqueta i icona
        val bottomNavItems = listOf(
            Triple(Screen.Home, stringResource(R.string.inicio), Icons.Default.Home),
            Triple(Screen.Fines, stringResource(R.string.multas), Icons.Default.Receipt),
            Triple(Screen.Ranking, stringResource(R.string.ranking2), Icons.Default.EmojiEvents),
            Triple(Screen.Profile, stringResource(R.string.perfil), Icons.Default.Person)
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { (screen, label, icon) ->
                            val selected = currentDestination?.hierarchy
                                ?.any { it.route == screen.route } == true

                            NavigationBarItem(
                                selected = selected,
                                onClick = {

                                    if (screen == Screen.Home) {

                                        navController.navigate(screen.route) {

                                            popUpTo(navController.graph.findStartDestination().id) {
                                                inclusive = false
                                            }

                                            launchSingleTop = true
                                        }

                                    } else {

                                        navController.navigate(screen.route) {

                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }

                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Icon(
                                        icon,
                                        contentDescription = label,
                                        tint = if (selected) PenaltyGreen
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                label = {
                                    Text(
                                        label,
                                        color = if (selected) PenaltyGreen
                                        else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = PenaltyGreen.copy(alpha = 0.15f)
                                )
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            PenaltyNavHost(
                navController = navController,
                isLoggedIn = isLoggedIn,
                finesViewModel = finesViewModel,
                settingsViewModel = settingsViewModel,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

