package com.dicoding.finnn.ui.screen.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.finnn.data.remote.response.User
import com.dicoding.finnn.data.remote.retrofit.ApiConfig
import com.dicoding.finnn.data.remote.retrofit.RegisterRequest
import kotlinx.coroutines.launch
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.finnn.data.local.DataStoreManager
import com.dicoding.finnn.data.remote.retrofit.LoginRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStoreManager = DataStoreManager(application)

    private val _loginStatus = MutableStateFlow(false)
    val loginStatus: StateFlow<Boolean> = _loginStatus

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _userProfile = MutableStateFlow<User?>(null)
    val userProfile: StateFlow<User?> = _userProfile

    var authToken: String? = null

    init {
        viewModelScope.launch {
            dataStoreManager.isLoggedIn.collect { loggedIn ->
                _loginStatus.value = loggedIn
            }
            dataStoreManager.authToken.collect { token ->
                authToken = token
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                val response = ApiConfig.apiService.login(LoginRequest(email, password))
                _loading.value = false

                if (response.isSuccessful && response.body() != null) {
                    val token = response.body()?.token.orEmpty()
                    authToken = token
                    _loginStatus.value = true

                    // Simpan token ke DataStore
                    dataStoreManager.saveUserSession(token, true)
                } else {
                    _errorMessage.value = "Login failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _loading.value = false
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _loading.value = true
            try {
                authToken?.let { token ->
                    val response = ApiConfig.apiService.logout("Bearer $token")
                    if (response.isSuccessful) {
                        dataStoreManager.clearSession()
                        authToken = null
                        _loginStatus.value = false
                    } else {
                        _errorMessage.value = "Failed to logout: ${response.message()}"
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error during logout: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }

    fun fetchUserProfile() {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                val authHeader = authToken?.let { "Bearer $it" } ?: throw Exception("Auth token not found")
                val response = ApiConfig.apiService.getUserProfile(authHeader)

                if (response.isSuccessful) {
                    _userProfile.value = response.body()?.user
                    Log.d("AuthViewModel", "User profile fetched successfully.")
                } else {
                    _errorMessage.value = "Failed to fetch user profile: ${response.message()}"
                    Log.e("AuthViewModel", "Failed to fetch user profile: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching user profile: ${e.message}"
                Log.e("AuthViewModel", "Error fetching user profile: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }

    fun updateProfile(name: String?, password: String?, passwordConfirmation: String?) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                val authHeader = authToken?.let { "Bearer $it" } ?: throw Exception("Auth token not found")
                val response = ApiConfig.apiService.updateProfile(authHeader, name, password, passwordConfirmation)

                if (response.isSuccessful) {
                    _userProfile.value = response.body()?.user // Perbarui profil pengguna
                    Log.d("AuthViewModel", "Profile updated successfully.")
                } else {
                    _errorMessage.value = "Failed to update profile: ${response.message()}"
                    Log.e("AuthViewModel", "Failed to update profile: ${response.message()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error updating profile: ${e.message}"
                Log.e("AuthViewModel", "Error updating profile: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }


    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _errorMessage.value = null

            try {
                val response = ApiConfig.apiService.register(RegisterRequest(name, email, password))
                _loading.value = false

                if (response.isSuccessful && response.body() != null) {
                    _loginStatus.value = true
                    Log.d("AuthViewModel", "Register successful.")
                } else {
                    val errorMsg = "Register failed: ${response.message()}"
                    Log.e("AuthViewModel", errorMsg)
                }
            } catch (e: Exception) {
                _loading.value = false
                val errorMsg = "Register request failed: ${e.message}"
                Log.e("AuthViewModel", errorMsg)
            }
        }
    }
}
