package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
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
fun EditProfileScreen(
    onNavigateBack: () -> Unit
) {
    var name by remember { mutableStateOf("Endra Zhafir") }
    var email by remember { mutableStateOf("zhafir@example.com") }
    var phone by remember { mutableStateOf("081234567890") }

    Scaffold(
        topBar = {
            KpriTopBar(
                config = TopBarConfig.Navigation(
                    title = "Edit Informasi Pribadi",
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
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                contentAlignment = Alignment.Center
            ) {
                // pp
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(4.dp, Color.White),
                    shadowElevation = 12.dp,
                    modifier = Modifier.size(110.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profilepicture),
                        contentDescription = "Profile Picture",
                        contentScale = ContentScale.Crop
                    )
                }

                // icon camera
                Surface(
                    shape = CircleShape,
                    color = InfoBlue,
                    border = BorderStroke(2.dp, Color.White),
                    shadowElevation = 12.dp,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.BottomEnd)
                        .clickable { /* TODO */ }
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_camera),
                        contentDescription = "Ubah Foto",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // tombol ubah pp
            Surface(
                color = InfoContainer,
                shape = Shapes.small,
                shadowElevation = 12.dp,
                border = BorderStroke(1.dp, AccentBlue),
                modifier = Modifier.clickable { /* TODO */ }
            ) {
                Text(
                    text = "Ubah Foto",
                    style = MaterialTheme.typography.labelSmall,
                    color = InfoBlue,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // input form
            KpriTextField(
                value = name,
                label = "Nama Lengkap",
                placeholder = "Masukkan nama",
                iconId = R.drawable.ic_profile,
                onValueChange = { name = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            KpriTextField(
                value = email,
                label = "Email",
                placeholder = "Masukkan email",
                iconId = R.drawable.ic_mail,
                onValueChange = { email = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            KpriTextField(
                value = phone,
                label = "Nomor HP",
                placeholder = "Masukkan nomor HP",
                iconId = R.drawable.ic_phone,
                onValueChange = { phone = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            KpriPrimaryButton(
                text = "Simpan",
                onClick = { onNavigateBack() },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfilePreview() {
    KPRIBinaSejahteraTheme {
        EditProfileScreen(onNavigateBack = {})
    }
}