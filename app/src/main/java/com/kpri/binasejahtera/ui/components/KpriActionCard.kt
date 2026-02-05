package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.BorderGray
import com.kpri.binasejahtera.ui.theme.ErrorContainer
import com.kpri.binasejahtera.ui.theme.InfoRed
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun KpriActionCard(
    title: String,
    iconId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    isDestructive: Boolean = false,
    containerColor: Color = Color.White
) {
    val contentColor = if (isDestructive) InfoRed else PrimaryBlack
    val backgroundColor = if (isDestructive) ErrorContainer else containerColor
    val borderColor = if (isDestructive) InfoRed else Color.Transparent

    Card(
        onClick = onClick,
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = if (isDestructive) BorderStroke(1.dp, borderColor) else BorderStroke(1.dp, color = BorderGray),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                Shapes.medium,
                spotColor = Color.Black.copy(0.5f)
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White,
                shadowElevation = 4.dp,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.padding(10.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isDestructive) contentColor.copy(alpha = 0.7f) else TertiaryGray
                    )
                }
            }

            if (!isDestructive) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = null,
                    tint = TertiaryGray,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(name = "1. Normal Menu", showBackground = true)
@Composable
fun KpriActionCardPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriActionCard(
                title = "Informasi Pribadi",
                subtitle = "Nama, Email, No HP",
                iconId = R.drawable.ic_profile,
                onClick = {}
            )
        }
    }
}

@Preview(name = "2. Logout Button", showBackground = true)
@Composable
fun KpriActionCardDestructivePreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriActionCard(
                title = "Keluar Aplikasi",
                iconId = R.drawable.ic_out,
                isDestructive = true,
                onClick = {}
            )
        }
    }
}