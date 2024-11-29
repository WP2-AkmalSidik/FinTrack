package com.dicoding.finnn.data.remote.response


data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val user: UserResponse
)