package com.example.vigil

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.vigil.ui.theme.VigilTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            VigilTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->

                    // Available screens: "onboarding", "auth", "login", "signup"
                    var currentScreen by remember { mutableStateOf("onboarding") }

                    when (currentScreen) {
                        "onboarding" -> OnboardingScreen(
                            onFinished = { currentScreen = "auth" },
                            onLoginClick = { currentScreen = "auth" }
                        )

                        "auth" -> AuthScreen(
                            onSignUpClick = { currentScreen = "signup" },
                            onLogInClick = { currentScreen = "login" }
                        )

                        "login" -> LoginScreen(
                            onBackClick = { currentScreen = "auth" },
                            onLoginClick = { email, password ->
                                // TODO: actual login logic (validate, call API, etc.)
                            },
                            onForgotPasswordClick = {
                                // TODO: navigate to forgot password screen
                            },
                            onGoogleClick = {
                                // TODO: Google sign-in logic
                            },
                            onSignUpClick = { currentScreen = "signup" }
                        )

                        "signup" -> SignUpScreen(
                            onBackClick = { currentScreen = "auth" },
                            onSignUpClick = { fullName, email, password ->
                                // TODO: actual signup logic (validate, API call, etc.)
                            },
                            onGoogleClick = {
                                // TODO: Google sign-in logic
                            },
                            onLoginClick = { currentScreen = "login" }
                        )
                    }
                }
            }
        }
    }
}