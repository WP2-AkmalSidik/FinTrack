package com.dicoding.finnn.data.remote.response

data class UpdateProfileResponse(
    val status: Boolean,
    val message: String,
    val user: User
)
