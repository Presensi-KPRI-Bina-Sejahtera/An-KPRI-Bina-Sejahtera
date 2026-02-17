package com.kpri.binasejahtera.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.unit.dp
import com.kpri.binasejahtera.R
import com.kpri.binasejahtera.ui.components.KpriPrimaryButton
import com.kpri.binasejahtera.ui.components.KpriTextField
import com.kpri.binasejahtera.ui.components.KpriTopBar
import com.kpri.binasejahtera.ui.components.ToastManager
import com.kpri.binasejahtera.ui.components.ToastType
import com.kpri.binasejahtera.ui.components.TopBarConfig
import com.kpri.binasejahtera.ui.theme.AppBackground
import com.kpri.binasejahtera.ui.theme.ErrorRed
import com.kpri.binasejahtera.ui.theme.PrimaryBlack
import com.kpri.binasejahtera.ui.theme.Shapes
import com.kpri.binasejahtera.ui.theme.SuccessGreen
import com.kpri.binasejahtera.ui.theme.TertiaryGray
import com.kpri.binasejahtera.ui.viewmodel.AttendanceViewModel
import com.kpri.binasejahtera.ui.viewmodel.DepositDraftItem

@Composable
fun DailyReportScreen(
    onNavigateBack: () -> Unit,
    onNavigateNext: () -> Unit,
    viewModel: AttendanceViewModel
) {
    val draftState by viewModel.reportDraft.collectAsState()

    var pemasukan by remember { mutableStateOf(draftState.pemasukan) }
    var keterangan_pemasukan by remember { mutableStateOf(draftState.keterangan_pemasukan) }
    var pengeluaran by remember { mutableStateOf(draftState.pengeluaran) }
    var keterangan_pengeluaran by remember { mutableStateOf(draftState.keterangan_pengeluaran) }


    val depositList = remember {
        mutableStateListOf<DepositDraftItem>().apply {
            if (draftState.deposits.isNotEmpty()) {
                addAll(draftState.deposits)
            } else {
                add(DepositDraftItem(id = System.currentTimeMillis()))
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            viewModel.updateReportDraft(
                pemasukan,
                keterangan_pemasukan,
                pengeluaran,
                keterangan_pengeluaran,
                depositList.toList())
        }
    }

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

            // keuangan toko
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

                    KpriTextField(
                        value = keterangan_pemasukan ?: "",
                        label = "Keterangan Pemasukan",
                        placeholder = "Keterangan (Opsional)",
                        onValueChange = { keterangan_pemasukan = it },
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

                    Spacer(modifier = Modifier.height(12.dp))

                    KpriTextField(
                        value = keterangan_pengeluaran ?: "",
                        label = "Keterangan Pengeluaran",
                        placeholder = "Keterangan (Opsional)",
                        onValueChange = { keterangan_pengeluaran = it },
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            SectionHeader(text = "Setoran Anggota", iconId = R.drawable.ic_card)

            depositList.forEachIndexed { index, deposit ->

                Spacer(modifier = Modifier.height(12.dp))

                DepositCard(
                    state = deposit,
                    isExtraCard = index > 0,
                    onRemove = { depositList.removeAt(index) },
                    onNameChange = { deposit.name = it },
                    onAmountChange = { deposit.amount = it },
                    onTypeChange = { deposit.isSimpanan = it }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // tambah setoran
            KpriPrimaryButton(
                text = "Tambah Setoran Lain",
                iconId = R.drawable.ic_plus,
                onClick = { depositList.add(DepositDraftItem(id = System.currentTimeMillis())) },
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
                iconId = R.drawable.ic_arrow_go,
                onClick = {
                    // pemasukan/pengeluaran harus diisi, tapi boleh nol.
                    if (pemasukan.isBlank() || pengeluaran.isBlank()) {
                        ToastManager.show("Harap isi Pemasukan dan Pengeluaran (Isi 0 jika tidak ada)", ToastType.ERROR)
                        return@KpriPrimaryButton
                    }

                    val hasInvalidInput = depositList.any { item ->
                        val isNameFilled = item.name.isNotBlank()
                        val isAmountFilled = item.amount.isNotBlank()

                        // ini bakalan error klo nama diisi tp jumlah kosong (dan sebaliknya). tapi deposit ttp opsional
                        (isNameFilled && !isAmountFilled) || (!isNameFilled && isAmountFilled)
                    }

                    if (hasInvalidInput) {
                        ToastManager.show("Data Setoran tidak lengkap. Harap isi Nama & Jumlah, atau kosongkan keduanya.", ToastType.ERROR)
                        return@KpriPrimaryButton
                    }

                    // simpen datanya dulu sblm navigasi
                    viewModel.updateReportDraft(
                        pemasukan,
                        keterangan_pemasukan,
                        pengeluaran,
                        keterangan_pengeluaran,
                        depositList.toList())

                    onNavigateNext()
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionHeader(text: String, iconId: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(PrimaryBlack),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = PrimaryBlack
        )
    }
}

@Composable
fun DepositCard(
    state: DepositDraftItem,
    isExtraCard: Boolean,
    onRemove: () -> Unit,
    onNameChange: (String) -> Unit,
    onAmountChange: (String) -> Unit,
    onTypeChange: (Boolean) -> Unit
) {
    var localName by remember { mutableStateOf(state.name) }
    var localAmount by remember { mutableStateOf(state.amount) }
    var localIsSimpanan by remember { mutableStateOf(state.isSimpanan) }

    Card(
        colors = CardDefaults.cardColors(Color.White),
        shape = Shapes.medium,
        modifier = Modifier.shadow(12.dp, Shapes.medium, spotColor = Color.Black.copy(0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (isExtraCard) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.TopEnd) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Hapus",
                        tint = PrimaryBlack,
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onRemove() }
                    )
                }
            }

            KpriTextField(
                value = localName,
                label = "Nama Anggota",
                placeholder = "Masukkan nama...",
                iconId = R.drawable.ic_profile,
                onValueChange = {
                    localName = it
                    state.name = it
                    onNameChange(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ReportTypeToggle(
                isSimpanan = localIsSimpanan,
                onToggle = {
                    localIsSimpanan = it
                    state.isSimpanan = it
                    onTypeChange(it)
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            KpriTextField(
                value = localAmount,
                label = "Jumlah Setoran",
                placeholder = "0",
                prefixText = "Rp ",
                onValueChange = {
                    localAmount = it
                    state.amount = it
                    onAmountChange(it)
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
    }
}

@Composable
fun ReportTypeToggle(
    isSimpanan: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column {
        Text(
            text = "Jenis Setoran",
            style = MaterialTheme.typography.bodyLarge,
            color = PrimaryBlack
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(Shapes.medium)
                .background(TertiaryGray.copy(alpha = 0.05f))
                .border(1.dp, TertiaryGray.copy(alpha = 0.1f), Shapes.medium)
                .padding(4.dp)
        ) {
            ToggleButton(
                text = "Simpanan",
                isSelected = isSimpanan,
                onClick = { onToggle(true) },
                modifier = Modifier.weight(1f)
            )
            ToggleButton(
                text = "Angsuran",
                isSelected = !isSimpanan,
                onClick = { onToggle(false) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
fun ToggleButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        color = if (isSelected) Color.White else Color.Transparent,
        shape = Shapes.medium,
        modifier = modifier.height(36.dp),
        shadowElevation = if (isSelected) 4.dp else 0.dp
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = if (isSelected) PrimaryBlack else TertiaryGray,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

// preview ku hapus. dah malas error mulu gara-gara hilt wkwkwk