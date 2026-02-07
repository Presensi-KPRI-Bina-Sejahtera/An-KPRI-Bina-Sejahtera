package com.kpri.binasejahtera.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
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
import com.kpri.binasejahtera.ui.viewmodel.AttendanceViewModel
import com.kpri.binasejahtera.ui.viewmodel.AuthViewModel
import com.kpri.binasejahtera.ui.viewmodel.ProfileViewModel
import com.kpri.binasejahtera.ui.viewmodel.ReportViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,

        enterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
        },
        exitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, tween(400))
        },
        popEnterTransition = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400))
        },
        popExitTransition = {
            slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, tween(400))
        }
    ) {
        // --- Login ---
        composable(Screen.Login.route) {
            val viewModel: AuthViewModel = hiltViewModel()
            val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

            LaunchedEffect(isUserLoggedIn) {
                if (isUserLoggedIn) {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            }

            LaunchedEffect(true) {
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

            LoginScreen(
                onLoginClick = { email, pass -> viewModel.login(email, pass) },
                onGoogleSignInClick = { /* TODO: Implement Google Sign In Launch */ }
            )
        }

        // --- Home ---
        composable(
            Screen.Home.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) }
        ) {
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
        composable(
            Screen.Presence.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) }
        ) {
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

        // --- Profile ---
        composable(
            Screen.Profile.route,
            enterTransition = { fadeIn(tween(300)) },
            exitTransition = { fadeOut(tween(300)) },
            popEnterTransition = { fadeIn(tween(300)) },
            popExitTransition = { fadeOut(tween(300)) }
        ) {
            val viewModel: AuthViewModel = hiltViewModel()

            LaunchedEffect(true) {
                viewModel.authEvent.collect { event ->
                    if (event is AuthViewModel.AuthEvent.Success && event.message.contains("Logout")) {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }

            ProfileScreen(
                onNavigate = { route ->
                    when (route) {
                        "login" -> viewModel.logout()
                        "personal_info" -> navController.navigate(Screen.EditProfile.route)
                        "change_password" -> navController.navigate(Screen.ChangePassword.route)
                        else -> navController.navigate(route)
                    }
                }
            )
        }

        // --- Daily Report (report sebelum pulang) ---
        composable(Screen.DailyReport.route) {
            val viewModel: ReportViewModel = hiltViewModel()

            LaunchedEffect(true) {
                viewModel.reportEvent.collect { event ->
                    if (event is ReportViewModel.ReportEvent.Success) {
                        ToastManager.show(event.message, ToastType.SUCCESS)
                        navController.navigate(Screen.PresenceConfirmation.createRoute(false))
                    } else if (event is ReportViewModel.ReportEvent.Error) {
                        ToastManager.show(event.message, ToastType.ERROR)
                    }
                }
            }

            DailyReportScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateNext = {
                    // Sementara hardcode dummy submit biar bisa navigasi
                    // Nanti ambil value asli dari Screen State
                    viewModel.submitReport("0", "0", emptyList())
                }
            )
        }

        // --- Presence Confirmation ---
        composable(
            route = Screen.PresenceConfirmation.route,
            arguments = listOf(navArgument("status") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isCheckIn = backStackEntry.arguments?.getBoolean("status") ?: true
            val viewModel: AttendanceViewModel = hiltViewModel()

            // Setup data dummy sementara agar tidak error saat render
            // Nanti ini diganti dengan data Live dari ViewModel
            val dummyState = com.kpri.binasejahtera.ui.screens.PresenceLocationState(
                time = "00:00:00", date = "-", locationName = "Memuat...",
                address = "-", radius = "0m", distance = "0m", isDistanceSafe = false
            )

            LaunchedEffect(true) {
                viewModel.attendanceEvent.collect { event ->
                    if (event is AttendanceViewModel.AttendanceEvent.Success) {
                        ToastManager.show(event.message, ToastType.SUCCESS)
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    } else if (event is AttendanceViewModel.AttendanceEvent.Error) {
                        ToastManager.show(event.message, ToastType.ERROR)
                    }
                }
            }

            PresenceConfirmationScreen(
                isCheckIn = isCheckIn,
                state = dummyState, // TODO: Bind ke viewModel.state
                onBackClick = { navController.popBackStack() },
                onConfirmClick = {
                    // Panggil API CheckIn / CheckOut
                    // Dummy LatLong sementara
                    if (isCheckIn) viewModel.checkIn(-6.2, 106.8, "Lokasi Dummy")
                    else viewModel.checkOut(-6.2, 106.8, "Lokasi Dummy")
                }
            )
        }

        // --- Edit Profile ---
        composable(Screen.EditProfile.route) {
            val viewModel: ProfileViewModel = hiltViewModel()

            EditProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // --- Change Password ---
        composable(Screen.ChangePassword.route) {
            val viewModel: AuthViewModel = hiltViewModel()

            ChangePasswordScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}