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

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun loadTransactions() {
        viewModelScope.launch {
            if (_transactions.value.isEmpty()) { // Hanya tampilkan loading jika belum ada data
                _isLoading.value = true
            }
            authToken?.let {
                try {
                    val response = ApiConfig.apiService.getTransactions("Bearer $it")
                    if (response.isSuccessful) {
                        _transactions.value = response.body() ?: emptyList()
                    } else {
                        _errorMessage.value = "Failed to load transactions"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error loading transactions: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun createTransaction(transaction: TransactionRequest) {
        viewModelScope.launch {
            authToken?.let {
                try {
                    val response = ApiConfig.apiService.createTransaction("Bearer $it", transaction)
                    if (response.isSuccessful) {
                        loadTransactions() // Refresh the list
                    } else {
                        _errorMessage.value = "Failed to create transaction"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error creating transaction: ${e.message}"
                }
            }
        }
    }

    fun updateTransaction(id: Int, transaction: TransactionRequest) {
        viewModelScope.launch {
            authToken?.let {
                try {
                    val response = ApiConfig.apiService.updateTransaction("Bearer $it", id, transaction)
                    if (response.isSuccessful) {
                        loadTransactions()
                    } else {
                        _errorMessage.value = "Failed to update transaction"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error updating transaction: ${e.message}"
                }
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            _isLoading.value = true // Tampilkan loading saat menghapus
            authToken?.let {
                try {
                    val response = ApiConfig.apiService.deleteTransaction("Bearer $it", id)
                    if (response.isSuccessful) {
                        loadTransactions() // Refresh transaksi setelah menghapus
                    } else {
                        _errorMessage.value = "Failed to delete transaction"
                    }
                } catch (e: Exception) {
                    _errorMessage.value = "Error deleting transaction: ${e.message}"
                } finally {
                    _isLoading.value = false
                }
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null // Hapus pesan error
    }
}
