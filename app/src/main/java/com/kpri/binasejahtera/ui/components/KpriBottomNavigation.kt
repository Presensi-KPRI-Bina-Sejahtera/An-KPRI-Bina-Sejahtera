package com.kpri.binasejahtera.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun KpriBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier
) {
    val isPresenceActive = currentRoute == "presence"

    val animatedContainerColor by animateColorAsState(
        targetValue = containerColor,
        animationSpec = tween(durationMillis = 300),
        label = "navBarBg"
    )

    // animasi transisi warna icon (home dan profile)
    val activeContentColor = if (isPresenceActive) Color.White else PrimaryBlack
    val inactiveContentColor = if (isPresenceActive) Color.White else TertiaryGray

    // animasi transisi warna content
    val animatedActiveColor by animateColorAsState(
        targetValue = activeContentColor,
        animationSpec = tween(200),
        label = "iconActive"
    )

    val animatedInactiveColor by animateColorAsState(
        targetValue = inactiveContentColor.copy(alpha = 0.6f),
        animationSpec = tween(200),
        label = "iconInactive"
    )

    // box untuk tombol presensi yang nonjol
    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
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
            color = animatedContainerColor,
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
                    activeColor = animatedActiveColor,
                    inactiveColor = animatedInactiveColor,
                    onClick = {
                        if (currentRoute != "home") {
                            onNavigate("home")
                        }
                    }
                )

                // spacer tengah untuk gap
                Spacer(modifier = Modifier.width(48.dp))

                // profil
                KpriNavItem(
                    iconId = R.drawable.ic_profile,
                    label = "Profile",
                    isSelected = currentRoute == "profile",
                    activeColor = animatedActiveColor,
                    inactiveColor = animatedInactiveColor,
                    onClick = {
                        if (currentRoute != "profile") {
                            onNavigate("profile")
                        }
                    }
                )
            }
        }

        // tombol presensi
        Box(
            modifier = Modifier.align(Alignment.TopCenter)
        ) {
            KpriPresenceButton(
                isActive = isPresenceActive,
                outerBorderColor = containerColor,
                onClick = {
                    if (currentRoute != "presence") {
                        onNavigate("presence")
                    }
                }
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
        animationSpec = tween(durationMillis = 150), label = "btnBg"
    )

    val iconColor by animateColorAsState(
        targetValue = if (isActive) PrimaryBlack else Color.White,
        animationSpec = tween(durationMillis = 300), label = "btnIcon"
    )

    val borderColor by animateColorAsState(
        targetValue = outerBorderColor,
        animationSpec = tween(durationMillis = 300), label = "btnBorder"
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
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            )
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
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 11.sp),
            color = if (isSelected) activeColor else inactiveColor
        )
    }
}

// preview kuhilangin soalnya ribet lol