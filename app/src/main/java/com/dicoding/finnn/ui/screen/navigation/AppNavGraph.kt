package com.dicoding.finnn.ui.screen.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.dicoding.finnn.ui.screen.auth.AuthViewModel
import com.dicoding.finnn.ui.screen.home.HomeScreen
import com.dicoding.finnn.ui.screen.home.TransactionViewModel
import com.dicoding.finnn.ui.screen.profile.ProfileScreen
import com.dicoding.finnn.ui.screen.transaction.AddTransactionScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.dicoding.finnn.data.remote.retrofit.ApiConfig
import com.dicoding.finnn.ui.screen.detail.TransactionDetailScreen
import com.dicoding.finnn.ui.screen.edit.EditTransactionScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel,
    onLogout: () -> Unit
) {
    NavHost(navController = navController, startDestination = "home") {
        // Home Screen
        composable("home") {
            HomeScreen(
                navController = navController,
                transactionViewModel = transactionViewModel
            )
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen(
                authViewModel = authViewModel, // Corrected parameter name
                transactionViewModel = transactionViewModel, // Added if needed for transaction data
                onLogout = onLogout
            )
        }

        // Add Transaction Screen
        composable("addTransaction") {
            AddTransactionScreen(
                transactionViewModel = transactionViewModel,
                onTransactionAdded = {
                    navController.navigate("home") // Navigate to home after adding a transaction
                }
            )
        }

        // Transaction Detail Screen
        composable(
            route = "transactionDetail/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
            transactionViewModel.authHeader?.let { authHeader ->
                TransactionDetailScreen(
                    navController = navController,
                    transactionId = transactionId,
                    apiService = ApiConfig.apiService,
                    authHeader = authHeader
                )
            }
        }

        // Edit Transaction Screen
        composable(
            route = "editTransaction/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getInt("transactionId") ?: 0
            EditTransactionScreen(
                navController = navController,
                transactionViewModel = transactionViewModel,
                transactionId = transactionId
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewNavigation() {
    val navController = rememberNavController()
    val authViewModel = AuthViewModel() // Provide a test instance of AuthViewModel
    val transactionViewModel = TransactionViewModel() // Provide a test instance of TransactionViewModel
    AppNavGraph(
        navController = navController,
        authViewModel = authViewModel,
        transactionViewModel = transactionViewModel,
        onLogout = {} // Pass an empty lambda for logout
    )
}
