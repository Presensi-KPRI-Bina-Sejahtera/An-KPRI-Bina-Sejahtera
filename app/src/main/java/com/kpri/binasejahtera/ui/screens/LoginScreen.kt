package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriPrimaryButton
import com.kpri.binasejahtera.ui.components.KpriTextField
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.SecondaryGray
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun LoginScreen(
    onLoginClick: (String, String) -> Unit,
    onGoogleSignInClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    val loginCardShape = Shapes.medium.copy(
        bottomStart = CornerSize(0.dp),
        bottomEnd = CornerSize(0.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBackground),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // logo
            Image(
                painter = painterResource(id = R.drawable.img_logo_koperasi_2),
                contentDescription = "Logo Koperasi",
                modifier = Modifier
                    .size(200.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Presensi KPRI\nBina Sejahtera",
                style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                color = PrimaryBlack,
                textAlign = TextAlign.Center
            )
        }

        // form login
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 12.dp,
                    shape = loginCardShape,
                    spotColor = Color.Black.copy(alpha = 0.5f)
                )
                .weight(1f),
            shape = loginCardShape,
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.displayLarge,
                    color = PrimaryBlack,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Silakan masukkan kredensial Anda untuk mengakses aplikasi.",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
                    color = TertiaryGray,
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // input email
                KpriTextField(
                    value = email,
                    label = "Email",
                    placeholder = "Masukkan email Anda",
                    iconId = R.drawable.ic_mail,
                    onValueChange = { email = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // input password
                KpriTextField(
                    value = password,
                    label = "Password",
                    placeholder = "Masukkan password Anda",
                    iconId = R.drawable.ic_lock,
                    isPassword = true,
                    onValueChange = { password = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // tombol login
                KpriPrimaryButton(
                    text = "Masuk",
                    onClick = { onLoginClick(email, password) },
                    iconId = R.drawable.ic_in,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // garis divider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        color = TertiaryGray
                    )

                    Text(
                        text = "Or continue with",
                        style = MaterialTheme.typography.labelMedium,
                        color = SecondaryGray,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                    HorizontalDivider(
                        modifier = Modifier
                            .weight(1f),
                        color = TertiaryGray
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // tombol login google
                OutlinedButton(
                    onClick = onGoogleSignInClick,
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, TertiaryGray),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = PrimaryBlack
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.Unspecified
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Masuk dengan Google",
                        style = MaterialTheme.typography.bodyLarge,
                        color = PrimaryBlack
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                Spacer(modifier = Modifier.navigationBarsPadding())

            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    KPRIBinaSejahteraTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onGoogleSignInClick = {}
        )
    }
}