package com.dicoding.finnn.data.remote.response

data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: Data? = null
)

data class Data(
    val token: String
)
