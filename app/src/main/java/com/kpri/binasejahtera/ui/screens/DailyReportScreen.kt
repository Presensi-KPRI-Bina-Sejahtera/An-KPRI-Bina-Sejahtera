package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriPrimaryButton
import com.kpri.binasejahtera.ui.components.KpriTextField
import com.kpri.binasejahtera.ui.components.KpriTopBar
import com.kpri.binasejahtera.ui.components.TopBarConfig
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.*

@Composable
fun DailyReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit
) {
    var pemasukan by remember { mutableStateOf("") }
    var pengeluaran by remember { mutableStateOf("") }

    var namaAnggota by remember { mutableStateOf("") }
    var jumlahSetoran by remember { mutableStateOf("") }

    var isSimpanan by remember { mutableStateOf(true) }

    Scaffold(
        topBar = {
            KpriTopBar(
                config = TopBarConfig.Navigation(
                    title = "Laporan Harian",
                    subtitle = "Isi data sebelum pulang",
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
            Spacer(modifier = Modifier.height(12.dp))

            SectionHeader(text = "Keuangan Toko", iconId = R.drawable.ic_wallet)

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(Color.White),
                shape = Shapes.medium,
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        Shapes.medium,
                        spotColor = Color.Black.copy(0.5f)
                    )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // pemasukan
                    KpriTextField(
                        value = pemasukan,
                        label = "Pemasukan Hari Ini",
                        placeholder = "0",
                        iconId = R.drawable.ic_up,
                        iconColor = SuccessGreen,
                        onValueChange = { pemasukan = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // pengeluaran
                    KpriTextField(
                        value = pengeluaran,
                        label = "Pengeluaran Hari Ini",
                        placeholder = "0",
                        iconId = R.drawable.ic_down,
                        iconColor = ErrorRed,
                        onValueChange = { pengeluaran = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionHeader(text = "Setoran Anggota", iconId = R.drawable.ic_card)

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                colors = CardDefaults.cardColors(Color.White),
                shape = Shapes.medium,
                modifier = Modifier
                    .shadow(
                        elevation = 12.dp,
                        Shapes.medium,
                        spotColor = Color.Black.copy(0.5f)
                    )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    KpriTextField(
                        value = namaAnggota,
                        label = "Nama Anggota",
                        placeholder = "Masukkan nama...",
                        iconId = R.drawable.ic_profile,
                        onValueChange = { namaAnggota = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // tombol toggle simpanan/angsuran
                    ReportTypeToggle(
                        isSimpanan = isSimpanan,
                        onToggle = { isSimpanan = it }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // jumlah setoran
                    KpriTextField(
                        value = jumlahSetoran,
                        label = "Jumlah Setoran",
                        placeholder = "0",
                        prefixText = "Rp ",
                        onValueChange = { jumlahSetoran = it },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // tambah setoran
            KpriPrimaryButton(
                text = "Tambah Setoran Lain",
                iconId = R.drawable.ic_plus,
                onClick = { /* TODO */ },
                containerColor = Color.Transparent,
                contentColor = TertiaryGray,
                isIconStart = true,
                border = BorderStroke(1.5.dp, TertiaryGray.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // tombol lanjut
            KpriPrimaryButton(
                text = "Lanjut ke Presensi Pulang",
                iconId = R.drawable.ic_arrow_right,
                onClick = onNavigateNext,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(text: String, iconId: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            painter = painterResource(id = iconId),
            contentDescription = null,
            tint = TertiaryGray,
            modifier = Modifier.size(16.dp)
        )

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = TertiaryGray,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun ReportTypeToggle(
    isSimpanan: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        // simpanan
        TypeButton(
            text = "Simpanan",
            isSelected = isSimpanan,
            color = InfoBlue,
            modifier = Modifier.weight(1f),
            onClick = { onToggle(true) }
        )

        Spacer(modifier = Modifier.width(12.dp))

        // angsuran
        TypeButton(
            text = "Angsuran",
            isSelected = !isSimpanan,
            color = InfoBlue,
            modifier = Modifier.weight(1f),
            onClick = { onToggle(false) }
        )
    }
}

@Composable
fun TypeButton(
    text: String,
    isSelected: Boolean,
    color: Color,
    modifier: Modifier,
    onClick: () -> Unit
) {
    Surface(
        shape = Shapes.small,
        color = if (isSelected) color.copy(alpha = 0.1f) else Color.White,
        border = BorderStroke(
            width = 1.dp,
            color = if (isSelected) color else TertiaryGray.copy(alpha = 0.3f)
        ),
        modifier = modifier
            .height(40.dp)
            .clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // radio
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .then(
                        if (isSelected) {
                            Modifier.background(color)
                        } else {
                            Modifier
                                .border(
                                    2.dp,
                                    TertiaryGray.copy(alpha = 0.5f),
                                    CircleShape)
                                .background(Color.Transparent)
                        }
                    )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = if (isSelected) color else TertiaryGray,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DailyReportPreview() {
    KPRIBinaSejahteraTheme {
        DailyReportScreen(onNavigateBack = {}, onNavigateNext = {})
    }
}