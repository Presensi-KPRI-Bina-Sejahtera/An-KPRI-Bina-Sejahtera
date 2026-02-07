package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.*

@Composable
fun KpriTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    iconId: Int? = null,
    prefixText: String? = null,
    isPassword: Boolean = false,
    iconSize: Dp = 20.dp,
    iconColor: Color = TertiaryGray,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    backgroundColor: Color = TertiaryGray.copy(alpha = 0.05f),
    hasShadow: Boolean = false
) {

    var isPasswordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryBlack
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.labelMedium,
                    color = TertiaryGray
                )
            },
            leadingIcon = if (iconId != null) {
                {
                    Icon(
                        painter = painterResource(id = iconId),
                        modifier = Modifier.size(iconSize),
                        contentDescription = null,
                        tint = iconColor
                    )
                }
            } else if (prefixText != null) {
                {
                    Text(
                        text = prefixText,
                        style = MaterialTheme.typography.labelMedium,
                        color = TertiaryGray
                    )
                }
            } else null,

            // password eye
            trailingIcon = if (isPassword) {
                {
                    val image = if (isPasswordVisible)
                        R.drawable.ic_eye_open
                    else
                        R.drawable.ic_eye_closed

                    IconButton(
                        onClick = { isPasswordVisible = !isPasswordVisible }
                    ) {
                        Icon(
                            painter = painterResource(id = image),
                            contentDescription = if (isPasswordVisible) "Hide Password" else "Show Password",
                            tint = TertiaryGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            } else null,

            visualTransformation = if (isPassword && !isPasswordVisible) {
                PasswordVisualTransformation()
            } else {
                VisualTransformation.None
            },

            keyboardOptions = keyboardOptions,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (hasShadow) Modifier
                        .shadow(
                            elevation = 12.dp,
                            Shapes.medium,
                            spotColor = Color.Black.copy(0.5f)
                        ) else Modifier
                ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlack,
                unfocusedBorderColor = TertiaryGray.copy(alpha = 0.3f),
                focusedContainerColor = backgroundColor,
                unfocusedContainerColor = backgroundColor,
                cursorColor = PrimaryBlack
            )
        )
    }
}

@Preview(
    name = "1. Default",
    showBackground = true
)
@Composable
fun KpriTextFieldDefaultPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriTextField(
                value = "",
                label = "Email",
                placeholder = "Masukkan Email",
                iconId = R.drawable.ic_mail,
                onValueChange = {}
            )
        }
    }
}

@Preview(
    name = "2. Password Input (Closed)",
    showBackground = true
)
@Composable
fun KpriTextFieldPasswordClosedPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriTextField(
                value = "rahasia123",
                label = "Password",
                placeholder = "Masukkan Password",
                iconId = R.drawable.ic_lock,
                isPassword = true,
                onValueChange = {}
            )
        }
    }
}

@Preview(
    name = "3. Card Style",
    showBackground = true,
    backgroundColor = 0xFFEEEEEE
)
@Composable
fun KpriTextFieldShadowPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriTextField(
                value = "",
                label = "Nama Lengkap",
                placeholder = "Endra Zhafir",
                iconId = R.drawable.ic_profile,
                onValueChange = {},
                backgroundColor = Color.White,
                hasShadow = true
            )
        }
    }
}