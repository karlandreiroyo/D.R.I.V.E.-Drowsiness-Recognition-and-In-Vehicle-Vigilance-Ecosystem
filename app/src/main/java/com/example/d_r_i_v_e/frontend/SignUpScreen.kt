package com.example.d_r_i_v_e.frontend

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.d_r_i_v_e.backend.SignUpUiState

// ---------- Validation (same rules as DriverRegistrationScreen) ----------

private fun validateName(name: String, label: String): String? {
    val trimmed = name.trim()
    return when {
        trimmed.isEmpty() -> "$label is required."
        trimmed.length < 2 -> "$label must be at least 2 characters."
        !trimmed.all { it.isLetter() || it == ' ' } -> "$label can only contain letters and spaces."
        else -> null
    }
}

private fun validateEmail(email: String): String? {
    val trimmed = email.trim()
    val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
    return when {
        trimmed.isEmpty() -> "Email address is required."
        !emailRegex.matches(trimmed) -> "Enter a valid email address (e.g. name@domain.com)."
        else -> null
    }
}

private fun validatePhone(phone: String, label: String): String? {
    val trimmed = phone.trim()
    val phoneRegex = "^09[0-9]{9}$".toRegex()
    return when {
        trimmed.isEmpty() -> "$label is required."
        !phoneRegex.matches(trimmed) -> "Enter a valid 11-digit number starting with 09 (e.g. 09123456789)."
        else -> null
    }
}

private fun validatePlateNumber(plate: String): String? {
    val trimmed = plate.trim()
    return when {
        trimmed.isEmpty() -> "Vehicle plate number is required."
        trimmed.length < 3 -> "Enter a valid plate number (e.g. ABC 1234)."
        !trimmed.all { it.isLetterOrDigit() || it == ' ' } -> "Plate number can only contain letters, numbers, and spaces."
        else -> null
    }
}

private fun validatePassword(password: String): String? {
    return when {
        password.isEmpty() -> "Password is required."
        password.length < 8 -> "Password must be at least 8 characters."
        !password.any { it.isLetter() } -> "Password must contain at least one letter."
        !password.any { it.isDigit() || !it.isLetterOrDigit() } -> "Include at least one number or special character."
        else -> null
    }
}

private fun validateConfirmPassword(password: String, confirmPassword: String): String? {
    return when {
        confirmPassword.isEmpty() -> "Please confirm your password."
        confirmPassword != password -> "Passwords do not match."
        else -> null
    }
}

private fun getPasswordStrength(password: String): Int {
    if (password.length < 8) return 0
    var score = 1
    if (password.any { it.isDigit() }) score++
    if (password.any { !it.isLetterOrDigit() } || password.any { it.isUpperCase() }) score++
    return score
}

// ---------- Input filters: invalid characters are removed as the user types or pastes ----------

private fun sanitizeName(input: String): String =
    input.filter { it.isLetter() || it == ' ' }
        .replace(Regex(" {2,}"), " ")
        .trimStart()
        .take(50)

private fun sanitizePhone(input: String): String =
    input.filter { it in '0'..'9' }.take(11)

private fun sanitizePlate(input: String): String =
    input.uppercase()
        .filter { it in 'A'..'Z' || it in '0'..'9' || it == ' ' }
        .replace(Regex(" {2,}"), " ")
        .trimStart()
        .take(8)

private fun sanitizeEmail(input: String): String =
    input.filter { !it.isWhitespace() }.take(100)

// ---------- Shared UI pieces ----------

@Composable
private fun appTextFieldColors(
    focusedBorderColor: Color = Color(0xFF6C7BFF),
    unfocusedBorderColor: Color = Color.Transparent,
    isError: Boolean = false
): TextFieldColors {
    val errorColor = Color(0xFFFF5252)
    return OutlinedTextFieldDefaults.colors(
        focusedContainerColor = Color(0xFF2C2C2E),
        unfocusedContainerColor = Color(0xFF2C2C2E),
        errorContainerColor = Color(0xFF2C2C2E),
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        errorTextColor = Color.White,
        focusedBorderColor = if (isError) errorColor else focusedBorderColor,
        unfocusedBorderColor = unfocusedBorderColor,
        errorBorderColor = errorColor,
        cursorColor = Color.White,
        errorCursorColor = Color.White
    )
}

