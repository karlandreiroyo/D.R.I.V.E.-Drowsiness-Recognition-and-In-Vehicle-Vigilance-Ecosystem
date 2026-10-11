package com.example.d_r_i_v_e.backend

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface SignUpUiState {
    object Idle : SignUpUiState
    object Loading : SignUpUiState
    data class Success(val message: String) : SignUpUiState
    data class ValidationError(val message: String) : SignUpUiState
    data class ConnectionError(val message: String) : SignUpUiState
}

class AuthViewModel(
    private val apiService: AuthApiService = AuthApiService.create()
) : ViewModel() {

    // SignUp State
    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    // Login State
    var loginError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)

    fun signup(fullName: String, email: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            try {
                val request = SignUpRequest(
                    fullName = fullName.trim(),
                    email = email.trim(),
                    password = password,
                    confirmPassword = confirmPassword
                )
                val response = apiService.signup(request)

                if (response.isSuccessful) {
                    val body = response.body()
                    val msg = body?.message ?: "Signup successful!"
                    _uiState.value = SignUpUiState.Success(msg)
                } else {
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        Gson().fromJson(errorJson, SignUpResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }

                    val message = parsedError?.message
                        ?: parsedError?.errors?.firstOrNull()?.message
                        ?: "Validation failed. Please check your inputs."

                    _uiState.value = SignUpUiState.ValidationError(message)
                }
            } catch (e: Exception) {
                val friendlyMessage = "Unable to connect to the backend server.\n\n" +
                        "1. Ensure your Node.js backend is running (`npm run dev`).\n" +
                        "2. If using a physical Android device, run this command in terminal:\n" +
                        "adb reverse tcp:5000 tcp:5000"

                _uiState.value = SignUpUiState.ConnectionError(friendlyMessage)
            }
        }
    }

    fun loginUser(emailOrPhone: String, password: String, onSuccess: () -> Unit) {
        if (emailOrPhone.isBlank() || password.isBlank()) {
            loginError = "Please fill in all fields."
            return
        }

        isLoading = true
        loginError = null

        viewModelScope.launch {
            try {
                val bodyMap = mapOf(
                    "email" to emailOrPhone.trim(),
                    "password" to password
                )
                val response = apiService.login(bodyMap)

                isLoading = false
                if (response.isSuccessful && response.body()?.success == true) {
                    loginError = null
                    onSuccess()
                } else {
                    val errorJson = response.errorBody()?.string()
                    val parsedError = try {
                        Gson().fromJson(errorJson, SignUpResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }
                    loginError = parsedError?.message ?: "Invalid email or password."
                }
            } catch (e: Exception) {
                isLoading = false
                loginError = "Cannot connect to server. Ensure your backend is running."
            }
        }
    }

    fun dismissDialog() {
        _uiState.value = SignUpUiState.Idle
    }
}
