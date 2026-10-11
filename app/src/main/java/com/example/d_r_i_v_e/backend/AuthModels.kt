package com.example.d_r_i_v_e.backend

import com.google.gson.annotations.SerializedName

data class SignUpRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("confirmPassword") val confirmPassword: String
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
