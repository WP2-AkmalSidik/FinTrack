package com.dicoding.finnn.data.remote.response

data class AuthResponse(
    val user: User,
    val token: String
)

data class User(
    val id: Int,
    val name: String,
    val email: String
)
