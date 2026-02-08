package com.kpri.binasejahtera.ui.screens

import android.content.Intent
import android.net.Uri
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriBottomNavigation
import com.kpri.binasejahtera.ui.components.KpriInfoCard
import com.kpri.binasejahtera.ui.components.KpriTopBar
import com.kpri.binasejahtera.ui.components.TopBarConfig
import com.kpri.binasejahtera.ui.components.ToastManager
import com.kpri.binasejahtera.ui.components.ToastType
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.ErrorRed
import com.kpri.binasejahtera.ui.theme.InfoBlue
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.SuccessGreen
import com.kpri.binasejahtera.ui.theme.TertiaryGray
import com.kpri.binasejahtera.ui.viewmodel.AttendanceViewModel
import com.kpri.binasejahtera.ui.viewmodel.HomeUiState
import androidx.core.net.toUri

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit,
    isNested: Boolean = false
) {
    val viewModel: AttendanceViewModel = hiltViewModel()
    val state by viewModel.homeState.collectAsState()

    HomeContent(
        state = state,
        onNavigate = onNavigate,
        onRefreshLocation = { viewModel.loadUserLocation() },
        isNested = isNested
    )
}

@Composable
fun HomeContent(
    state: HomeUiState,
    onNavigate: (String) -> Unit,
    onRefreshLocation: () -> Unit = {},
    isNested: Boolean = false
) {
    val context = LocalContext.current

    // logic untuk izin gps
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (isGranted) {
            onRefreshLocation()
        } else {
            ToastManager.show("Nyalakan izin lokasi diperlukan untuk fitur presensi", ToastType.ERROR)
        }
    }

    // cek izin otomatis saat masuk halaman
    LaunchedEffect(Unit) {
        val hasFineLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLoc = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLoc && !hasCoarseLoc) {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Scaffold(
        topBar = {
            KpriTopBar(
                config = TopBarConfig.Home(
                    greeting = state.greeting,
                    name = state.userName,
                    userPhotoUrl = state.userPhoto
                ),
            )
        },
        bottomBar = {
            if (!isNested) {
                KpriBottomNavigation(
                    currentRoute = "home",
                    onNavigate = onNavigate,
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(bottom = 24.dp)
                )
            }
        },
        containerColor = AppBackground

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = innerPadding.calculateTopPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // tanggal
                Card(
                    shape = Shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, Shapes.medium, spotColor = Color.Black.copy(0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_calendar),
                            contentDescription = null,
                            tint = PrimaryBlack,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.currentDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = TertiaryGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // shift & waktu
                Card(
                    shape = Shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(12.dp, Shapes.medium, spotColor = Color.Black.copy(0.5f))
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TimeColumn(
                                label = "Masuk",
                                time = state.checkInTime,
                                isPlaceholder = state.checkInTime == "--:--:--"
                            )

                            VerticalDivider(
                                modifier = Modifier.height(40.dp).width(1.dp),
                                color = TertiaryGray.copy(alpha = 0.3f)
                            )

                            TimeColumn(
                                label = "Jam Pulang",
                                time = state.checkOutTime,
                                isPlaceholder = state.checkOutTime == "--:--:--"
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // durasi kerja
                        Surface(
                            color = TertiaryGray.copy(alpha = 0.05f),
                            shape = Shapes.medium,
                            border = BorderStroke(1.dp, TertiaryGray.copy(alpha = 0.1f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(8.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Durasi Kerja",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TertiaryGray
                                )
                                Spacer(modifier = Modifier.padding(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_clock),
                                        contentDescription = null,
                                        tint = InfoBlue,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = state.workDuration,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = InfoBlue
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // tombol presensi (quick action button)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    DashboardActionCard(
                        title = "Presensi Masuk",
                        subtitle = "Datang & mulai kerja",
                        iconId = R.drawable.ic_in,
                        colorTheme = SuccessGreen,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (state.isCheckIn || state.isCheckOut) {
                                ToastManager.show("Anda sudah presensi masuk hari ini", ToastType.ERROR)
                            } else {
                                onNavigate("attendance_in")
                            }
                        }
                    )

                    DashboardActionCard(
                        title = "Presensi Pulang",
                        subtitle = "Laporan harian & pulang",
                        iconId = R.drawable.ic_out,
                        colorTheme = ErrorRed,
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (state.isCheckOut) {
                                ToastManager.show("Anda sudah presensi pulang hari ini", ToastType.ERROR)
                            } else if (!state.isCheckIn) {
                                ToastManager.show("Anda belum melakukan Presensi Masuk", ToastType.ERROR)
                            } else {
                                onNavigate("attendance_out")
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // info lokasi
                KpriInfoCard(
                    title = "Lokasi Anda",
                    value = state.currentAddress,
                    iconId = R.drawable.ic_map
                )

                Spacer(modifier = Modifier.height(16.dp))

                KpriInfoCard(
                    title = "Berangkat Ke Tempat Kerja",
                    value = state.officeAddress,
                    iconId = R.drawable.ic_nav_arrow,
                    onClick = {
                        val mapUrl = state.officeMapsUrl
                            ?: "geo:0,0?q=${Uri.encode(state.officeAddress)}"

                        try {
                            val intent = Intent(Intent.ACTION_VIEW, mapUrl.toUri())
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                )

                Spacer(modifier = Modifier.height(160.dp))
            }
        }
    }
}

@Composable
private fun TimeColumn(
    label: String,
    time: String,
    isPlaceholder:
    Boolean = false
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = TertiaryGray
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = time,
            style = MaterialTheme.typography.titleLarge,
            color = if (isPlaceholder) TertiaryGray.copy(alpha = 0.5f) else PrimaryBlack
        )
    }
}

@Composable
private fun DashboardActionCard(
    title: String,
    subtitle: String,
    iconId: Int,
    colorTheme: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = modifier
            .shadow(
                elevation = 12.dp,
                Shapes.medium,
                spotColor = Color.Black.copy(0.5f)
            )
            .height(160.dp)
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                color = colorTheme.copy(alpha = 0.1f),
                shape = Shapes.medium,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = colorTheme,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = PrimaryBlack,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = TertiaryGray,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreenPreview() {
    KPRIBinaSejahteraTheme {
        HomeContent(
            state = HomeUiState(
                userName = "Endra Zhafir",
                currentDate = "Sabtu, 08 Februari 2026",
                checkInTime = "07:45:00",
                workDuration = "4 jam 30 menit",
                officeAddress = "Jl. Jendral Sudirman No. 1",
                currentAddress = "Rumah"
            ),
            onNavigate = {}
        )
    }
}