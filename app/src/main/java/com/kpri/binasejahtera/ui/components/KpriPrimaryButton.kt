package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun KpriPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = PrimaryBlack,
    contentColor: Color = Color.White,
    border: BorderStroke? = null,
    iconId: Int? = null,
    iconSize: Dp = 24.dp,
    height: Dp = 56.dp,
    isIconStart: Boolean = false
) {

    val finalContainerColor = if (enabled) containerColor else TertiaryGray.copy(alpha = 0.25f)
    val finalContentColor = if (enabled) contentColor else TertiaryGray
    val finalBorder = if (enabled) border else BorderStroke(1.dp, TertiaryGray.copy(alpha = 0.3f))

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        border = finalBorder,
        colors = ButtonDefaults.buttonColors(
            containerColor = finalContainerColor,
            contentColor = finalContentColor,
            disabledContainerColor = finalContainerColor,
            disabledContentColor = finalContentColor
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (iconId != null && isIconStart) {

                Icon(
                    painter = painterResource(id = iconId),
                    modifier = Modifier.size(iconSize),
                    contentDescription = null,
                    tint = finalContentColor
                )

                Spacer(modifier = Modifier.width(8.dp))

            }

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = finalContentColor
            )

            if (iconId != null && !isIconStart) {

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = iconId),
                    modifier = Modifier.size(iconSize),
                    contentDescription = null,
                    tint = finalContentColor
                )

            }
        }
    }
}

@Preview(name = "1. Solid Button (Enabled)", showBackground = true)
@Composable
fun PrimaryButtonSolidPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriPrimaryButton(
                text = "Masuk",
                iconId = R.drawable.ic_in,
                onClick = {}
            )
        }
    }
}

@Preview(name = "2. Outlined Button (Tambah)", showBackground = true)
@Composable
fun PrimaryButtonOutlinedPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriPrimaryButton(
                text = "Tambah Setoran Lain",
                iconId = R.drawable.ic_plus,
                containerColor = Color.Transparent,
                contentColor = TertiaryGray,
                border = BorderStroke(1.dp, TertiaryGray.copy(alpha = 0.3f)),
                isIconStart = true,
                onClick = {}
            )
        }
    }
}

@Preview(name = "3. Disabled State (Greyed Out)", showBackground = true)
@Composable
fun PrimaryButtonDisabledPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriPrimaryButton(
                text = "Presensi Pulang Sekarang",
                iconId = R.drawable.ic_map,
                isIconStart = true,
                enabled = false,
                onClick = {}
            )
        }
    }
}