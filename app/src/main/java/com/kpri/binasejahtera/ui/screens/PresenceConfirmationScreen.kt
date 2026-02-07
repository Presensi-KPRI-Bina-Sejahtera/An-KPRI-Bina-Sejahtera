package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriOsmMap
import com.kpri.binasejahtera.ui.components.KpriPrimaryButton
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.ErrorContainer
import com.kpri.binasejahtera.ui.theme.ErrorRed
import com.kpri.binasejahtera.ui.theme.InfoGreen
import com.kpri.binasejahtera.ui.theme.InfoRed
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.SuccessContainer
import com.kpri.binasejahtera.ui.theme.SuccessGreen
import com.kpri.binasejahtera.ui.theme.TertiaryGray
import com.kpri.binasejahtera.ui.viewmodel.ConfirmationUiState
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PresenceConfirmationScreen(
    isCheckIn: Boolean,
    state: ConfirmationUiState,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val title = if (isCheckIn) "Presensi Masuk" else "Presensi Pulang"

    val presenceCardShape = Shapes.medium.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )

    // realtime clock
    var currentTime by remember { mutableStateOf("") }
    var currentDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale("id", "ID"))

        while (true) {
            val now = Date()
            currentTime = timeFormat.format(now)
            currentDate = dateFormat.format(now)
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // maps
        if (state.officeLat != 0.0 && state.officeLong != 0.0) {
            KpriOsmMap(
                latitude = state.officeLat,
                longitude = state.officeLong,
                radiusMeter = state.maxRadius,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppBackground)
            )
        }

        // info presensi masuk/pulang
        FloatingTopBar(
            title = title,
            onBackClick = onBackClick,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(
                    top = 16.dp,
                    start = 24.dp,
                    end = 24.dp
                )
        )

        // presence card
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom
        ) {

            Spacer(modifier = Modifier.weight(1f))

            Card(
                shape = presenceCardShape,
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 6.dp,
                        shape = presenceCardShape,
                        spotColor = Color.Black.copy(alpha = 0.5f)
                    ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // jam
                    Text(
                        text = currentTime.ifEmpty { "--:--:--" },
                        style = MaterialTheme.typography.displayLarge.copy(fontSize = 32.sp),
                        color = PrimaryBlack
                    )

                    Text(
                        text = currentDate,
                        style = MaterialTheme.typography.labelMedium,
                        color = TertiaryGray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // card lokasi presensi
                    LocationDetailCard(
                        title = "Lokasi Presensi",
                        locationName = state.officeName,
                        address = state.officeAddress
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // radius info
                        StatCard(
                            label = "Radius",
                            value = "${state.maxRadius.toInt()} Meter",
                            backgroundColor = AppBackground.copy(alpha = 0.25f),
                            borderStroke = BorderStroke(1.dp, TertiaryGray.copy(alpha = 0.3f)),
                            contentColor = PrimaryBlack,
                            modifier = Modifier.weight(1f)
                        )

                        // safe radius
                        val distanceBgColor = if (state.isSafe) SuccessContainer else ErrorContainer
                        val distanceContentColor = if (state.isSafe) InfoGreen else ErrorRed
                        val distanceBorderColor = if (state.isSafe) SuccessGreen else ErrorRed

                        StatCard(
                            label = "Jarak Anda",
                            value = "${state.currentDistance.toInt()} Meter",
                            iconId = R.drawable.ic_nav_arrow,
                            backgroundColor = distanceBgColor,
                            borderStroke = BorderStroke(1.dp, distanceBorderColor.copy(alpha = 0.3f)),
                            contentColor = distanceContentColor,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.error != null) {
                        Text(
                            text = state.error,
                            style = MaterialTheme.typography.labelSmall,
                            color = ErrorRed,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // tombol presensi
                    KpriPrimaryButton(
                        text = "$title Sekarang",
                        iconId = R.drawable.ic_map,
                        onClick = onConfirmClick,
                        isIconStart = true,
                        enabled = state.isSafe && !state.isLoadingLocation,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // note dibawah tombol presensi
                    Text(
                        text = if (state.isLoadingLocation) "Sedang mencari lokasi..." else "Pastikan GPS Anda aktif dan akurat",
                        style = MaterialTheme.typography.labelSmall,
                        color = TertiaryGray.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.navigationBarsPadding())

                    Spacer(modifier = Modifier.height(16.dp))

                }
            }
        }
    }
}

@Composable
fun FloatingTopBar(
    title: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // tombol back
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(48.dp)
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_left),
                    contentDescription = "Back",
                    tint = PrimaryBlack,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }

        // label judul info
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 16.sp),
                color = PrimaryBlack,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun LocationDetailCard(
    title: String,
    locationName: String,
    address: String
) {
    Surface(
        shape = Shapes.medium,
        color = AppBackground.copy(alpha = 0.25f),
        border = BorderStroke(1.dp, TertiaryGray.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon pin
            Icon(
                painter = painterResource(id = R.drawable.ic_map),
                contentDescription = null,
                tint = InfoRed,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = TertiaryGray
                )

                Text(
                    text = locationName,
                    style = MaterialTheme.typography.labelMedium,
                    color = PrimaryBlack
                )

                Text(
                    text = address,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = TertiaryGray
                )
            }
        }
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    backgroundColor: Color,
    borderStroke: BorderStroke,
    contentColor: Color,
    modifier: Modifier = Modifier,
    iconId: Int? = null
) {
    Surface(
        shape = Shapes.medium,
        color = backgroundColor,
        border = borderStroke,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (iconId != null) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
            }
        }
    }
}

@Preview(name = "1. Masuk - Safe Zone", showBackground = true, showSystemUi = true)
@Composable
fun PresenceInSafePreview() {
    KPRIBinaSejahteraTheme {
        PresenceConfirmationScreen(
            isCheckIn = true,
            state = ConfirmationUiState(
                officeLat = -6.2,
                officeLong = 106.8,
                officeName = "Koperasi Sejahtera Bersama",
                officeAddress = "Jl. Jendral Sudirman No. 1",
                maxRadius = 50.0,
                currentDistance = 15.0,
                isSafe = true,
                isLoadingLocation = false
            ),
            onBackClick = {},
            onConfirmClick = {}
        )
    }
}

@Preview(name = "2. Pulang - Danger Zone", showBackground = true, showSystemUi = true)
@Composable
fun PresenceOutDangerPreview() {
    KPRIBinaSejahteraTheme {
        PresenceConfirmationScreen(
            isCheckIn = false,
            state = ConfirmationUiState(
                officeLat = -6.2,
                officeLong = 106.8,
                officeName = "Koperasi Sejahtera Bersama",
                officeAddress = "Jl. Jendral Sudirman No. 1",
                maxRadius = 50.0,
                currentDistance = 120.0,
                isSafe = false,
                isLoadingLocation = false,
                error = "Anda berada di luar jangkauan"
            ),
            onBackClick = {},
            onConfirmClick = {}
        )
    }
}

@Preview(name = "3. Loading State", showBackground = true, showSystemUi = true)
@Composable
fun PresenceLoadingPreview() {
    KPRIBinaSejahteraTheme {
        PresenceConfirmationScreen(
            isCheckIn = true,
            state = ConfirmationUiState(
                isLoadingLocation = true,
                officeName = "Memuat data...",
                officeAddress = "..."
            ),
            onBackClick = {},
            onConfirmClick = {}
        )
    }
}