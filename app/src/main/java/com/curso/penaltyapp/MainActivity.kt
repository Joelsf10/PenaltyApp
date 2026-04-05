package com.curso.penaltyapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.curso.penaltyapp.ui.navigation.PenaltyNavHost
import com.curso.penaltyapp.ui.navigation.Screen
import com.curso.penaltyapp.ui.theme.*
import com.curso.penaltyapp.viewmodel.FinesViewModel
import com.curso.penaltyapp.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PenaltyApp(settingsViewModel = settingsViewModel)
        }
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
            Triple(Screen.Home, "Inici", Icons.Default.Home),
            Triple(Screen.Fines, "Multes", Icons.Default.Receipt),
            Triple(Screen.Ranking, "Rànquing", Icons.Default.EmojiEvents),
            Triple(Screen.Profile, "Perfil", Icons.Default.Person)
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
                                    navController.navigate(screen.route) {
                                        // `popUpTo` evita acumular destinacions al back stack
                                        // en navegar entre pestanyes repetidament
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        // `launchSingleTop` evita crear múltiples còpies
                                        // de la mateixa pantalla al stack
                                        launchSingleTop = true
                                        // `restoreState` recupera l'estat de la pantalla
                                        // si ja s'havia visitat anteriorment
                                        restoreState = true
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