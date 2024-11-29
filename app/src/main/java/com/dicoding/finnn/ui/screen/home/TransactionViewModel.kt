package com.dicoding.finnn.ui.screen.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.finnn.data.remote.response.Transaction
import com.dicoding.finnn.data.remote.response.TransactionRequest
import com.dicoding.finnn.data.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {
    private var authToken: String? = null

    val authHeader: String?
        get() = authToken?.let { "Bearer $it" }

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun loadTransactions() {
        viewModelScope.launch {
            Log.d("TransactionViewModel", "Loading transactions...")
            authToken?.let {
                val response = ApiConfig.apiService.getTransactions("Bearer $it")
                if (response.isSuccessful) {
                    _transactions.value = response.body() ?: emptyList()
                    Log.d("TransactionViewModel", "Transactions loaded successfully.")
                } else {
                    _errorMessage.value = "Failed to load transactions"
                    Log.e("TransactionViewModel", "Failed to load transactions: ${response.message()}")
                }
            } ?: Log.e("TransactionViewModel", "Auth token is null, cannot load transactions.")
        }
    }

    fun createTransaction(transaction: TransactionRequest) {
        viewModelScope.launch {
            authToken?.let {
                val response = ApiConfig.apiService.createTransaction("Bearer $it", transaction)
                if (response.isSuccessful) {
                    loadTransactions()  // Refresh the list
                } else {
                    _errorMessage.value = "Failed to create transaction"
                }
            }
        }
    }

    fun updateTransaction(id: Int, transaction: TransactionRequest) {
        viewModelScope.launch {
            authToken?.let {
                val response = ApiConfig.apiService.updateTransaction("Bearer $it", id, transaction)
                if (response.isSuccessful) {
                    loadTransactions()
                } else {
                    _errorMessage.value = "Failed to update transaction"
                }
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            authToken?.let {
                val response = ApiConfig.apiService.deleteTransaction("Bearer $it", id)
                if (response.isSuccessful) {
                    loadTransactions()  // Refresh transactions after delete
                } else {
                    _errorMessage.value = "Failed to delete transaction"
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null // Clear the error message
    }
}
