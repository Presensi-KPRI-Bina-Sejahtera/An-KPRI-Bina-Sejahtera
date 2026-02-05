package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.SuccessGreen

@Composable
fun KpriPresenceTile(
    title: String,
    subtitle: String,
    iconId: Int,
    iconColor: Color,
    onClick: () -> Unit,
    iconSize: Dp = 20.dp
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, iconColor.copy(alpha = 0.2f)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = Shapes.medium,
                color = iconColor.copy(alpha = 0.1f)
            ) {
                Icon(
                    painterResource(id = iconId),
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier
                        .padding(12.dp)
                        .size(iconSize)

                ) 
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KpriPresenceTilePreview() {
    KPRIBinaSejahteraTheme {
        KpriPresenceTile(
            title = "Presensi Masuk",
            subtitle = "Datang & mulai kerja",
            iconId = R.drawable.ic_in,
            iconColor = SuccessGreen,
            onClick = {}
        )
    }
}