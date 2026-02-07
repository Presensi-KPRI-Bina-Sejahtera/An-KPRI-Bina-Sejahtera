package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriBottomNavigation
import com.kpri.binasejahtera.ui.components.KpriPresenceTile
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.ErrorRed
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.SuccessGreen
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun PresenceScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            KpriBottomNavigation(
                currentRoute = "presence",
                onNavigate = onNavigate,
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 24.dp)
            )
        },
        containerColor = AppBackground
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Mau Presensi Apa?",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                color = PrimaryBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Silakan pilih jenis presensi Anda hari ini",
                style = MaterialTheme.typography.labelMedium,
                color = TertiaryGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            KpriPresenceTile(
                title = "Presensi Masuk",
                subtitle = "Datang & mulai kerja",
                iconId = R.drawable.ic_in,
                iconColor = SuccessGreen,
                onClick = { onNavigate("attendance_in") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            KpriPresenceTile(
                title = "Presensi Pulang",
                subtitle = "Laporan harian & pulang",
                iconId = R.drawable.ic_out,
                iconColor = ErrorRed,
                onClick = { onNavigate("attendance_out") }
            )

            Spacer(modifier = Modifier.height(100.dp))
        }

    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PresenceScreenPreview() {
    KPRIBinaSejahteraTheme {
        PresenceScreen(onNavigate = {})
    }
}