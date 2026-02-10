package com.kpri.binasejahtera.ui.navigation

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriDialog
import com.kpri.binasejahtera.ui.components.ToastManager
import com.kpri.binasejahtera.ui.components.ToastType
import com.kpri.binasejahtera.ui.screens.ChangePasswordScreen
import com.kpri.binasejahtera.ui.screens.DailyReportScreen
import com.kpri.binasejahtera.ui.screens.EditProfileScreen
import com.kpri.binasejahtera.ui.screens.LoginScreen
import com.kpri.binasejahtera.ui.screens.MainContainerScreen
import com.kpri.binasejahtera.ui.screens.PresenceConfirmationScreen
import com.kpri.binasejahtera.ui.theme.InfoGreen
import com.kpri.binasejahtera.ui.viewmodel.AttendanceViewModel
import com.kpri.binasejahtera.ui.viewmodel.AuthViewModel
import com.kpri.binasejahtera.ui.viewmodel.ProfileViewModel
import kotlinx.coroutines.launch


@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        // --- Login ---
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val credentialManager = remember { CredentialManager.create(context) }

            fun handleGoogleSignIn() {
                scope.launch {
                    try {
                        // google public clientID
                        val googleIdOption = GetGoogleIdOption.Builder()
                            .setFilterByAuthorizedAccounts(false)
                            .setServerClientId("364805871560-5sbnmaojh82j4c4hn29a64nni1f7p8vs.apps.googleusercontent.com")
                            .setAutoSelectEnabled(false)
                            .build()

                        val request = GetCredentialRequest.Builder()
                            .addCredentialOption(googleIdOption)
                            .build()

                        val result = credentialManager.getCredential(
                            request = request,
                            context = context
                        )

                        val credential = result.credential
                        if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                            try {
                                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                                val idToken = googleIdTokenCredential.idToken

                                viewModel.googleLogin(idToken)

                            } catch (e: GoogleIdTokenParsingException) {
                                ToastManager.show("Gagal parsing token Google", ToastType.ERROR)
                            }
                        } else {
                            ToastManager.show("Tipe kredensial tidak dikenali", ToastType.ERROR)
                        }

                    } catch (e: Exception) {
                        if (e !is androidx.credentials.exceptions.GetCredentialCancellationException) {
                            e.printStackTrace()
                            ToastManager.show("Google Sign In gagal: ${e.message}", ToastType.ERROR)
                        }
                    }
                }
            }

            LoginScreen(
                onLoginClick = { email, pass -> viewModel.login(email, pass) },
                onGoogleSignInClick = { handleGoogleSignIn() }
            )

            LaunchedEffect(Unit) {
                viewModel.authEvent.collect { event ->
                    when (event) {
                        is AuthViewModel.AuthEvent.Success -> {
                            ToastManager.show(event.message, ToastType.SUCCESS)

                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        is AuthViewModel.AuthEvent.Error -> {
                            ToastManager.show(event.message, ToastType.ERROR)
                        }
                    }
                }
            }
        }

        // --- Main Route (Home) ---
        composable(Screen.Home.route) {
            val authViewModel: AuthViewModel = hiltViewModel()
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val profileState by profileViewModel.profileState.collectAsState()

            val attendanceViewModel: AttendanceViewModel = hiltViewModel()
            val homeState by attendanceViewModel.homeState.collectAsState()

            val currentBackStack = navController.currentBackStackEntry
            val savedStateHandle = currentBackStack?.savedStateHandle

            val shouldRefresh by savedStateHandle?.getStateFlow("refresh_profile", false)!!.collectAsState()

            LaunchedEffect(shouldRefresh) {
                if (shouldRefresh) {
                    profileViewModel.loadProfile(forceUpdate = true)
                    attendanceViewModel.refreshData()
                    savedStateHandle.remove<Boolean>("refresh_profile")
                }
            }

            MainContainerScreen(
                onNavigate = { route ->
                    when(route) {
                        "attendance_in" -> navController.navigate("presence_confirmation/true")
                        "attendance_out" -> navController.navigate(Screen.DailyReport.route)
                        "personal_info" -> navController.navigate(Screen.EditProfile.route)
                        "change_password" -> {
                            val hasPass = profileState?.hasPassword ?: false
                            navController.navigate("change_password/$hasPass")
                        }
                    }
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                },

                profileState = profileState,

                homeState = homeState,
                onRefreshHome = { attendanceViewModel.refreshData() }
            )
        }

        // --- Daily Report (report sebelum pulang) ---
        composable(Screen.DailyReport.route) {
            val context = LocalContext.current
            val sharedViewModel: AttendanceViewModel = hiltViewModel(context as ComponentActivity)

            DailyReportScreen(
                viewModel = sharedViewModel, // Pass VM yang sama
                onNavigateBack = { navController.popBackStack() },
                onNavigateNext = {
                    navController.navigate("presence_confirmation/false")
                }
            )
        }

        // --- Presence Confirmation ---
        composable(
            route = "presence_confirmation/{status}",
            arguments = listOf(navArgument("status") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isCheckIn = backStackEntry.arguments?.getBoolean("status") ?: true

            val context = LocalContext.current
            val sharedViewModel: AttendanceViewModel = hiltViewModel(context as ComponentActivity)

            val confirmState by sharedViewModel.confirmationState.collectAsState()
            var successDialogMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                sharedViewModel.initPresenceConfirmation(isCheckIn)
            }

            LaunchedEffect(true) {
                sharedViewModel.attendanceEvent.collect { event ->
                    when (event) {
                        is AttendanceViewModel.AttendanceEvent.Success -> {
                            successDialogMessage = event.message
                        }
                        is AttendanceViewModel.AttendanceEvent.Error -> {
                            ToastManager.show(event.message, ToastType.ERROR)
                        }
                    }
                }
            }

            // dialog muncul pas ada pesannya
            if (successDialogMessage != null) {
                KpriDialog(
                    title = "Presensi Berhasil!",
                    message = successDialogMessage ?: "",
                    confirmText = "Kembali ke Beranda",
                    iconId = R.drawable.ic_check,
                    iconContainerColor = InfoGreen,
                    onConfirm = {
                        successDialogMessage = null
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )
            }

            PresenceConfirmationScreen(
                isCheckIn = isCheckIn,
                state = confirmState,
                onBackClick = { navController.popBackStack() },
                onConfirmClick = { sharedViewModel.performAttendance(isCheckIn) },
                onUpdateLocation = { sharedViewModel.refreshUserLocation() },

                onRefresh = {
                    sharedViewModel.refreshPresenceData(isCheckIn)
                }
            )
        }

        // --- Edit Profile ---
        composable(Screen.EditProfile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()
            val profileState by viewModel.profileState.collectAsState()

            LaunchedEffect(true) {
                viewModel.profileEvent.collect { event ->
                    when(event) {
                        is ProfileViewModel.ProfileEvent.Success -> {
                            ToastManager.show(event.message, ToastType.SUCCESS)

                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_profile", true)

                            navController.popBackStack()
                        }
                        is ProfileViewModel.ProfileEvent.Error -> {
                            ToastManager.show(event.message, ToastType.ERROR)
                        }
                    }
                }
            }

            EditProfileScreen(
                state = profileState,
                onNavigateBack = { navController.popBackStack() },
                onSaveProfile = { name, email, username ->
                    viewModel.updateProfile(name, email, username)
                },
                onUploadPhoto = { photoPart ->
                    viewModel.uploadPhoto(photoPart)
                }
            )
        }

        // --- Change Password ---
        composable(
            route = "change_password/{hasPassword}",
            arguments = listOf(navArgument("hasPassword") { type = NavType.BoolType })
        ) { backStackEntry ->
            val hasPass = backStackEntry.arguments?.getBoolean("hasPassword") ?: false
            val viewModel: AuthViewModel = hiltViewModel()

            LaunchedEffect(true) {
                viewModel.authEvent.collect { event ->
                    when(event) {
                        is AuthViewModel.AuthEvent.Success -> {
                            ToastManager.show(event.message, ToastType.SUCCESS)

                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("refresh_profile", true)

                            navController.popBackStack()
                        }
                        is AuthViewModel.AuthEvent.Error -> {
                            ToastManager.show(event.message, ToastType.ERROR)
                        }
                    }
                }
            }

            ChangePasswordScreen(
                hasPassword = hasPass,
                onNavigateBack = { navController.popBackStack() },
                onSavePassword = { current, new, confirm ->
                    viewModel.changePassword(current, new, confirm)
                }
            )
        }
    }
}