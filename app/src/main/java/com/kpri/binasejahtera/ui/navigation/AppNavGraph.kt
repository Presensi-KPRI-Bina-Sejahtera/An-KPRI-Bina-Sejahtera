package com.kpri.binasejahtera.ui.navigation

import android.app.Activity
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
import com.google.gson.Gson
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.data.remote.dto.DepositItemDto
import com.kpri.binasejahtera.ui.components.KpriDialog
import com.kpri.binasejahtera.ui.components.ToastManager
import com.kpri.binasejahtera.ui.components.ToastType
import com.kpri.binasejahtera.ui.screens.ChangePasswordScreen
import com.kpri.binasejahtera.ui.screens.DailyReportScreen
import com.kpri.binasejahtera.ui.screens.EditProfileScreen
import com.kpri.binasejahtera.ui.screens.HomeScreen
import com.kpri.binasejahtera.ui.screens.LoginScreen
import com.kpri.binasejahtera.ui.screens.PresenceConfirmationScreen
import com.kpri.binasejahtera.ui.screens.PresenceScreen
import com.kpri.binasejahtera.ui.screens.ProfileScreen
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
            val state by viewModel.isLoading.collectAsState()
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

        // --- Home ---
        composable(Screen.Home.route) {
            val viewModel: AttendanceViewModel = hiltViewModel()
            // Nanti bind data home dari viewModel disini (homeData.collectAsState)

            HomeScreen(
                onNavigate = { route ->
                    when (route) {
                        "attendance_in" -> navController.navigate(Screen.PresenceConfirmation.createRoute(true))
                        "attendance_out" -> navController.navigate(Screen.DailyReport.route)
                        else -> navController.navigate(route)
                    }
                }
            )
        }

        // --- Presence Selection (BottomNavigation) ---
        composable(Screen.Presence.route) {
            PresenceScreen(
                onNavigate = { route ->
                    when (route) {
                        "attendance_in" -> navController.navigate(Screen.PresenceConfirmation.createRoute(true))
                        "attendance_out" -> navController.navigate(Screen.DailyReport.route)
                        else -> navController.navigate(route)
                    }
                }
            )
        }

        // --- Daily Report (report sebelum pulang) ---
        composable(Screen.DailyReport.route) {

            DailyReportScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateNext = { pemasukan, pengeluaran, deposits ->
                    val gson = Gson()
                    val depositsJson = gson.toJson(deposits)

                    val encodedDeposits = java.net.URLEncoder.encode(depositsJson, "UTF-8")

                    navController.navigate(
                        "presence_confirmation/false?pemasukan=$pemasukan&pengeluaran=$pengeluaran&deposits=$encodedDeposits"
                    )
                }
            )
        }

        // --- Presence Confirmation ---
        composable(
            route = "presence_confirmation/{status}?pemasukan={pemasukan}&pengeluaran={pengeluaran}&deposits={deposits}",
            arguments = listOf(
                navArgument("status") { type = NavType.BoolType },
                navArgument("pemasukan") { type = NavType.StringType; nullable = true },
                navArgument("pengeluaran") { type = NavType.StringType; nullable = true },
                navArgument("deposits") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            val isCheckIn = backStackEntry.arguments?.getBoolean("status") ?: true

            val pemasukan = backStackEntry.arguments?.getString("pemasukan")
            val pengeluaran = backStackEntry.arguments?.getString("pengeluaran")
            val depositsRaw = backStackEntry.arguments?.getString("deposits")

            val viewModel: AttendanceViewModel = hiltViewModel()
            val confirmState by viewModel.confirmationState.collectAsState()

            // state untuk nyimpen pesan sukses
            var successDialogMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                if (!isCheckIn && pemasukan != null && pengeluaran != null) {
                    try {
                        val gson = Gson()
                        val depositsList = if (depositsRaw != null) {
                            gson.fromJson(depositsRaw, Array<DepositItemDto>::class.java).toList()
                        } else emptyList()

                        viewModel.setPendingReport(pemasukan, pengeluaran, depositsList)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                viewModel.initPresenceConfirmation(isCheckIn)
            }

            LaunchedEffect(true) {
                viewModel.attendanceEvent.collect { event ->
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
                onConfirmClick = {
                    viewModel.performAttendance(isCheckIn)
                }
            )
        }

        // --- Profile ---
        composable(Screen.Profile.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val profileViewModel: ProfileViewModel = hiltViewModel()
            val profileState by profileViewModel.profileState.collectAsState()
            val context = LocalContext.current

            LaunchedEffect(Unit) {
                profileViewModel.loadProfile()
            }

            LaunchedEffect(true) {
                viewModel.authEvent.collect { event ->
                    if (event is AuthViewModel.AuthEvent.Success) {
                        if (event.message.contains("Logout", ignoreCase = true) || event.message.contains("Keluar", ignoreCase = true)) {
                            (context as? Activity)?.finishAffinity()
                        } else {
                            // Kalau sukses update profil/password biasa
                            ToastManager.show(event.message, ToastType.SUCCESS)
                        }
                    } else if (event is AuthViewModel.AuthEvent.Error) {
                        ToastManager.show(event.message, ToastType.ERROR)
                    }
                }
            }

            ProfileScreen(
                state = profileState,
                onNavigate = { route ->
                    when (route) {
                        "personal_info" -> navController.navigate(Screen.EditProfile.route)
                        "change_password" -> navController.navigate(Screen.ChangePassword.route)
                        else -> navController.navigate(route)
                    }
                },
                onLogout = {
                    viewModel.logout()
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
        composable(Screen.ChangePassword.route) {
            val viewModel: AuthViewModel = hiltViewModel()

            LaunchedEffect(true) {
                viewModel.authEvent.collect { event ->
                    when(event) {
                        is AuthViewModel.AuthEvent.Success -> {
                            ToastManager.show(event.message, ToastType.SUCCESS)
                            navController.popBackStack()
                        }
                        is AuthViewModel.AuthEvent.Error -> {
                            ToastManager.show(event.message, ToastType.ERROR)
                        }
                    }
                }
            }

            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() },
                onSavePassword = { current, new, confirm ->
                    viewModel.changePassword(current, new, confirm)
                }
            )
        }
    }
}