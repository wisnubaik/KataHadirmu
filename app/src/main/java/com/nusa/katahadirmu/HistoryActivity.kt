package com.nusa.katahadirmu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusa.katahadirmu.ui.theme.KataHadirmuTheme
import java.text.SimpleDateFormat
import java.util.Locale

class HistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KataHadirmuTheme {
                HistoryScreen()
            }
        }
    }
}

@Composable
fun HistoryScreen() {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("AbsensiPrefs", Context.MODE_PRIVATE)

    // Ambil daftar absensi yang tersimpan di SharedPreferences
    val absensiList = sharedPreferences.getStringSet("absensiList", mutableSetOf())?.toList() ?: listOf()

    // Ambil username dari SharedPreferences yang disimpan saat login
    val username = sharedPreferences.getString("username", "Wisnu") ?: "Wisnu"  // Default "Wisnu" jika tidak ada

    // Fungsi untuk mengonversi tanggal string menjadi timestamp (Long)
    fun parseTimestamp(dateStr: String): Long {
        return try {
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val date = dateFormat.parse(dateStr)
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    // Urutkan absensi berdasarkan timestamp dari tanggal (yang terbaru di atas)
    val sortedAbsensiList = absensiList.sortedWith(compareByDescending { absensi ->
        val absensiDetails = absensi.split("|")
        val date = absensiDetails[2] // Ambil tanggal dari detail absensi
        parseTimestamp(date)  // Ubah tanggal ke timestamp untuk pengurutan
    })

    // Variabel untuk mengontrol tampilnya dialog
    var showDialog by remember { mutableStateOf(false) }

    // Mengatur aksi tombol back
    BackHandler {
        showDialog = true
    }

    // Dialog konfirmasi keluar aplikasi
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Konfirmasi Keluar", color = Color(0xFF2424AC)) },
            text = { Text("Apakah Anda yakin ingin keluar aplikasi?", color = Color.Black) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        (context as? ComponentActivity)?.finishAffinity()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2424AC))
                ) {
                    Text("Ya", color = Color.White)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2424AC))
                ) {
                    Text("Tidak", color = Color.White)
                }
            },
            shape = RoundedCornerShape(8.dp),
            containerColor = Color.White
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = "Riwayat Absensi",
                fontSize = 20.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Pemilik Absensi: $username",
                fontSize = 16.sp,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                if (sortedAbsensiList.isEmpty()) {
                    item {
                        Text(
                            text = "Tidak Ada Presensi",
                            fontSize = 18.sp,
                            color = Color.Gray,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                } else {
                    items(sortedAbsensiList) { absensi ->
                        val absensiDetails = absensi.split("|")
                        val absensiCode = absensiDetails[0]
                        val status = "Berhasil☑"
                        val date = absensiDetails[2]
                        val time = absensiDetails[3]
                        val capturedImage = absensiDetails[4]

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .border(2.dp, Color.Black, RoundedCornerShape(8.dp))
                                .background(Color.LightGray.copy(alpha = 0.1f)),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                            ) {
                                Text("Nama : $username", fontSize = 16.sp, color = Color.Black)
                                Text("Kode Absensi: $absensiCode", fontSize = 16.sp, color = Color.Black)
                                Text("Status Kehadiran: $status", fontSize = 16.sp, color = Color.Black)

                                if (capturedImage.isNotEmpty()) {
                                    Text(text = "$capturedImage", fontSize = 14.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val intent = Intent(context, MainActivity::class.java)
                    context.startActivity(intent)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF2424AC)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2424AC))
            ) {
                Text(text = "Absen Baru", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    (context as? ComponentActivity)?.onBackPressed()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF0000)),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000))
            ) {
                Text(text = "Keluar", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}