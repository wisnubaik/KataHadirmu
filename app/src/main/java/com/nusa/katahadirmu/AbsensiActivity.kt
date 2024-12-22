package com.nusa.katahadirmu

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.compose.setContent
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.core.content.ContextCompat
import com.nusa.katahadirmu.ui.theme.KataHadirmuTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class AbsensiActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KataHadirmuTheme {
                AbsensiScreen(
                    onSubmit = { imageBitmap, absensiCode ->
                        if (absensiCode.text == "ABSEN2024") {
                            saveAbsensiToSharedPreferences(imageBitmap, absensiCode.text)

                            val intent = Intent(this, HistoryActivity::class.java)
                            startActivity(intent)
                        }
                    },
                    onViewHistory = {
                        val intent = Intent(this, HistoryActivity::class.java)
                        startActivity(intent)
                    }
                )
            }
        }
    }

    private fun saveAbsensiToSharedPreferences(imageBitmap: Bitmap?, absensiCode: String) {
        val sharedPreferences = getSharedPreferences("AbsensiPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()

        val imageString = imageBitmap?.let { bitmapToBase64(it) } ?: ""
        val currentDate = getCurrentDate()
        val currentTime = getCurrentTime()
        val currentTimestamp = getCurrentTimestamp()

        val absensiList = sharedPreferences.getStringSet("absensiList", mutableSetOf())?.toMutableSet() ?: mutableSetOf()

        val absensiData = "$absensiCode|Absensi Tersubmit|$currentDate|$currentTime|$currentTimestamp|$imageString"
        absensiList.add(absensiData)

        editor.putStringSet("absensiList", absensiList)
        editor.apply()
    }

    private fun getCurrentDate(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDateTime.format(formatter)
    }

    private fun getCurrentTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    private fun getCurrentTimestamp(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        return ""
    }
}

@Composable
fun AbsensiScreen(
    onSubmit: (Bitmap, TextFieldValue) -> Unit,
    onViewHistory: () -> Unit
) {
    val capturedImage = remember { mutableStateOf<Bitmap?>(null) }
    val absensiCode = remember { mutableStateOf(TextFieldValue("")) }
    val isCodeValid = absensiCode.value.text == "ABSEN2024"
    val context = LocalContext.current

    // Kamera launcher
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) {
            capturedImage.value = bitmap
        }
    }

    // Permission untuk kamera
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch()
        } else {
            Toast.makeText(context, "Tindakan ini memerlukan izin kamera", Toast.LENGTH_SHORT).show()
        }
    }

    val checkAndRequestCameraPermission = {
        when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
            PackageManager.PERMISSION_GRANTED -> cameraLauncher.launch()
            else -> cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Format tanggal dan waktu saat ini
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")
    val formattedDateTime = currentDateTime.format(formatter)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Header(title = "Halaman Absensi")

            Spacer(modifier = Modifier.height(16.dp))

            // Tanggal dan waktu saat berhasil masuk
            Text(
                text = "Berhasil Masuk: $formattedDateTime",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Pesan instruksi
            Text(
                text = "Jangan lupa sertakan bukti foto!",
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Menampilkan gambar yang tertangkap
            capturedImage.value?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Captured Image",
                    modifier = Modifier
                        .size(200.dp)
                        .padding(16.dp)
                )
            }

            // Tombol untuk mengambil gambar atau mengganti gambar
            Button(
                onClick = { checkAndRequestCameraPermission() },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .width(200.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000000))
            ) {
                Text(
                    text = if (capturedImage.value == null) "Ambil Foto" else "Ganti Foto",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input untuk kode absensi dengan simbol centang saat menekan Enter
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .padding(8.dp)
                    .height(50.dp)
                    .background(Color.LightGray)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                BasicTextField(
                    value = absensiCode.value,
                    onValueChange = { newValue ->
                        absensiCode.value = newValue
                    },
                    textStyle = TextStyle(color = Color.Black, fontSize = 16.sp),
                    cursorBrush = SolidColor(Color.Black),
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (absensiCode.value.text.isEmpty()) {
                                Text(
                                    text = "Masukkan kode absensi",
                                    color = Color.Gray,
                                    fontSize = 16.sp
                                )
                            }
                            innerTextField()
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            // Mengganti tombol Enter dengan simbol centang saat menekan 'Enter'
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    )
                )
            }

            // Menampilkan pesan kesalahan jika kode absensi salah
            if (!isCodeValid && absensiCode.value.text.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Kode absensi salah!", color = Color.Red, fontSize = 14.sp)
            }
        }

        // Tombol-tombol bagian bawah
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { capturedImage.value?.let { onSubmit(it, absensiCode.value) } },
                enabled = capturedImage.value != null && isCodeValid,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2424AC))
            ) {
                Text(
                    text = "Kirim Absensi",
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onViewHistory() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
            ) {
                Text(
                    text = "Lihat Rekap Riwayat Saja",
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun Header(title: String) {
    Text(
        text = title,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF2424AC),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        textAlign = androidx.compose.ui.text.style.TextAlign.Center
    )
}

