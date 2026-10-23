package com.example.d_r_i_v_e.backend

import com.google.gson.annotations.SerializedName

sealed class SignUpUiState {
    object Idle : SignUpUiState()
    object Loading : SignUpUiState()
    data class Success(val message: String) : SignUpUiState()
    data class ValidationError(val message: String) : SignUpUiState()
    data class ConnectionError(val message: String) : SignUpUiState()
}

data class SignUpRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String,
    @SerializedName("emergencyName") val emergencyName: String,
    @SerializedName("emergencyPhone") val emergencyPhone: String,
    @SerializedName("vehicleType") val vehicleType: String,
    @SerializedName("plateNumber") val plateNumber: String
)

data class SignUpResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("errors") val errors: List<FieldError>? = null
)

data class FieldError(
    @SerializedName("field") val field: String,
    @SerializedName("message") val message: String
)