package com.example.otp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.otp.ui.theme.OTPTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen() {
    var password by remember {
        mutableStateOf("")
    }

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(text = "OTP Compose Demo")
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = {
                    Text(text = "Password:")
                },
                singleLine = true
            )
        }
    }

    SmsOtpReader { otp ->
        password = otp
    }
}

@Preview
@Composable
private fun OtpScreenPreview() {
    OTPTheme {
        OtpScreen()
    }
}
