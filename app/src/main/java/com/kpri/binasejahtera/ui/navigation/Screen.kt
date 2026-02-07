package com.kpri.binasejahtera.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object Presence : Screen("presence")
    object DailyReport : Screen("daily_report")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object ChangePassword : Screen("change_password")

    // route dengan argumen untuk konfirmasi masuk/pulang
    object PresenceConfirmation : Screen("presence_confirmation/{status}") {
        fun createRoute(isCheckIn: Boolean): String {
            return "presence_confirmation/$isCheckIn"
        }
    }
}