package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack

@Composable
fun MainScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_logo_koperasi_4),
                contentDescription = "Logo KPRI",
                modifier = Modifier.size(225.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "KPRI",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 32.sp),
                color = PrimaryBlack
            )

            Text(
                text = "Bina Sejahtera",
                style = MaterialTheme.typography.labelMedium.copy(fontSize = 24.sp),
                color = PrimaryBlack
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    KPRIBinaSejahteraTheme {
        MainScreen()
    }
}