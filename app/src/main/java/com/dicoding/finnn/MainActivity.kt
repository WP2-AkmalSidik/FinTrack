package com.dicoding.finnn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.dicoding.finnn.data.local.DataStoreManager
import com.dicoding.finnn.ui.component.BottomNavigationBar
import com.dicoding.finnn.ui.screen.auth.AuthViewModel
import com.dicoding.finnn.ui.screen.auth.LoginScreen
import com.dicoding.finnn.ui.screen.auth.RegisterScreen
import com.dicoding.finnn.ui.screen.navigation.AppNavGraph
import com.dicoding.finnn.ui.theme.FinnnTheme
import com.dicoding.finnn.ui.screen.home.TransactionViewModel
import kotlinx.coroutines.launch

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object App : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val dataStoreManager = DataStoreManager(applicationContext)

        setContent {
            FinnnTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                val authViewModel: AuthViewModel = viewModel()
                val transactionViewModel: TransactionViewModel = viewModel()
                val navController = rememberNavController()

                // Initialize token from DataStore
                LaunchedEffect(Unit) {
                    dataStoreManager.authToken.collect { token ->
                        if (!token.isNullOrEmpty()) {
                            authViewModel.authToken = token
                            transactionViewModel.setAuthToken(token)
                            currentScreen = Screen.App
                        } else {
                            currentScreen = Screen.Login
                        }
                    }
                }

                Scaffold(
                    bottomBar = {
                        if (currentScreen == Screen.App) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { paddingValues ->
                    when (currentScreen) {
                        is Screen.Login -> LoginScreen(
                            viewModel = authViewModel,
                            onLoginSuccess = {
                                currentScreen = Screen.App
                            },
                            onNavigateToRegister = { currentScreen = Screen.Register },
                            modifier = Modifier.padding(paddingValues)
                        )
                        is Screen.Register -> RegisterScreen(
                            viewModel = authViewModel,
                            onRegisterSuccess = { currentScreen = Screen.Login },
                            onNavigateToLogin = { currentScreen = Screen.Login },
                            modifier = Modifier.padding(paddingValues)
                        )
                        is Screen.App -> AppNavGraph(
                            navController = navController,
                            authViewModel = authViewModel,
                            transactionViewModel = transactionViewModel,
                            onLogout = {
                                lifecycleScope.launch { // Use lifecycleScope to handle suspend function
                                    dataStoreManager.clearSession() // Clear session
                                }
                                currentScreen = Screen.Login
                            }
                        )
                    }
                }
            }
        }
    }
}
