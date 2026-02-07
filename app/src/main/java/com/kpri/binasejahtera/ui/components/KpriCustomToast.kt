package com.kpri.binasejahtera.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.ErrorRed
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.SuccessGreen
import kotlinx.coroutines.delay

enum class ToastType {
    SUCCESS, ERROR
}

data class ToastData(
    val message: String,
    val type: ToastType
)

object ToastManager {
    val toastEvent = mutableStateOf<ToastData?>(null)

    fun show(message: String, type: ToastType) {
        toastEvent.value = ToastData(message, type)
    }
}

@Composable
fun KpriCustomToastHost() {
    val toastData = ToastManager.toastEvent.value
    var isVisible by remember { mutableStateOf(false) }

    LaunchedEffect(toastData) {
        if (toastData != null) {
            isVisible = true
            delay(3000)
            isVisible = false
            delay(500)
            if (ToastManager.toastEvent.value == toastData) {
                ToastManager.toastEvent.value = null
            }
        }
    }

    // slide animation dari atas layar
    AnimatedVisibility(
        visible = isVisible && toastData != null,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, start = 24.dp, end = 24.dp)
            .zIndex(99f)
    ) {
        toastData?.let { data ->
            KpriToastContent(data = data)
        }
    }
}

// UI component (dipisah biar bisa di preview)
@Composable
fun KpriToastContent(data: ToastData) {
    val backgroundColor = if (data.type == ToastType.SUCCESS) SuccessGreen else ErrorRed
    val iconRes = if (data.type == ToastType.SUCCESS) R.drawable.ic_check else R.drawable.ic_warn

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = backgroundColor.copy(alpha = 0.9f),
                shape = RoundedCornerShape(12.dp)
            )
            .border(1.dp, backgroundColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = data.message,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Preview(name = "Toast - Success", showBackground = true)
@Composable
fun KpriCustomToastSuccessPreview() {
    KPRIBinaSejahteraTheme {
        KpriToastContent(
            data = ToastData("Login berhasil!", ToastType.SUCCESS)
        )
    }
}

@Preview(name = "Toast - Error", showBackground = true)
@Composable
fun KpriCustomToastErrorPreview() {
    KPRIBinaSejahteraTheme {
        KpriToastContent(
            data = ToastData("Email atau password salah", ToastType.ERROR)
        )
    }
}