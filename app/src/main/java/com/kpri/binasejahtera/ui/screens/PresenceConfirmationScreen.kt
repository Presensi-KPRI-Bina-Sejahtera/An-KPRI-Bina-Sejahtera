package com.kpri.binasejahtera.ui.screens

import android.app.Activity
import android.content.IntentSender
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest.Builder
import com.google.android.gms.location.Priority
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriOsmMap
import com.kpri.binasejahtera.ui.components.KpriPrimaryButton
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.ErrorContainer
import com.kpri.binasejahtera.ui.theme.ErrorRed
import com.kpri.binasejahtera.ui.theme.InfoGreen
import com.kpri.binasejahtera.ui.theme.InfoRed
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
    onConfirmClick: () -> Unit,
    onUpdateLocation: () -> Unit,
    onRefresh: () -> Unit
) {
    val title = if (isCheckIn) "Presensi Masuk" else "Presensi Pulang"
    val context = LocalContext.current

    val settingResultRequest = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // pas user teken yes, lgsg nyalain
            onUpdateLocation()
        }
    }

    // ngecek settingan gps
    LaunchedEffect(Unit) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000).build()
        val builder = Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(context)
        val task = client.checkLocationSettings(builder.build())

        // klo gps ok lgsg update lokasi
        task.addOnSuccessListener {
            onUpdateLocation()
        }

        // gps mati, munculkan dialog
        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest.Builder(exception.resolution).build()
                    settingResultRequest.launch(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // ignore
                }
            }
        }
    }

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
            onUpdateLocation()
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
                userLatitude = state.userLat,
                userLongitude = state.userLong,
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
            onRefreshClick = onRefresh,
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
                        val radiusText = if (state.isLoadingLocation) "..." else "${state.maxRadius.toInt()} Meter"
                        val distanceText = if (state.isLoadingLocation) "Memuat..." else "${state.currentDistance.toInt()} Meter"

                        // radius info
                        StatCard(
                            label = "Radius",
                            value = radiusText,
                            backgroundColor = AppBackground.copy(alpha = 0.25f),
                            borderStroke = BorderStroke(1.dp, TertiaryGray.copy(alpha = 0.3f)),
                            contentColor = PrimaryBlack,
                            modifier = Modifier.weight(1f)
                        )

                        // safe radius
                        val isSafe = state.isSafe
                        val distanceBgColor = if (state.isLoadingLocation) AppBackground else if (isSafe) SuccessContainer else ErrorContainer
                        val distanceContentColor = if (state.isLoadingLocation) TertiaryGray else if (isSafe) InfoGreen else ErrorRed
                        val distanceBorderColor = if (state.isLoadingLocation) TertiaryGray else if (isSafe) SuccessGreen else ErrorRed

                        StatCard(
                            label = "Jarak Anda",
                            value = distanceText,
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

                    if (state.isAlreadyDone) {
                        Text(
                            text = if (isCheckIn) "Anda sudah melakukan Presensi Masuk hari ini" else "Anda sudah melakukan Presensi Pulang hari ini",
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
                        enabled = state.isSafe && !state.isLoadingLocation && !state.isAlreadyDone,
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
    onRefreshClick: () -> Unit,
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

        // tombol refresh
        Surface(
            shape = CircleShape,
            color = Color.White,
            shadowElevation = 8.dp,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(48.dp)
        ) {
            IconButton(onClick = onRefreshClick) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Refresh Data",
                    tint = PrimaryBlack,
                    modifier = Modifier.size(20.dp)
                )
            }
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

// preview kuhapus karena malas ngotak atik lagi wkwkwk