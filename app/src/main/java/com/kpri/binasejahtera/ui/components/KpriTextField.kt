package com.kpri.binasejahtera.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.ui.theme.KPRIBinaSejahteraTheme
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.TertiaryGray

@Composable
fun KpriTextField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String,
    iconId: Int,
    isPassword: Boolean = false,
    iconSize: Dp = 20.dp
) {
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
            leadingIcon = {
                Icon(
                    painter = painterResource(id = iconId),
                    modifier = Modifier.size(iconSize),
                    contentDescription = null,
                    tint = TertiaryGray
                )
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryBlack,
                unfocusedBorderColor = TertiaryGray,
                cursorColor = PrimaryBlack
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun KpriTextFieldPreview() {
    KPRIBinaSejahteraTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            KpriTextField(
                value = "",
                label = "Email",
                placeholder = "Masukkan Email",
                iconId = com.kpri.binasejahtera.R.drawable.ic_mail,
                onValueChange = {}
            )
        }
    }
}