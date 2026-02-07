package com.kpri.binasejahtera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.kpri.binasejahtera.ui.components.KpriCustomToastHost
import com.kpri.binasejahtera.ui.navigation.AppNavGraph
import com.kpri.binasejahtera.ui.screens.LoginScreen
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import dagger.hilt.android.AndroidEntryPoint
import org.osmdroid.config.Configuration

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
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

                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavGraph(navController = navController)

                    // custom toast dari atas
                    KpriCustomToastHost()
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    KPRIBinaSejahteraTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onGoogleSignInClick = {}
        )
    }
}
