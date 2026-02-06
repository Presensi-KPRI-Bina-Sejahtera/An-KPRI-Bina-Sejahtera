package com.kpri.binasejahtera.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.*

@Composable
fun KpriBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isPresenceActive = currentRoute == "presence"

    // animasi transisi warna
    val navBackgroundColor by animateColorAsState(
        targetValue = if (isPresenceActive) PrimaryBlack else Color.White,
        animationSpec = tween(durationMillis = 500), label = "navBg"
    )

    val activeContentColor = if (isPresenceActive) Color.White else PrimaryBlack
    val inactiveContentColor = if (isPresenceActive) Color.White else TertiaryGray

    // box untuk tombol presensi yang nonjol
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(96.dp)
    ) {
        // bar menu navigasi
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(64.dp)
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    Shapes.medium,
                    spotColor = Color.Black.copy(0.5f)
                ),
            color = navBackgroundColor,
            shape = Shapes.medium
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // home
                KpriNavItem(
                    iconId = R.drawable.ic_home,
                    label = "Home",
                    isSelected = currentRoute == "home",
                    activeColor = activeContentColor,
                    inactiveColor = inactiveContentColor,
                    onClick = { onNavigate("home") }
                )

                // spacer tengah untuk gap
                Spacer(modifier = Modifier.width(48.dp))

                // profil
                KpriNavItem(
                    iconId = R.drawable.ic_profile,
                    label = "Profile",
                    isSelected = currentRoute == "profile",
                    activeColor = activeContentColor,
                    inactiveColor = inactiveContentColor,
                    onClick = { onNavigate("profile") }
                )
            }
        }

        // tombol presensi
        Box(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            KpriPresenceButton(
                isActive = isPresenceActive,
                outerBorderColor = navBackgroundColor,
                onClick = { onNavigate("presence") }
            )
        }
    }
}

@Composable
fun KpriPresenceButton(
    isActive:Boolean,
    outerBorderColor: Color,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (isActive) Color.White else PrimaryBlack,
        animationSpec = tween(durationMillis = 500), label = "btnBg"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isActive) PrimaryBlack else Color.White,
        animationSpec = tween(durationMillis = 500), label = "btnIcon"
    )

    val borderColor by animateColorAsState(
        targetValue = outerBorderColor,
        animationSpec = tween(durationMillis = 500), label = "btnBorder"
    )

    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .background(borderColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(containerColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_fingerprint),
                contentDescription = "Presensi",
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun KpriNavItem(
    iconId: Int,
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = label,
            tint = if (isSelected) activeColor else inactiveColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium.copy(
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            ),
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

@Preview(name = "1. Home", showBackground = true)
@Composable
fun KpriBottomNavHomePreview() {
    KPRIBinaSejahteraTheme {
        KpriBottomNavigation(currentRoute = "home", onNavigate = {})
    }
}

@Preview(name = "2. Profile", showBackground = true)
@Composable
fun KpriBottomNavProfilePreview() {
    KPRIBinaSejahteraTheme {
        KpriBottomNavigation(currentRoute = "profile", onNavigate = {})
    }
}

@Preview(name = "3. Presence", showBackground = true)
@Composable
fun KpriBottomNavPresencePreview() {
    KPRIBinaSejahteraTheme {
        KpriBottomNavigation(currentRoute = "presence", onNavigate = {})
    }
}