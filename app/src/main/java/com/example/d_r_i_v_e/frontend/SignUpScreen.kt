package com.example.d_r_i_v_e.frontend

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.d_r_i_v_e.backend.SignUpUiState

@Composable
fun SignUpScreen(
    uiState: SignUpUiState = SignUpUiState.Idle,
    onBackClick: () -> Unit,
    onSignUpClick: (fullName: String, email: String, password: String, confirmPassword: String) -> Unit,
    onDismissDialog: () -> Unit = {},
    onGoogleClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isAgreed by remember { mutableStateOf(false) }

    // Alert Dialog for Backend Validation Errors / Connection Errors / Success Messages
    when (uiState) {
        is SignUpUiState.ValidationError -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = {
                    Text(
                        text = "Validation Error",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = uiState.message,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                },
                confirmButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("OK", color = Color(0xFF6C7BFF), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF2C2C2E),
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
        is SignUpUiState.ConnectionError -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = {
                    Text(
                        text = "Connection Error",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = uiState.message,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                },
                confirmButton = {
                    TextButton(onClick = onDismissDialog) {
                        Text("OK", color = Color(0xFF6C7BFF), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF2C2C2E),
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
        is SignUpUiState.Success -> {
            AlertDialog(
                onDismissRequest = onDismissDialog,
                title = {
                    Text(
                        text = "Success",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                text = {
                    Text(
                        text = uiState.message,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            onDismissDialog()
                            onLoginClick()
                        }
                    ) {
                        Text("Proceed to Login", color = Color(0xFF6C7BFF), fontWeight = FontWeight.Bold)
                    }
                },
                containerColor = Color(0xFF2C2C2E),
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
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
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Title Header
            Text(
                text = "Start Your Journey.",
                color = Color.White,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Subtitle
            Text(
                text = "Set up your account in seconds before you hit the road.",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Full Name field
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                placeholder = { Text("Enter your full name", color = Color.White.copy(alpha = 0.5f)) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email or Phone field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your Email or Phone", color = Color.White.copy(alpha = 0.5f)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter Password", color = Color.White.copy(alpha = 0.5f)) },
                singleLine = true,
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirm Password", color = Color.White.copy(alpha = 0.5f)) },
                singleLine = true,
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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2C2C2E),
                    unfocusedContainerColor = Color(0xFF2C2C2E),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = Color.White
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

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
                onClick = { onSignUpClick(fullName, email, password, confirmPassword) },
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
                GoogleLogo(
                    modifier = Modifier.size(24.dp)
                )
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
