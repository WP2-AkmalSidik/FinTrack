package com.dicoding.finnn.ui.screen.edit

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.dicoding.finnn.data.remote.response.TransactionRequest
import com.dicoding.finnn.ui.screen.home.TransactionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel,
    transactionId: Int
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("income") }
    var description by remember { mutableStateOf("") }
    var transactionDate by remember { mutableStateOf("") }

    LaunchedEffect(transactionId) {
        val transaction = transactionViewModel.transactions.value.find { it.id == transactionId }
        transaction?.let {
            title = it.title ?: ""
            amount = it.amount.toString() ?: ""
            type = it.type ?: "income"
            description = it.description ?: ""
            transactionDate = it.transaction_date ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Transaction") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                label = { Text("Amount") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = false,
                maxLines = 4
            )
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = transactionDate,
                onValueChange = { transactionDate = it },
                label = { Text("Transaction Date") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))

            // Dropdown Menu for Type
            Text(
                text = "Transaction Type",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                RadioButton(
                    selected = type == "income",
                    onClick = { type = "income" }
                )
                Text("Income", style = MaterialTheme.typography.bodyMedium)

                RadioButton(
                    selected = type == "expense",
                    onClick = { type = "expense" }
                )
                Text("Expense", style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    val transactionRequest = TransactionRequest(
                        title = title,
                        description = description,
                        amount = amount.toDoubleOrNull() ?: 0.0,
                        type = type,
                        transactionDate = transactionDate
                    )
                    transactionViewModel.updateTransaction(transactionId, transactionRequest)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Save Changes", color = MaterialTheme.colorScheme.onPrimary)
            }
        }
    }
}
