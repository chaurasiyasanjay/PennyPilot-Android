package com.pennypilot.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.pennypilot.app.presentation.addtransaction.AddTransactionScreen
import com.pennypilot.app.presentation.dashboard.DashboardScreen
import com.pennypilot.app.presentation.settings.SettingsScreen

private object PennyPilotDestination {
    const val DASHBOARD = "dashboard"
    const val ADD_TRANSACTION = "add_transaction"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
    const val SETTINGS = "settings"
}

@Composable
fun PennyPilotNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = PennyPilotDestination.DASHBOARD
    ) {
        composable(PennyPilotDestination.DASHBOARD) {
            DashboardScreen(
                onAddTransaction = {
                    navController.navigate(PennyPilotDestination.ADD_TRANSACTION)
                },
                onEditTransaction = { transactionId ->
                    navController.navigate("edit_transaction/$transactionId")
                },
                onOpenSettings = {
                    navController.navigate(PennyPilotDestination.SETTINGS)
                }
            )
        }
        composable(PennyPilotDestination.ADD_TRANSACTION) {
            AddTransactionScreen(onBack = { navController.popBackStack() })
        }
        composable(
            route = PennyPilotDestination.EDIT_TRANSACTION,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) {
            AddTransactionScreen(onBack = { navController.popBackStack() })
        }
        composable(PennyPilotDestination.SETTINGS) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
