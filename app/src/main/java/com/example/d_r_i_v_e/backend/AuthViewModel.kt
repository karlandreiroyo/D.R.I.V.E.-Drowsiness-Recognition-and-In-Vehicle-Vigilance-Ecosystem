package com.example.d_r_i_v_e.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    fun signup(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String,
        emergencyName: String,
        emergencyPhone: String,
        vehicleType: String,
        plateNumber: String
    ) {
        if (password != confirmPassword) {
            _uiState.value = SignUpUiState.ValidationError("Passwords do not match.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading
            try {
                val request = SignUpRequest(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    password = password,
                    confirmPassword = confirmPassword,
                    emergencyName = emergencyName,
                    emergencyPhone = emergencyPhone,
                    vehicleType = vehicleType,
                    plateNumber = plateNumber
                )

                val response = AuthApiService.create().signup(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _uiState.value = SignUpUiState.Success(
                        response.body()?.message ?: "Account successfully created!"
                    )
                } else {
                    val errorMsg = response.body()?.message ?: "Registration failed."
                    _uiState.value = SignUpUiState.ValidationError(errorMsg)
                }
            } catch (e: Exception) {
                _uiState.value = SignUpUiState.ConnectionError(
                    e.localizedMessage ?: "Failed to connect to server."
                )
            }
        }
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _loginError.value = "Email and password cannot be empty."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                val credentials = mapOf("email" to email, "password" to password)
                val response = AuthApiService.create().login(credentials)

                _isLoading.value = false
                if (response.isSuccessful && response.body()?.success == true) {
                    onSuccess()
                } else {
                    _loginError.value = response.body()?.message ?: "Invalid email or password."
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _loginError.value = e.localizedMessage ?: "Login failed. Please try again."
            }
        }
    }

    fun dismissDialog() {
        _uiState.value = SignUpUiState.Idle
        _loginError.value = null
    }
}