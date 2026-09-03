package com.example.d_r_i_v_e.frontend

import androidx.compose.runtime.*
import com.example.d_r_i_v_e.TripEntry
import com.example.d_r_i_v_e.Incident

@Composable
fun AppNavigation() {
    var currentScreen by remember { mutableStateOf("onboarding") }
    var otpDestination by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }
    var isHardwareConnected by remember { mutableStateOf(false) }

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

    var archiveEntries by remember {
        mutableStateOf(
            listOf(
                ArchiveEntry("a1", "Drive session", "Aug 13 · 42 mins · 2 alerts", "drive", isAlertType = false),
                ArchiveEntry("a2", "Drowsiness alert", "Aug 13 · 5:42 AM", "alert", isAlertType = true),
                ArchiveEntry("a3", "Drowsiness alert", "Aug 13 · 5:01 AM", "alert", isAlertType = true),
                ArchiveEntry("a4", "Drive session", "Yesterday · 1 hr · 1 alert", "drive", isAlertType = false)
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
            isHardwareConnected = isHardwareConnected,
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
            onAccountProfileClick = { currentScreen = "accountProfile" },
            onCalibrateDeviceClick = { currentScreen = "calibrateDevice" },
            onConfigureBuzzerClick = { currentScreen = "configureBuzzer" },
            onArchiveClick = { currentScreen = "archive" },
            onHelpSupportClick = { currentScreen = "helpSupport" },
            onTabDashboardClick = { currentScreen = "dashboard" },
            onTabCameraClick = { currentScreen = "liveFeed" },
            onTabMapClick = { currentScreen = "tripHistory" },
            onTabSettingsClick = { currentScreen = "settings" }
        )

        "accountProfile" -> AccountProfileScreen(
            onBackClick = { currentScreen = "settings" },
            onTabDashboardClick = { currentScreen = "dashboard" },
            onTabCameraClick = { currentScreen = "liveFeed" },
            onTabMapClick = { currentScreen = "tripHistory" },
            onTabSettingsClick = { currentScreen = "settings" }
        )

        "calibrateDevice" -> CalibrateDeviceScreen(
            isHardwareConnected = isHardwareConnected,
            onBackClick = { currentScreen = "settings" },
            onStartCalibrationClick = {
                // TODO: actual calibration logic
            },
            onTabDashboardClick = { currentScreen = "dashboard" },
            onTabCameraClick = { currentScreen = "liveFeed" },
            onTabMapClick = { currentScreen = "tripHistory" },
            onTabSettingsClick = { currentScreen = "settings" }
        )

        "configureBuzzer" -> ConfigureBuzzerScreen(
            onBackClick = { currentScreen = "settings" },
            onTabDashboardClick = { currentScreen = "dashboard" },
            onTabCameraClick = { currentScreen = "liveFeed" },
            onTabMapClick = { currentScreen = "tripHistory" },
            onTabSettingsClick = { currentScreen = "settings" }
        )

        "archive" -> ArchiveScreen(
            entries = archiveEntries,
            onRestoreEntry = { id ->
                // TODO: actual restore logic
            },
            onDeleteEntry = { id ->
                archiveEntries = archiveEntries.filter { it.id != id }
            },
            onBackClick = { currentScreen = "settings" },
            onTabDashboardClick = { currentScreen = "dashboard" },
            onTabCameraClick = { currentScreen = "liveFeed" },
            onTabMapClick = { currentScreen = "tripHistory" },
            onTabSettingsClick = { currentScreen = "settings" }
        )

        "helpSupport" -> HelpSupportScreen(
            onBackClick = { currentScreen = "settings" },
            onTabDashboardClick = { currentScreen = "dashboard" },
            onTabCameraClick = { currentScreen = "liveFeed" },
            onTabMapClick = { currentScreen = "tripHistory" },
            onTabSettingsClick = { currentScreen = "settings" }
        )
    }
}

fun maskDestination(input: String): String {
    if (input.length <= 2) return input
    val visiblePart = input.take(2)
    val maskedPart = "*".repeat(input.length - 2)
    return "$visiblePart$maskedPart"
}