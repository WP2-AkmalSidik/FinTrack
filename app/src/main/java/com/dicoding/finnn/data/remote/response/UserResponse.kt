package com.dicoding.finnn.data.remote.response

data class UserResponse(
    val status: Boolean,
    val message: String,
    val user: User
)
