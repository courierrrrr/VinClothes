package com.example.vintageclothesapp.auth

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.vintageclothesapp.ClothingApiService
import kotlinx.coroutines.launch

class AuthViewModel(
    application: Application,
    private val api: ClothingApiService
) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>()

    var loginSuccess by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun login(username: String, password: String) {
        viewModelScope.launch {
            try {
                val response = api.login(LoginRequest(username, password))
                android.util.Log.d("TOKEN", "Token received: ${response.token}")
                TokenStore.save(context, response.token)
                loginSuccess = true
            } catch (e: Exception) {
                errorMessage = "Login failed. Check your credentials."
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        TokenStore.clear(context)
        onDone()
    }

    fun resetLoginSuccess() {
        loginSuccess = false
    }

}
