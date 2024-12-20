package com.dicoding.finnn.ui.screen.home.mock

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PreviewTransactionViewModel {
    val transactions: StateFlow<List<Transaction>> = MutableStateFlow(
        listOf(
            Transaction("1", "Salary", 5000.0, "income"),
            Transaction("2", "Groceries", 150.0, "expense"),
            Transaction("3", "Freelance", 1200.0, "income")
        )
    )

    val errorMessage: StateFlow<String?> = MutableStateFlow(null)
}
