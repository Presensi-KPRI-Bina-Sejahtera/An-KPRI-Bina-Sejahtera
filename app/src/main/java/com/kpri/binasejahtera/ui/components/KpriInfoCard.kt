package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes

@Composable
fun KpriInfoCard(
    title: String,
    value: String,
    iconId: Int,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        shape = Shapes.medium,
        colors = CardDefaults.cardColors(containerColor = PrimaryBlack),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 12.dp,
                Shapes.medium,
                spotColor = Color.Black.copy(0.5f)
            )
            .then(
                if (onClick != null) Modifier.clickable { onClick() } else Modifier
            )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = Shapes.small,
                color = Color.White,
                modifier = Modifier.size(40.dp),
                shadowElevation = 4.dp,
            ) {
                Icon(
                    painter = painterResource(id = iconId),
                    contentDescription = null,
                    tint = PrimaryBlack,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )

                Text(
                    text = value,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White
                )
            }

            if (onClick != null) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_go),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview(
    name = "1. Static Info (Lokasi)",
    showBackground = true
)
@Composable
fun InfoCardStaticPreview() {
    KPRIBinaSejahteraTheme {
        KpriInfoCard(
            title = "Lokasi Anda",
            value = "Jl. Jendral Sudirman No. 1 (Dalam Radius)",
            iconId = R.drawable.ic_map
        )
    }
}

@Preview(
    name = "2. Action Info (Berangkat)",
    showBackground = true
)
@Composable
fun InfoCardActionPreview() {
    KPRIBinaSejahteraTheme {
        KpriInfoCard(
            title = "Berangkat Ke Tempat Kerja",
            value = "Buka Maps untuk navigasi",
            iconId = R.drawable.ic_nav_arrow,
            onClick = {}
        )
    }
}