private fun validBorder(touched: Boolean, error: String?): Color =
    if (touched && error == null) Color(0xFF4CAF50) else Color.Transparent

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        color = Color.White.copy(alpha = 0.7f),
        fontSize = 13.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun FieldError(visible: Boolean, message: String?) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Text(
            text = message ?: "",
            color = Color(0xFFFF5252),
            fontSize = 12.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, top = 4.dp)
        )
    }
}

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = Color(0xFF6C7BFF),
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    )
}

@Composable
private fun InfoDialog(
    title: String,
    message: String,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, fontWeight = FontWeight.Bold, color = Color.White) },
        text = { Text(text = message, color = Color.White.copy(alpha = 0.9f)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(confirmText, color = Color(0xFF6C7BFF), fontWeight = FontWeight.Bold)
            }
        },
        containerColor = Color(0xFF2C2C2E),
        titleContentColor = Color.White,
        textContentColor = Color.White
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    uiState: SignUpUiState = SignUpUiState.Idle,
    onBackClick: () -> Unit,
    onSignUpClick: (
        fullName: String,
        email: String,
        phoneNumber: String,
        password: String,
        confirmPassword: String,
        emergencyName: String,
        emergencyPhone: String,
        vehicleType: String,
        plateNumber: String
    ) -> Unit,
    onDismissDialog: () -> Unit = {},
    onGoogleClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var emergencyName by remember { mutableStateOf("") }
    var emergencyPhone by remember { mutableStateOf("") }

    val vehicleTypes = listOf(
        "Sedan", "Hatchback", "Coupe", "Convertible",
        "Station Wagon", "SUV", "Crossover", "Minivan"
    )
    var selectedVehicleType by remember { mutableStateOf(vehicleTypes[0]) }
    var isVehicleDropdownExpanded by remember { mutableStateOf(false) }
    var plateNumber by remember { mutableStateOf("") }

    var fullNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var phoneTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }
    var emergencyNameTouched by remember { mutableStateOf(false) }
    var emergencyPhoneTouched by remember { mutableStateOf(false) }
    var plateTouched by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isAgreed by remember { mutableStateOf(false) }

    // Live validation: recalculated on every keystroke
    val fullNameResult = validateName(fullName, "Full name")
    val emailResult = validateEmail(email)
    val phoneResult = validatePhone(phoneNumber, "Phone number")
    val passwordResult = validatePassword(password)
    val confirmPasswordResult = validateConfirmPassword(password, confirmPassword)
    val emergencyNameResult = validateName(emergencyName, "Emergency contact name")
    val emergencyPhoneResult = validatePhone(emergencyPhone, "Emergency contact phone")
    val plateResult = validatePlateNumber(plateNumber)

    val fullNameError = if (fullNameTouched) fullNameResult else null
    val emailError = if (emailTouched) emailResult else null
    val phoneError = if (phoneTouched) phoneResult else null
    val passwordError = if (passwordTouched) passwordResult else null
    val confirmPasswordError = if (confirmPasswordTouched) confirmPasswordResult else null
    val emergencyNameError = if (emergencyNameTouched) emergencyNameResult else null
    val emergencyPhoneError = if (emergencyPhoneTouched) emergencyPhoneResult else null
    val plateError = if (plateTouched) plateResult else null

    val isFormValid = fullNameResult == null && emailResult == null && phoneResult == null &&
            passwordResult == null && confirmPasswordResult == null &&
            emergencyNameResult == null && emergencyPhoneResult == null && plateResult == null

    // Dialogs for backend validation errors / connection errors / success
    when (uiState) {
        is SignUpUiState.ValidationError -> InfoDialog(
            title = "Validation Error",
            message = uiState.message,
            confirmText = "OK",
            onConfirm = onDismissDialog,
            onDismiss = onDismissDialog
        )

        is SignUpUiState.ConnectionError -> InfoDialog(
            title = "Connection Error",
            message = uiState.message,
            confirmText = "OK",
            onConfirm = onDismissDialog,
            onDismiss = onDismissDialog
        )

        is SignUpUiState.Success -> InfoDialog(
            title = "Success",
            message = uiState.message,
            confirmText = "Proceed to Login",
            onConfirm = {
                onDismissDialog()
                onLoginClick()
            },
            onDismiss = onDismissDialog
        )

        else -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(start = 8.dp, top = 8.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Start Your Journey.",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Set up your account in seconds before you hit the road.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // --- SECTION 1: PERSONAL INFORMATION ---
            SectionTitle("PERSONAL INFORMATION")

            // Full Name
            FieldLabel("Full Name")
            OutlinedTextField(
                value = fullName,
                onValueChange = {
                    fullName = sanitizeName(it)
                    fullNameTouched = true
                },
                placeholder = { Text("Enter your full name", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                isError = fullNameError != null,
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(fullNameTouched, fullNameError),
                    isError = fullNameError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(fullNameError != null, fullNameError)

            Spacer(modifier = Modifier.height(16.dp))

            // Email
            FieldLabel("Email Address")
            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = sanitizeEmail(it)
                    emailTouched = true
                },
                placeholder = { Text("Enter your Email address", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(emailTouched, emailError),
                    isError = emailError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(emailError != null, emailError)

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            FieldLabel("Phone Number")
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    phoneNumber = sanitizePhone(it)
                    phoneTouched = true
                },
                placeholder = { Text("e.g. 09123456789", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = phoneError != null,
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(phoneTouched, phoneError),
                    isError = phoneError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(phoneError != null, phoneError)

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION 2: ACCOUNT SECURITY ---
            SectionTitle("ACCOUNT SECURITY")

            // Password
            FieldLabel("Password")
            OutlinedTextField(
                value = password,
                onValueChange = {
                    password = it
                    passwordTouched = true
                },
                placeholder = { Text("Enter Password", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                isError = passwordError != null,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(passwordTouched, passwordError),
                    isError = passwordError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Live password strength indicator
            if (password.isNotEmpty()) {
                val strength = getPasswordStrength(password)
                val (strengthText, strengthColor) = when (strength) {
                    1 -> "Weak" to Color(0xFFFF5252)
                    2 -> "Medium" to Color(0xFFFFB74D)
                    3 -> "Strong" to Color(0xFF4CAF50)
                    else -> "Too Short" to Color(0xFFFF5252)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Password strength:",
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 11.sp
                        )
                        Text(
                            text = strengthText,
                            color = strengthColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        repeat(3) { index ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(
                                        if (index < strength) strengthColor else Color.White.copy(alpha = 0.15f)
                                    )
                            )
                        }
                    }
                }
            }
            FieldError(passwordError != null, passwordError)

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password
            FieldLabel("Confirm Password")
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordTouched = true
                },
                placeholder = { Text("Confirm Password", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                isError = confirmPasswordError != null,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color.White.copy(alpha = 0.6f)
                        )
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(confirmPasswordTouched, confirmPasswordError),
                    isError = confirmPasswordError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(confirmPasswordError != null, confirmPasswordError)

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION 3: EMERGENCY CONTACT ---
            SectionTitle("EMERGENCY CONTACT")

            FieldLabel("Contact Person Name")
            OutlinedTextField(
                value = emergencyName,
                onValueChange = {
                    emergencyName = sanitizeName(it)
                    emergencyNameTouched = true
                },
                placeholder = { Text("e.g. Spouse, Parent, Relative", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                isError = emergencyNameError != null,
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(emergencyNameTouched, emergencyNameError),
                    isError = emergencyNameError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(emergencyNameError != null, emergencyNameError)

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel("Contact Person Phone")
            OutlinedTextField(
                value = emergencyPhone,
                onValueChange = {
                    emergencyPhone = sanitizePhone(it)
                    emergencyPhoneTouched = true
                },
                placeholder = { Text("e.g. 09123456789", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = emergencyPhoneError != null,
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(emergencyPhoneTouched, emergencyPhoneError),
                    isError = emergencyPhoneError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(emergencyPhoneError != null, emergencyPhoneError)

            Spacer(modifier = Modifier.height(24.dp))

            // --- SECTION 4: VEHICLE DETAILS ---
            SectionTitle("VEHICLE DETAILS")

            FieldLabel("Vehicle Type")
            ExposedDropdownMenuBox(
                expanded = isVehicleDropdownExpanded,
                onExpandedChange = { isVehicleDropdownExpanded = !isVehicleDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedVehicleType,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isVehicleDropdownExpanded) },
                    shape = RoundedCornerShape(12.dp),
                    colors = appTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                )

                ExposedDropdownMenu(
                    expanded = isVehicleDropdownExpanded,
                    onDismissRequest = { isVehicleDropdownExpanded = false },
                    modifier = Modifier.background(Color(0xFF2C2C2E))
                ) {
                    vehicleTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type, color = Color.White) },
                            onClick = {
                                selectedVehicleType = type
                                isVehicleDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FieldLabel("Vehicle Plate Number")
            OutlinedTextField(
                value = plateNumber,
                onValueChange = {
                    plateNumber = sanitizePlate(it)
                    plateTouched = true
                },
                placeholder = { Text("e.g. ABC 1234", color = Color.White.copy(alpha = 0.4f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                isError = plateError != null,
                shape = RoundedCornerShape(12.dp),
                colors = appTextFieldColors(
                    unfocusedBorderColor = validBorder(plateTouched, plateError),
                    isError = plateError != null
                ),
                modifier = Modifier.fillMaxWidth()
            )
            FieldError(plateError != null, plateError)

            Spacer(modifier = Modifier.height(16.dp))

            // Terms and Privacy Checkbox
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isAgreed,
                    onCheckedChange = { isAgreed = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF0F0B94),
                        uncheckedColor = Color.White.copy(alpha = 0.6f),
                        checkmarkColor = Color.White
                    )
                )
                Text(
                    text = "I agree to the Terms & privacy policy.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Signup button
            Button(
                onClick = {
                    // Mark all fields as touched so any errors show if the form is incomplete
                    fullNameTouched = true
                    emailTouched = true
                    phoneTouched = true
                    passwordTouched = true
                    confirmPasswordTouched = true
                    emergencyNameTouched = true
                    emergencyPhoneTouched = true
                    plateTouched = true

                    if (isFormValid && isAgreed) {
                        onSignUpClick(
                            fullName.trim(),
                            email.trim(),
                            phoneNumber,
                            password,
                            confirmPassword,
                            emergencyName.trim(),
                            emergencyPhone,
                            selectedVehicleType,
                            plateNumber.trim()
                        )
                    }
                },
                enabled = isAgreed && uiState !is SignUpUiState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0F0B94),
                    disabledContainerColor = Color(0xFF0F0B94).copy(alpha = 0.5f)
                )
            ) {
                if (uiState is SignUpUiState.Loading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Signup",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider "or continue with"
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.weight(1f))
                Text(
                    text = "or continue with",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.2f), modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Google button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF2C2C2E))
                    .clickable { onGoogleClick() },
                contentAlignment = Alignment.Center
            ) {
                GoogleLogo(modifier = Modifier.size(24.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login link at the bottom
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Already have an account? ",
                    color = Color.White.copy(alpha = 0.7f),
                    fontSize = 13.sp
                )
                Text(
                    text = "Login",
                    color = Color(0xFF6C7BFF),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}