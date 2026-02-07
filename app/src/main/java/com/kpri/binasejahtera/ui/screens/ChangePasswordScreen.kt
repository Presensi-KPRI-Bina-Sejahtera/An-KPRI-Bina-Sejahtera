package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriPrimaryButton
import com.kpri.binasejahtera.ui.components.KpriTextField
import com.kpri.binasejahtera.ui.components.KpriTopBar
import com.kpri.binasejahtera.ui.components.TopBarConfig
import com.kpri.binasejahtera.ui.theme.AccentBlue
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.InfoBlue
import com.kpri.binasejahtera.ui.theme.InfoContainer
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.Shapes

@Composable
fun ChangePasswordScreen(
    onNavigateBack: () -> Unit,
    onSavePassword: (current: String, new: String, confirm: String) -> Unit
) {
    var currentPass by remember { mutableStateOf("") }
    var newPass by remember { mutableStateOf("") }
    var confirmPass by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            KpriTopBar(
                config = TopBarConfig.Navigation(
                    title = "Atur Kata Sandi",
                    onBackClick = onNavigateBack
                )
            )
        },
        containerColor = AppBackground
    ) {
        innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // info box
            Surface(
                color = InfoContainer,
                shape = Shapes.small,
                border = BorderStroke(1.dp, AccentBlue),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Password baru harus terdiri dari minimal 8 karakter, mengandung huruf besar, huruf kecil, dan angka.",
                    style = MaterialTheme.typography.labelMedium,
                    color = InfoBlue,
                    modifier = Modifier
                        .padding(16.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // input form
            KpriTextField(
                value = currentPass,
                label = "Password Saat Ini",
                placeholder = "Masukkan password lama",
                iconId = R.drawable.ic_lock,
                isPassword = true,
                onValueChange = { currentPass = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            KpriTextField(
                value = newPass,
                label = "Password Baru",
                placeholder = "Masukkan password baru",
                iconId = R.drawable.ic_lock,
                isPassword = true,
                onValueChange = { newPass = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            KpriTextField(
                value = confirmPass,
                label = "Konfirmasi Password",
                placeholder = "Ulangi password baru",
                iconId = R.drawable.ic_lock,
                isPassword = true,
                onValueChange = { confirmPass = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // tombol update
            KpriPrimaryButton(
                text = "Update Password",
                onClick = {
                    onSavePassword(currentPass, newPass, confirmPass)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChangePasswordPreview() {
    KPRIBinaSejahteraTheme {
        ChangePasswordScreen(
            onNavigateBack = {},
            onSavePassword = { current, new, confirm ->
                // simulasi callback, kosongin aja
            }
        )
    }
}