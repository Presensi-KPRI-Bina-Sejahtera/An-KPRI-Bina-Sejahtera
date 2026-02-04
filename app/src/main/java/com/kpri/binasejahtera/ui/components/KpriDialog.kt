package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.*
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme

@Composable
fun KpriDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    confirmText: String = "Lanjut",
    iconId: Int? = null,
    iconContainerColor: Color = Color.Transparent,
    secondaryButtonText: String? = null,
    onSecondaryClick: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = { },

        icon = {
            if (iconId != null) {
                Surface(
                    shape = CircleShape,
                    color = iconContainerColor.copy(alpha = 0.1f),
                    modifier = Modifier.size(64.dp)

                ) {
                    Icon(
                        painterResource(id = iconId),
                        contentDescription = null,
                        tint = iconContainerColor,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp)
                    )
                }
            }
        },

        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
            )
        },

        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                color = TertiaryGray
            )
        },

        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // tombol cancel
                if (secondaryButtonText != null) {
                    KpriPrimaryButton(
                        text = secondaryButtonText,
                        onClick = onSecondaryClick ?: {},
                        modifier = Modifier.weight(1f),
                        containerColor = PrimaryBlack,
                        height = 52.dp,
                        iconId = null
                    )
                }

                // main button (confirm)
                KpriPrimaryButton(
                    text = confirmText,
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    containerColor = iconContainerColor,
                    height = 52.dp,
                    iconId = null
                )
            }
        },

        containerColor = Color.White,
        shape = RoundedCornerShape(24.dp)
    )
}

// preview success
@Preview(name = "1. Success Dialog", showBackground = true)
@Composable
fun KpriDialogPreview() {
    KPRIBinaSejahteraTheme {
        KpriDialog(
            title = "Presensi Berhasil!",
            message = "Presensi masuk Anda telah berhasil tercatat pada 15:40 WIB.",
            iconId = R.drawable.ic_check,
            iconContainerColor = SuccessGreen,
            onConfirm = {}
        )
    }
}

// preview warning
@Preview(name = "2. Warning Confirmation", showBackground = true)
@Composable
fun KpriWarningDialogPreview() {
    KPRIBinaSejahteraTheme {
        KpriDialog(
            title = "Konfirmasi Keluar",
            message = "Apakah Anda yakin ingin keluar dari aplikasi? Anda harus login kembali untuk mengakses akun Anda.",
            iconId = R.drawable.ic_warn,
            iconContainerColor = ErrorRed,
            confirmText = "Ya, Keluar",
            secondaryButtonText = "Batal",
            onConfirm = {},
            onSecondaryClick = {}
        )
    }
}