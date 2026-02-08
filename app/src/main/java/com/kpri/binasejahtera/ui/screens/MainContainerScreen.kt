package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
import com.kpri.binasejahtera.ui.components.KpriBottomNavigation
import com.kpri.binasejahtera.ui.theme.AppBackground
import kotlinx.coroutines.launch

@Composable
fun MainContainerScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    profileState: ProfileResponse?
) {
    /* implementasi carrousel hehe
     * - 0 = home
     * - 1 = presence
     * - 2 = profile
     */
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    // state untuk BottomNavigation
    var currentRoute by remember { mutableStateOf("home") }

    LaunchedEffect(
        pagerState.currentPage
    ) {
       currentRoute = when (pagerState.currentPage) {
           0 -> "home"
           1 -> "presence"
           2 -> "profile"
           else -> "home"
       }
    }

    Scaffold(
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0.dp)
    ) {
        innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // layer 1 (konten utama)
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) {
                page ->
                when (page) {
                    0 -> HomeScreen(
                        onNavigate = onNavigate,
                        isNested = true
                    )

                    1 -> PresenceScreen(
                        onNavigate = onNavigate,
                        isNested = true
                    )

                    2 -> ProfileScreen(
                        state = profileState,
                        onNavigate = onNavigate,
                        onLogout = onLogout,
                        isNested = true
                    )
                }
            }

            // layer 2 (BottomNavigation nya cuy)
            KpriBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = {
                    route ->
                    val targerPage = when(route) {
                        "home" -> 0
                        "presence" -> 1
                        "profile" -> 2
                        else -> 0
                    }
                    scope.launch {
                        pagerState.animateScrollToPage(targerPage)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            )
        }
    }


}
