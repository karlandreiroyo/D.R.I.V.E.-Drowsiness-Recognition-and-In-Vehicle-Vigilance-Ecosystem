package com.example.d_r_i_v_e.backend

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.d_r_i_v_e.frontend.DriverProfile
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val apiService: AuthApiService = AuthApiService.create()
) : ViewModel() {

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    // Backward-compatible uiState Flow for existing UI callers
    val uiState: StateFlow<SignUpState> = _signUpState.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    // Overload para sa 4 parameters (kung sakaling ginagamit pa sa iba)
    fun signUp(
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String
    ) {
        signUp(
            fullName = fullName,
            email = email,
            phoneNumber = phoneNumber,
            emergencyName = "",
            emergencyPhone = "",
            vehicleType = "",
            plateNumber = "",
            password = password
        )
    }

    // Bagong signUp function para sa kumpletong form ng driver at vehicle details
    fun signUp(
        fullName: String,
        email: String,
        phoneNumber: String,
        emergencyName: String,
        emergencyPhone: String,
        vehicleType: String,
        plateNumber: String,
        password: String
    ) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val request = DriverSignUpRequest(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    password = password,
                    emergencyName = emergencyName,
                    emergencyPhone = emergencyPhone,
                    vehicleType = vehicleType,
                    plateNumber = plateNumber
                )

                val response = apiService.driverSignUp(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    val message = response.body()?.message ?: "Account registered successfully"
                    _signUpState.value = SignUpState.Success(message)
                } else {
                    val errorBodyString = response.errorBody()?.string()
                    val errorMessage = if (!errorBodyString.isNullOrBlank()) {
                        try {
                            val parsed = Gson().fromJson(errorBodyString, SignUpResponse::class.java)
                            parsed?.message ?: "Sign up failed."
                        } catch (e: Exception) {
                            "Sign up failed (${response.code()})."
                        }
                    } else {
                        response.body()?.message ?: "Sign up failed (${response.code()})."
                    }
                    _signUpState.value = SignUpState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error(
                    e.localizedMessage ?: "A network error occurred. Please try again."
                )
            }
        }
    }

    fun registerDriver(driverProfile: DriverProfile) {
        signUp(
            fullName = driverProfile.fullName,
            email = driverProfile.email,
            phoneNumber = driverProfile.phoneNumber,
            emergencyName = driverProfile.emergencyName,
            emergencyPhone = driverProfile.emergencyPhone,
            vehicleType = driverProfile.vehicleType,
            plateNumber = driverProfile.plateNumber,
            password = driverProfile.password
        )
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        val trimmedEmail = email.trim()
        val trimmedPassword = password.trim()

        if (trimmedEmail.isBlank() || trimmedPassword.isBlank()) {
            _loginError.value = "Email and password cannot be empty."
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _loginError.value = null
            try {
                val request = LoginRequest(
                    email = trimmedEmail,
                    password = trimmedPassword
                )

                val response = apiService.login(request)

                if (response.isSuccessful && response.body()?.success == true) {
                    _isLoading.value = false
                    _loginError.value = null
                    onSuccess()
                } else {
                    _isLoading.value = false
                    val errorBodyString = response.errorBody()?.string()
                    val errorMessage = if (!errorBodyString.isNullOrBlank()) {
                        try {
                            val parsed = Gson().fromJson(errorBodyString, LoginResponse::class.java)
                            parsed?.message ?: "Invalid email or password."
                        } catch (e: Exception) {
                            "Invalid email or password."
                        }
                    } else {
                        response.body()?.message ?: "Invalid email or password."
                    }
                    _loginError.value = errorMessage
                }
            } catch (e: Exception) {
                _isLoading.value = false
                _loginError.value = e.localizedMessage ?: "Failed to connect to the server. Please try again."
            }
        }
    }

    fun dismissDialog() {
        _signUpState.value = SignUpState.Idle
        _loginError.value = null
    }
}