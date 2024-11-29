package com.dicoding.finnn.data.remote.response

data class UserProfileResponse(
    val id: Int,
    val name: String,
    val email: String,
    val email_verified_at: String?,
    val created_at: String,
    val updated_at: String
)
