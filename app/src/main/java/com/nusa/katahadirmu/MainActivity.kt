package com.nusa.katahadirmu

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nusa.katahadirmu.ui.theme.KataHadirmuTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KataHadirmuTheme {
                LoginScreen { username, password ->
                    if (username == "aaa" && password == "aaa") {
                        // Simpan username ke SharedPreferences
                        val sharedPreferences = getSharedPreferences("AbsensiPrefs", Context.MODE_PRIVATE)
                        with(sharedPreferences.edit()) {
                            putString("username", username)
                            apply()
                        }
                        // Beralih ke halaman AbsensiActivity setelah login berhasil
                        val intent = Intent(this, AbsensiActivity::class.java)
                        startActivity(intent)
                    } else {
                        showToast("Username atau Password salah")
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(onLogin: (String, String) -> Unit) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val lindenHillFont = FontFamily(Font(R.font.lindenhill_regular))
    val livvicFont = FontFamily(Font(R.font.livvic_italic))

    val (usernameFocusRequester, passwordFocusRequester) = remember { FocusRequester.createRefs() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color(0xFF5E4F7))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(520.dp)
                .align(Alignment.BottomCenter)
                .background(
                    color = Color.Gray.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(topStart = 48.dp, topEnd = 48.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "App Logo",
                modifier = Modifier
                    .size(175.dp)
                    .clip(RectangleShape)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 50.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Selamat datang di KataHadirmu!",
                fontSize = 24.sp,
                fontFamily = livvicFont,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-120).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Login",
                    fontSize = 32.sp,
                    fontFamily = lindenHillFont,
                    color = Color.Black,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .offset(y = (-36).dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            BasicTextField(
                value = username,
                onValueChange = { username = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(56.dp)
                    .focusRequester(usernameFocusRequester),
                decorationBox = { innerTextField ->
                    Surface(
                        color = Color.Gray.copy(alpha = 0.1f),
                        shape = MaterialTheme.shapes.small
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (username.isEmpty()) Text("Username", color = Color.Gray)
                            innerTextField()
                        }
                    }
                },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(
                    onNext = { passwordFocusRequester.requestFocus() }
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(end = 40.dp)
                        .focusRequester(passwordFocusRequester),
                    decorationBox = { innerTextField ->
                        Surface(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (password.isEmpty()) Text("Password", color = Color.Gray)
                                innerTextField()
                            }
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(
                        onDone = { onLogin(username, password) }
                    )
                )

                IconButton(
                    onClick = { passwordVisible = !passwordVisible },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(28.dp)
                        .padding(end = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(id = if (passwordVisible) R.drawable.ic_visibility else R.drawable.ic_visibility_off),
                        contentDescription = "Toggle password visibility",
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onLogin(username, password) },
                modifier = Modifier
                    .width(200.dp)
                    .height(56.dp)
                    .clip(RectangleShape),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2424AC))
            ) {
                Text(text = "KIRIM")
            }
        }
    }
}
