package com.dicoding.finnn.data.remote.response

data class Transaction(
    val id: Int,
    val title: String,
    val description: String?,
    val amount: Double,
    val type: String,
    val transactionDate: String
)