package com.dicoding.finnn.ui.screen.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dicoding.finnn.data.remote.response.Transaction
import com.dicoding.finnn.ui.component.BottomNavigationBar
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by transactionViewModel.transactions.collectAsState()
    val errorMessage by transactionViewModel.errorMessage.collectAsState()
    val isLoading by transactionViewModel.isLoading.collectAsState()

    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTransactionId by remember { mutableStateOf<Int?>(null) }

    // State untuk filter
    var selectedFilter by remember { mutableStateOf("All") }
    val filteredTransactions = transactions.filter { transaction ->
        when (selectedFilter) {
            "Income" -> transaction.type == "income"
            "Expense" -> transaction.type == "expense"
            else -> true
        }
    }

    LaunchedEffect(Unit) {
        transactionViewModel.loadTransactions()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Transactions", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    FilterIconDropdown(selectedFilter, onFilterChange = { selectedFilter = it })
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = { BottomNavigationBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("addTransaction") },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Transaction")
            }
        },
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                Column(
                    modifier = modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    if (errorMessage != null) {
                        Text(
                            text = "Error: $errorMessage",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                    }

                    if (filteredTransactions.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No transactions available.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(filteredTransactions) { transaction ->
                                TransactionCard(transaction, navController) {
                                    // Tampilkan dialog konfirmasi penghapusan
                                    selectedTransactionId = transaction.id
                                    showDeleteDialog = true
                                }
                            }
                        }
                    }
                }
            }

            if (showDeleteDialog && selectedTransactionId != null) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text("Delete Transaction") },
                    text = { Text("Are you sure you want to delete this transaction?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                transactionViewModel.deleteTransaction(selectedTransactionId!!)
                                showDeleteDialog = false
                            }
                        ) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FilterIconDropdown(selectedFilter: String, onFilterChange: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
        IconButton(onClick = { expanded = true }) {
            Icon(
                Icons.Filled.ArrowDropDown,
                tint = MaterialTheme.colorScheme.onPrimary,
                contentDescription = "Filter Transactions"
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text("All") },
                onClick = {
                    onFilterChange("All")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Income") },
                onClick = {
                    onFilterChange("Income")
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("Expense") },
                onClick = {
                    onFilterChange("Expense")
                    expanded = false
                }
            )
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction, navController: NavController, onDeleteClick: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate("transactionDetail/${transaction.id}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))

                val formattedAmount = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
                    .format(transaction.amount)
                Text(
                    text = formattedAmount,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (transaction.type == "income") Color(0xFF4CAF50) else Color(0xFFF44336)
                )
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = transaction.type.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(
                    onClick = {
                        navController.navigate("editTransaction/${transaction.id}")
                    }
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(
                    onClick = { onDeleteClick(transaction.id) }
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
