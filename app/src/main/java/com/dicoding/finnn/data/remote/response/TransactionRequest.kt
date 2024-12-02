package com.dicoding.finnn.data.remote.response

import com.google.gson.annotations.SerializedName

data class TransactionRequest(
    val title: String,
    val description: String,
    val amount: Double,
    val type: String,
    @SerializedName("transaction_date") val transactionDate: String
)
