package com.dicoding.finnn.data.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("auth_prefs")

class DataStoreManager(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        private val LOGIN_STATUS_KEY = booleanPreferencesKey("is_logged_in")
    }

    val authToken: Flow<String?> = dataStore.data.map { prefs ->
        prefs[AUTH_TOKEN_KEY]
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[LOGIN_STATUS_KEY] ?: false
    }

    suspend fun saveUserSession(token: String, isLoggedIn: Boolean) {
        dataStore.edit { prefs ->
            prefs[AUTH_TOKEN_KEY] = token
            prefs[LOGIN_STATUS_KEY] = isLoggedIn
        }
    }

    suspend fun clearSession() {
        dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}
