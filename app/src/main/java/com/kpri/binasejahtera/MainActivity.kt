package com.kpri.binasejahtera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.kpri.binasejahtera.ui.components.KpriCustomToastHost
import com.kpri.binasejahtera.ui.navigation.AppNavGraph
import com.kpri.binasejahtera.ui.navigation.Screen
import com.kpri.binasejahtera.ui.screens.MainScreen
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import org.osmdroid.config.Configuration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // OSMDroid config
        val ctx = applicationContext
        Configuration.getInstance().load(ctx, androidx.preference.PreferenceManager.getDefaultSharedPreferences(ctx))
        Configuration.getInstance().userAgentValue = packageName

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )

        setContent {
            KPRIBinaSejahteraTheme {
                val navController = rememberNavController()

                val isUserLoggedIn by authViewModel.isUserLoggedIn.collectAsState(initial = null)

                var showSplashScreen by remember { mutableStateOf(true) }

                LaunchedEffect(Unit) {
                    delay(2000)
                    showSplashScreen = false
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    if (showSplashScreen || isUserLoggedIn == null) {
                        MainScreen()
                    } else {
                        val startDest = if (isUserLoggedIn == true) Screen.Home.route else Screen.Login.route

                        AppNavGraph(
                            navController = navController,
                            startDestination = startDest
                        )
                    }
                    
                    KpriCustomToastHost()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    KPRIBinaSejahteraTheme {
        MainScreen()
    }
}
