package com.kpri.binasejahtera.ui.screens

import androidx.compose.animation.core.tween
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
import com.kpri.binasejahtera.ui.components.KpriBottomNavigation
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.viewmodel.HomeUiState
import kotlinx.coroutines.launch

@Composable
fun MainContainerScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    profileState: ProfileResponse?,
    homeState: HomeUiState,
    onRefreshHome: () -> Unit
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

    val isTargetPresence = pagerState.targetPage == 1

    val targetColor = if (isTargetPresence) PrimaryBlack else Color.White
    if (isTargetPresence) Color.White else PrimaryBlack

    LaunchedEffect(pagerState.currentPage, pagerState.targetPage) {
        currentRoute = when (pagerState.targetPage) {
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
                modifier = Modifier.fillMaxSize(),
                beyondViewportPageCount = 2
            ) {
                page ->
                when (page) {
                    0 -> HomeScreen(
                        state = homeState,
                        onNavigate = onNavigate,
                        onRefresh = onRefreshHome
                    )

                    1 -> PresenceScreen(
                        onNavigate = onNavigate
                    )

                    2 -> ProfileScreen(
                        state = profileState,
                        onNavigate = onNavigate,
                        onLogout = onLogout
                    )
                }
            }

            // layer 2 (BottomNavigation nya cuy)
            KpriBottomNavigation(
                currentRoute = currentRoute,
                onNavigate = {
                    route ->
                    val targetPage = when(route) {
                        "home" -> 0
                        "presence" -> 1
                        "profile" -> 2
                        else -> 0
                    }
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = targetPage,
                            animationSpec = tween(
                                durationMillis = 500,
                                easing = androidx.compose.animation.core.FastOutSlowInEasing
                            )
                        )
                    }
                },
                containerColor = targetColor,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            )
        }
    }


}
