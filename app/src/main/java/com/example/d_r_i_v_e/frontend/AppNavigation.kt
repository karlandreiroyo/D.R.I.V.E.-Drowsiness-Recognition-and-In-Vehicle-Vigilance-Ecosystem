package com.example.d_r_i_v_e.frontend

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import com.example.d_r_i_v_e.TripEntry
import com.example.d_r_i_v_e.Incident

@Composable
fun AppNavigation() {
    var screenHistory by remember { mutableStateOf(listOf("onboarding")) }
    val currentScreen = screenHistory.last()

    fun navigateTo(screen: String) {
        screenHistory = screenHistory + screen
    }

    fun goBack() {
        if (screenHistory.size > 1) {
            screenHistory = screenHistory.dropLast(1)
        }
    }

    BackHandler(enabled = screenHistory.size > 1) {
        goBack()
    }

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
            onFinished = { navigateTo("auth") },
            onLoginClick = { navigateTo("auth") }
        )

        "auth" -> AuthScreen(
            onSignUpClick = { navigateTo("signUp") },
            onLogInClick = { navigateTo("login") }
        )

        "signUp" -> SignUpScreen(
            onBackClick = { goBack() },
            onSignUpClick = { fullName, email, password -> },
            onGoogleClick = { },
            onLoginClick = { navigateTo("login") }
        )

        "login" -> LoginScreen(
            onBackClick = { goBack() },
            onLoginClick = { email, password ->
                navigateTo("dashboard")
            },
            onForgotPasswordClick = { navigateTo("forgotPassword") },
            onGoogleClick = { },
            onSignUpClick = { navigateTo("signUp") }
        )

        "forgotPassword" -> ForgotPasswordScreen(
            onBackClick = { goBack() },
            onSendCodeClick = { emailOrPhone ->
                otpDestination = maskDestination(emailOrPhone)
                navigateTo("otp")
            },
            onLoginClick = { goBack() }
        )

        "otp" -> OtpVerificationScreen(
            maskedDestination = otpDestination,
            onBackClick = { goBack() },
            onVerifyClick = { otp ->
                navigateTo("createPassword")
            },
            onResendClick = { }
        )

        "createPassword" -> CreateNewPasswordScreen(
            onBackClick = { goBack() },
            onUpdatePasswordClick = { newPassword, confirmPassword ->
                navigateTo("auth")
            }
        )

        "dashboard" -> DashboardScreen(
            isHardwareConnected = isHardwareConnected,
            isOnline = isOnline,
            onStartDrivingClick = {
                isOnline = true
            },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "liveFeed" -> LiveFeedScreen(
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "tripHistory" -> TripHistoryScreen(
            trips = trips,
            onDeleteTrip = { id ->
                trips = trips.filter { it.id != id }
            },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "settings" -> SettingsScreen(
            onAccountProfileClick = { navigateTo("accountProfile") },
            onCalibrateDeviceClick = { navigateTo("calibrateDevice") },
            onConfigureBuzzerClick = { navigateTo("configureBuzzer") },
            onArchiveClick = { navigateTo("archive") },
            onHelpSupportClick = { navigateTo("helpSupport") },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "accountProfile" -> AccountProfileScreen(
            onBackClick = { goBack() },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "calibrateDevice" -> CalibrateDeviceScreen(
            isHardwareConnected = isHardwareConnected,
            onBackClick = { goBack() },
            onStartCalibrationClick = {
                // TODO: actual calibration logic
            },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "configureBuzzer" -> ConfigureBuzzerScreen(
            onBackClick = { goBack() },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "archive" -> ArchiveScreen(
            entries = archiveEntries,
            onRestoreEntry = { id ->
                // TODO: actual restore logic
            },
            onDeleteEntry = { id ->
                archiveEntries = archiveEntries.filter { it.id != id }
            },
            onBackClick = { goBack() },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "helpSupport" -> HelpSupportScreen(
            onBackClick = { goBack() },
            onContactSupportClick = { navigateTo("contactSupport") },
            onFaqClick = { /* TODO */ },
            onReportIncidentClick = { navigateTo("reportIncident") },
            onSuggestFeatureClick = { navigateTo("suggestFeature") },
            onWhatSystemDetectsClick = { navigateTo("whatSystemDetects") },
            onCameraPrivacyPolicyClick = { /* TODO */ },
            onPrivacyPolicyClick = { /* TODO */ },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "contactSupport" -> ContactSupportScreen(
            onBackClick = { goBack() },
            onSubmitMessage = { category, message ->
                // TODO: actual submit logic (call API, save to database, etc.)
            },
            onAttachFileClick = {
                // TODO: file picker logic
            },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "reportIncident" -> ReportIncidentScreen(
            onBackClick = { goBack() },
            onSubmitReport = { incidentType, description ->
                // TODO: actual submit logic (call API, save to database, etc.)
            },
            onAttachFileClick = {
                // TODO: file picker logic
            },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "suggestFeature" -> SuggestFeatureScreen(
            onBackClick = { goBack() },
            onSubmitIdea = { featureTitle, description ->
                // TODO: actual submit logic (call API, save to database, etc.)
            },
            onAttachFileClick = {
                // TODO: file picker logic
            },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "whatSystemDetects" -> WhatSystemDetectsScreen(
            onBackClick = { goBack() }
        )
    }
}

fun maskDestination(input: String): String {
    if (input.length <= 2) return input
    val visiblePart = input.take(2)
    val maskedPart = "*".repeat(input.length - 2)
    return "$visiblePart$maskedPart"
}