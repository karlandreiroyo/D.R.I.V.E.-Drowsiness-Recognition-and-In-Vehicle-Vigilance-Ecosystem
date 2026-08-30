package com.example.d_r_i_v_e

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
import com.example.d_r_i_v_e.ui.theme.D_R_I_V_ETheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
        )

        setContent {
            D_R_I_V_ETheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->

                    var currentScreen by remember { mutableStateOf("onboarding") }
                    var otpDestination by remember { mutableStateOf("") }
                    var isOnline by remember { mutableStateOf(false) }

                    var trips by remember {
                        mutableStateOf(
                            listOf(
                                TripEntry(
                                    id = "1",
                                    dateLabel = "Today, 7:45 AM",
                                    totalDuration = "45 mins total driving",
                                    incidents = listOf(
                                        Incident("Micro-sleep Detected", "9:12 AM")
                                    )
                                ),
                                TripEntry(
                                    id = "2",
                                    dateLabel = "Yesterday, 6:07 PM",
                                    totalDuration = "1 hr 15 mins total driving"
                                )
                            )
                        )
                    }

                    when (currentScreen) {
                        "onboarding" -> OnboardingScreen(
                            onFinished = { currentScreen = "auth" },
                            onLoginClick = { currentScreen = "auth" }
                        )

                        "auth" -> AuthScreen(
                            onSignUpClick = { currentScreen = "signUp" },
                            onLogInClick = { currentScreen = "login" }
                        )

                        "signUp" -> SignUpScreen(
                            onBackClick = { currentScreen = "auth" },
                            onSignUpClick = { fullName, email, password -> },
                            onGoogleClick = { },
                            onLoginClick = { currentScreen = "login" }
                        )

                        "login" -> LoginScreen(
                            onBackClick = { currentScreen = "auth" },
                            onLoginClick = { email, password ->
                                currentScreen = "dashboard"
                            },
                            onForgotPasswordClick = { currentScreen = "forgotPassword" },
                            onGoogleClick = { },
                            onSignUpClick = { currentScreen = "signUp" }
                        )

                        "forgotPassword" -> ForgotPasswordScreen(
                            onBackClick = { currentScreen = "login" },
                            onSendCodeClick = { emailOrPhone ->
                                otpDestination = maskDestination(emailOrPhone)
                                currentScreen = "otp"
                            },
                            onLoginClick = { currentScreen = "login" }
                        )

                        "otp" -> OtpVerificationScreen(
                            maskedDestination = otpDestination,
                            onBackClick = { currentScreen = "forgotPassword" },
                            onVerifyClick = { otp ->
                                currentScreen = "createPassword"
                            },
                            onResendClick = { }
                        )

                        "createPassword" -> CreateNewPasswordScreen(
                            onBackClick = { currentScreen = "otp" },
                            onUpdatePasswordClick = { newPassword, confirmPassword ->
                                currentScreen = "auth"
                            }
                        )

                        "dashboard" -> DashboardScreen(
                            isHardwareConnected = false,
                            isOnline = isOnline,
                            onStartDrivingClick = {
                                isOnline = true
                            },
                            onTabDashboardClick = { currentScreen = "dashboard" },
                            onTabCameraClick = { currentScreen = "liveFeed" },
                            onTabMapClick = { currentScreen = "tripHistory" },
                            onTabSettingsClick = { currentScreen = "settings" }
                        )

                        "liveFeed" -> LiveFeedScreen(
                            onTabDashboardClick = { currentScreen = "dashboard" },
                            onTabCameraClick = { currentScreen = "liveFeed" },
                            onTabMapClick = { currentScreen = "tripHistory" },
                            onTabSettingsClick = { currentScreen = "settings" }
                        )

                        "tripHistory" -> TripHistoryScreen(
                            trips = trips,
                            onDeleteTrip = { id ->
                                trips = trips.filter { it.id != id }
                            },
                            onTabDashboardClick = { currentScreen = "dashboard" },
                            onTabCameraClick = { currentScreen = "liveFeed" },
                            onTabMapClick = { currentScreen = "tripHistory" },
                            onTabSettingsClick = { currentScreen = "settings" }
                        )

                        "settings" -> SettingsScreen(
                            onTabDashboardClick = { currentScreen = "dashboard" },
                            onTabCameraClick = { currentScreen = "liveFeed" },
                            onTabMapClick = { currentScreen = "tripHistory" },
                            onTabSettingsClick = { currentScreen = "settings" }
                        )
                    }
                }
            }
        }
    }
}

fun maskDestination(input: String): String {
    if (input.length <= 2) return input
    val visiblePart = input.take(2)
    val maskedPart = "*".repeat(input.length - 2)
    return "$visiblePart$maskedPart"
}