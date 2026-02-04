package com.kpri.binasejahtera.ui.components

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

@Composable
fun KpriPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = PrimaryBlack,
    iconId: Int? = null,
    iconSize: Dp = 20.dp,
    height: Dp = 56.dp
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = MaterialTheme.shapes.medium,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = Color.White
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(height)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )

            if (iconId != null) {

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = iconId),
                    modifier = Modifier.size(iconSize),
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun KpriPrimaryButtonPreview() {
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