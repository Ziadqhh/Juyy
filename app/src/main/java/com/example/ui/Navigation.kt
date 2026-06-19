package com.example.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Reports : Screen("reports")
    object Stats : Screen("stats")
}

@Composable
fun Navigation(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            MainScreen(
                viewModel = viewModel,
                onNavigateToReports = { navController.navigate(Screen.Reports.route) },
                onNavigateToStats = { navController.navigate(Screen.Stats.route) }
            )
        }
        composable(Screen.Reports.route) {
            ReportsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
