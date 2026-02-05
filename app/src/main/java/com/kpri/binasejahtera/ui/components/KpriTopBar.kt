package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.TertiaryGray

sealed interface TopBarConfig {
    // dashboard home
    data class Home(
        val greeting: String,
        val name: String,
        val userPhotoId: Int
    ) : TopBarConfig

    // navigation bar (laporan, edit, change pass)
    data class Navigation(
        val title: String,
        val subtitle: String? = null,
        val onBackClick: () -> Unit
    ) : TopBarConfig

    // profile center
    data class Profile(
        val name: String,
        val username: String,
        val userPhotoId: Int
    ) : TopBarConfig
}

@Composable
fun KpriTopBar(
    config: TopBarConfig,
    modifier: Modifier = Modifier
) {
    val topBarShape = Shapes.medium.copy(
        topStart = CornerSize(0.dp),
        topEnd = CornerSize(0.dp)
    )

    Surface(
        color = PrimaryBlack,
        shape = topBarShape,
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                shape = topBarShape,
                spotColor = Color.Black.copy(alpha = 0.5f)
            )
    ) {
        Column(
            modifier = Modifier.statusBarsPadding()
        ) {
            when (config) {
                is TopBarConfig.Home -> HomeTopBarContent(config)
                is TopBarConfig.Navigation -> NavigationTopBarContent(config)
                is TopBarConfig.Profile -> ProfileTopBarContent(config)
            }
        }
    }
}

@Composable
private fun HomeTopBarContent(config: TopBarConfig.Home) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Surface(
            shape = CircleShape,
            shadowElevation = 16.dp,
            modifier = Modifier.size(48.dp)
        ) {
            Image(
                painter = painterResource(id = config.userPhotoId),
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = config.greeting,
                style = MaterialTheme.typography.labelMedium,
                color = TertiaryGray
            )

            Text(
            text = config.name,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 20.sp),
                color = Color.White
            )
        }
    }
}

@Composable
private fun NavigationTopBarContent(config: TopBarConfig.Navigation) {
    Row(
        modifier = Modifier
            .padding(
                horizontal = 8.dp,
                vertical = 8.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = config.onBackClick) {
            Icon(
                painter = painterResource(id = R.drawable.ic_arrow_left),
                contentDescription = "Back",
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = config.title,
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
                color = Color.White
            )

            if (config.subtitle != null) {
                Text(
                    text = config.subtitle,
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = TertiaryGray
                )
            }
        }
    }
}

@Composable
private fun ProfileTopBarContent(config: TopBarConfig.Profile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        // motif lingkaran abu-abu
        Canvas(
            modifier = Modifier
                .matchParentSize()
        ) {
            // bottom left
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 125.dp.toPx(),
                center = Offset(x = 50f, y = size.height)
            )

            // top right
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = 100.dp.toPx(),
                center = Offset(x = size.width, y = 100f)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                shadowElevation = 16.dp,
                modifier = Modifier.size(80.dp)
            ) {
                Image(
                    painter = painterResource(id = config.userPhotoId),
                    contentDescription = "Profile Picture",
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = config.name,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = config.username,
                style = MaterialTheme.typography.labelMedium,
                color = TertiaryGray
            )
        }
    }
}

@Preview(name = "1. Dashboard Mode", showBackground = true)
@Composable
fun TopBarHomePreview() {
    KPRIBinaSejahteraTheme {
        KpriTopBar(
            config = TopBarConfig.Home(
                greeting = "Selamat datang,",
                name = "Endra Zhafir",
                userPhotoId = R.drawable.profilepicture
            )
        )
    }
}

@Preview(name = "2. Nav + Desc Mode (Laporan)", showBackground = true)
@Composable
fun TopBarNavDescPreview() {
    KPRIBinaSejahteraTheme {
        KpriTopBar(
            config = TopBarConfig.Navigation(
                title = "Laporan Harian",
                subtitle = "Isi data sebelum pulang",
                onBackClick = {}
            )
        )
    }
}

@Preview(name = "3. Profile Mode", showBackground = true)
@Composable
fun TopBarProfilePreview() {
    KPRIBinaSejahteraTheme {
        KpriTopBar(
            config = TopBarConfig.Profile(
                name = "Endra Zhafir",
                username = "endra_zhafir",
                userPhotoId = R.drawable.profilepicture
            )
        )
    }
}

@Preview(name = "4. Nav Title Only (Change Pass)", showBackground = true)
@Composable
fun TopBarNavSimplePreview() {
    KPRIBinaSejahteraTheme {
        KpriTopBar(
            config = TopBarConfig.Navigation(
                title = "Edit Informasi Pribadi",
                subtitle = null,
                onBackClick = {}
            )
        )
    }
}