package com.dicoding.finnn.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.dicoding.finnn.ui.screen.auth.AuthViewModel
import com.dicoding.finnn.ui.screen.home.TransactionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    authViewModel: AuthViewModel,
    transactionViewModel: TransactionViewModel = viewModel(),
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val transactions by transactionViewModel.transactions.collectAsState()
    val scope = rememberCoroutineScope()
    val isLoading by authViewModel.loading.collectAsState()
    val userProfile by authViewModel.userProfile.collectAsState()
    val userName = userProfile?.name ?: "Loading..."

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf(userName) }
    var editingPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLogoutProcessing by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        if (userProfile == null) {
            authViewModel.fetchUserProfile()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", style = MaterialTheme.typography.titleLarge.copy(color = Color.White)) },
                actions = {
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
        ) {
            Column(verticalArrangement = Arrangement.Top) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hello, $userName",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { showEditDialog = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val income = transactions.filter { it.type == "income" }.sumOf { it.amount }
                        val expense = transactions.filter { it.type == "expense" }.sumOf { it.amount }
                        val balance = income - expense

                        Text("Total Income: Rp $income", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text("Total Expense: Rp $expense", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Balance: Rp $balance",
                            style = MaterialTheme.typography.titleLarge,
                            color = if (balance >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                        )
                    }
                }
            }

            if (isLogoutProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    isLogoutProcessing = true
                    scope.launch {
                        authViewModel.logout()
                        isLogoutProcessing = false // Reset processing state
                        onLogout() // Trigger logout navigation
                    }
                }) {
                    Text("Logout")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editingName,
                        onValueChange = { editingName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = editingPassword,
                        onValueChange = { editingPassword = it },
                        label = { Text("New Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (editingName.isBlank() && editingPassword.isBlank()) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Please provide either a name or a password.")
                        }
                        return@TextButton
                    }

                    if (editingPassword.isNotBlank() && editingPassword != confirmPassword) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Passwords do not match.")
                        }
                        return@TextButton
                    }

                    scope.launch {
                        authViewModel.updateProfile(
                            name = if (editingName != userName) editingName else null,
                            password = if (editingPassword.isNotBlank()) editingPassword else null,
                            passwordConfirmation = if (editingPassword.isNotBlank()) confirmPassword else null
                        )

                        if (authViewModel.errorMessage.value == null) {
                            snackbarHostState.showSnackbar("Profile updated successfully")
                            authViewModel.fetchUserProfile()
                            showEditDialog = false
                        } else {
                            snackbarHostState.showSnackbar(authViewModel.errorMessage.value ?: "Error updating profile")
                        }
                    }
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (isLoading && userProfile == null) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    }
}
