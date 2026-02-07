package com.kpri.binasejahtera.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.data.remote.dto.ProfileResponse
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
import com.kpri.binasejahtera.ui.theme.TertiaryGray
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

@Composable
fun EditProfileScreen(
    state: ProfileResponse?,
    onNavigateBack: () -> Unit,
    onSaveProfile: (String, String, String) -> Unit,
    onUploadPhoto: (MultipartBody.Part) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }

    LaunchedEffect(state) {
        state?.let {
            name = it.name
            email = it.email
            username = it.username
        }
    }

    val context = LocalContext.current

    // buka galeri
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val file = uriToFile(context, it)
            if (file != null) {
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("photo", file.name, requestFile)
                onUploadPhoto(body)
            }
        }
    }

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
                    if (state?.profileImage != null) {
                        AsyncImage(
                            model = state.profileImage,
                            contentDescription = "Foto Profil",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(TertiaryGray),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profilepicture),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                    }
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
                        .clickable { imagePickerLauncher.launch("image/*") }
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
                modifier = Modifier.clickable { imagePickerLauncher.launch("image/*") }
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
                value = username,
                label = "Username",
                placeholder = "Masukkan username",
                iconId = R.drawable.ic_profile,
                onValueChange = { username = it },
                backgroundColor = Color.White,
                hasShadow = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            KpriPrimaryButton(
                text = "Simpan",
                onClick = {
                    onSaveProfile(name, email, username)
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

fun uriToFile(context: Context, uri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val tempFile = File.createTempFile("profile_photo", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        inputStream?.copyTo(outputStream)
        inputStream?.close()
        outputStream.close()
        tempFile
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditProfilePreview() {
    KPRIBinaSejahteraTheme {
        EditProfileScreen(
            // harus isi semua field ProfileResponse meskipun dummy
            state = ProfileResponse(
                id = 123, // ngga kepake juga di UI tp ttp harus ditulis di prev
                name = "Endra Zhafir",
                username = "endra_zhafir",
                email = "endra@email.com",
                role = "employee",
                profileImage = null,
                hasPassword = true
            ),
            onNavigateBack = {},
            onSaveProfile = { _, _, _ -> },
            onUploadPhoto = {}
        )
    }
}

@Preview(name = "Loading State", showBackground = true, showSystemUi = true)
@Composable
fun EditProfileLoadingPreview() {
    KPRIBinaSejahteraTheme {
        EditProfileScreen(
            state = null, // ketika data blm load
            onNavigateBack = {},
            onSaveProfile = { _, _, _ -> },
            onUploadPhoto = {}
        )
    }
}