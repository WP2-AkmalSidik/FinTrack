package com.dicoding.finnn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.dicoding.finnn.ui.component.BottomNavigationBar
import com.dicoding.finnn.ui.screen.auth.AuthViewModel
import com.dicoding.finnn.ui.screen.auth.LoginScreen
import com.dicoding.finnn.ui.screen.auth.RegisterScreen
import com.dicoding.finnn.ui.screen.navigation.AppNavGraph
import com.dicoding.finnn.ui.theme.FinnnTheme
import com.dicoding.finnn.ui.screen.home.TransactionViewModel // Pastikan ini diimport dengan benar

sealed class Screen {
    object Login : Screen()
    object Register : Screen()
    object App : Screen() // Tambahkan Screen App sebagai layar utama setelah login
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FinnnTheme {
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }
                val authViewModel: AuthViewModel = viewModel()
                val transactionViewModel: TransactionViewModel = viewModel()
                val navController = rememberNavController()

                LaunchedEffect(authViewModel.loginStatus.collectAsState().value) {
                    currentScreen = if (authViewModel.loginStatus.value) Screen.App else Screen.Login
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
                            onLoginSuccess = { token ->
                                transactionViewModel.setAuthToken(token) // Menyimpan token
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
                            onLogout = { currentScreen = Screen.Login }
                        )
                    }
                }
            }
        }
    }
}
