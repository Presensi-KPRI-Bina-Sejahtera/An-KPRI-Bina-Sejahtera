package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriActionCard
import com.kpri.binasejahtera.ui.components.KpriBottomNavigation
import com.kpri.binasejahtera.ui.components.KpriTopBar
import com.kpri.binasejahtera.ui.components.TopBarConfig
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Scaffold(
        topBar = {
            KpriTopBar(
                config = TopBarConfig.Profile(
                    name = "Endra Zhafir",
                    username = "endra_zhafir",
                    userPhotoUrl = null
                )
            )
        },

        bottomBar = {
            KpriBottomNavigation(
                currentRoute = "profile",
                onNavigate = onNavigate,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            )
        },
        containerColor = AppBackground
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(24.dp))

            // menu info
            KpriActionCard(
                title = "Informasi Pribadi",
                subtitle = "Nama, Email",
                iconId = R.drawable.ic_profile,
                onClick = { onNavigate("personal_info") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // menu settings
            KpriActionCard(
                title = "Atur Kata Sandi",
                subtitle = "Ubah Password",
                iconId = R.drawable.ic_settings,
                onClick = { onNavigate("change_password") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // tombol keluar
            KpriActionCard(
                title = "Logout",
                iconId = R.drawable.ic_out,
                isDestructive = true,
                onClick = onLogout
            )

            // versi app
            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Versi Aplikasi 1.0.0",
                style = MaterialTheme.typography.labelSmall,
                color = TertiaryGray.copy(alpha = 0.5f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(130.dp))

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    KPRIBinaSejahteraTheme {
        ProfileScreen(
            onNavigate = {},
            onLogout = {}
        )
    }
}