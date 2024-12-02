package com.dicoding.finnn.data.remote.retrofit

import com.dicoding.finnn.data.remote.response.AuthResponse
import com.dicoding.finnn.data.remote.response.Transaction
import com.dicoding.finnn.data.remote.response.TransactionRequest
import com.dicoding.finnn.data.remote.response.UpdateProfileResponse
import com.dicoding.finnn.data.remote.response.UserProfileResponse
import com.dicoding.finnn.data.remote.response.UserResponse
import retrofit2.Response
import retrofit2.http.*

data class LoginRequest(val email: String, val password: String)
data class RegisterRequest(val name: String, val email: String, val password: String)

interface ApiService {
    @POST("register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("logout")
    suspend fun logout(@Header("Authorization") authHeader: String): Response<Unit>

    @GET("transactions")
    suspend fun getTransactions(@Header("Authorization") authHeader: String): Response<List<Transaction>>

    @GET("transactions/{id}")
    suspend fun getTransactionDetail(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<Transaction>

    @POST("transactions")
    suspend fun createTransaction(
        @Header("Authorization") authHeader: String,
        @Body transaction: TransactionRequest
    ): Response<Transaction>

    @PUT("transactions/{id}")
    suspend fun updateTransaction(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int,
        @Body transaction: TransactionRequest
    ): Response<Transaction>

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(
        @Header("Authorization") authHeader: String,
        @Path("id") id: Int
    ): Response<Unit>

    @PUT("update-profile")
    suspend fun updateProfile(
        @Header("Authorization") authHeader: String,
        @Query("name") name: String?,
        @Query("password") password: String?,
        @Query("password_confirmation") passwordConfirmation: String?
    ): Response<UpdateProfileResponse>


    @GET("userprofile")
    suspend fun getUserProfile(
        @Header("Authorization") authHeader: String
    ): Response<UserResponse>

}