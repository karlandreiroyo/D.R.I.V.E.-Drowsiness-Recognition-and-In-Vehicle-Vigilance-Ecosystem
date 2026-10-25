package com.example.d_r_i_v_e.frontend

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.d_r_i_v_e.TripEntry
import com.example.d_r_i_v_e.Incident
import com.example.d_r_i_v_e.backend.AuthViewModel

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

    // ===== Google sign-in + custom account chooser =====
    var selectedGoogleAccount by remember { mutableStateOf<Pair<String, String>?>(null) }
    var knownAccounts by remember { mutableStateOf(listOf<GoogleAccountItem>()) }
    var showAccountChooser by remember { mutableStateOf(false) }

    val launchRealGoogleSignIn = rememberGoogleSignIn { name, email ->
        if (knownAccounts.none { it.email == email }) {
            knownAccounts = knownAccounts + GoogleAccountItem(name, email)
        }
        selectedGoogleAccount = Pair(name, email)
        showAccountChooser = false
        navigateTo("driverRegistration")
    }

    // Ito ang tatawagin ng mga Google button
    val startGoogleSignIn: () -> Unit = {
        if (knownAccounts.isEmpty()) launchRealGoogleSignIn()
        else showAccountChooser = true
    }
    // ===================================================

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

    val authViewModel: AuthViewModel = viewModel()
    val signUpState by authViewModel.signUpState.collectAsState()

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
            signUpState = signUpState,
            onBackClick = { goBack() },
            onSignUpClick = { fullName, email, phoneNumber, emergencyName, emergencyPhone, vehicleType, plateNumber, password ->
                // Pinalitan ng authViewModel.signUp para tumugma sa inilagay natin sa AuthViewModel.kt
                authViewModel.signUp(
                    fullName = fullName,
                    email = email,
                    phoneNumber = phoneNumber,
                    emergencyName = emergencyName,
                    emergencyPhone = emergencyPhone,
                    vehicleType = vehicleType,
                    plateNumber = plateNumber,
                    password = password
                )
            },
            onLoginClick = { navigateTo("login") },
            onResetState = { authViewModel.dismissDialog() }
        )

        "login" -> LoginScreen(
            authViewModel = authViewModel,
            onBackClick = { goBack() },
            onLoginSuccess = { navigateTo("dashboard") },
            onForgotPasswordClick = { navigateTo("forgotPassword") },
            onGoogleClick = { startGoogleSignIn() },
            onSignUpClick = { navigateTo("signUp") }
        )

        "driverRegistration" -> DriverRegistrationScreen(
            googleName = selectedGoogleAccount?.first ?: "",
            googleEmail = selectedGoogleAccount?.second ?: "",
            signUpState = signUpState,
            onBackClick = { goBack() },
            onCompleteRegistration = { profile ->
                authViewModel.registerDriver(profile)
            },
            onNavigateToLogin = {
                navigateTo("login")
            },
            onResetState = {
                authViewModel.dismissDialog()
            }
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
            onStartCalibrationClick = { },
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
            onRestoreEntry = { id -> },
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
            onFaqClick = { },
            onReportIncidentClick = { navigateTo("reportIncident") },
            onSuggestFeatureClick = { navigateTo("suggestFeature") },
            onWhatSystemDetectsClick = { navigateTo("whatSystemDetects") },
            onCameraPrivacyPolicyClick = { },
            onPrivacyPolicyClick = { },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "contactSupport" -> ContactSupportScreen(
            onBackClick = { goBack() },
            onSubmitMessage = { category, message -> },
            onAttachFileClick = { },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "reportIncident" -> ReportIncidentScreen(
            onBackClick = { goBack() },
            onSubmitReport = { incidentType, description -> },
            onAttachFileClick = { },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "suggestFeature" -> SuggestFeatureScreen(
            onBackClick = { goBack() },
            onSubmitIdea = { featureTitle, description -> },
            onAttachFileClick = { },
            onTabDashboardClick = { navigateTo("dashboard") },
            onTabCameraClick = { navigateTo("liveFeed") },
            onTabMapClick = { navigateTo("tripHistory") },
            onTabSettingsClick = { navigateTo("settings") }
        )

        "whatSystemDetects" -> WhatSystemDetectsScreen(
            onBackClick = { goBack() }
        )
    }

    // Custom Google-style account chooser
    if (showAccountChooser) {
        AccountChooserDialog(
            accounts = knownAccounts,
            onAccountClick = { account ->
                selectedGoogleAccount = Pair(account.name, account.email)
                showAccountChooser = false
                navigateTo("driverRegistration")
            },
            onAddAccountClick = {
                showAccountChooser = false
                launchRealGoogleSignIn()
            },
            onDismiss = { showAccountChooser = false }
        )
    }
}

fun maskDestination(input: String): String {
    if (input.length <= 2) return input
    val visiblePart = input.take(2)
    val maskedPart = "*".repeat(input.length - 2)
    return "$visiblePart$maskedPart"
}