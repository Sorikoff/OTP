package com.example.otp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.otp.ui.theme.OTPTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpScreen() {
    var password by remember {
        mutableStateOf("")
    }
    var checked by remember {
        mutableStateOf(false)
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
            OtpInputForm(
                password = password,
                onPasswordChanged = { password = it },
                checked = checked,
                onCheckedChanged = { checked = it }
            )
        }
    }

    SmsOtpReader { otp ->
        password = otp
    }
}

@Composable
private fun OtpInputForm(
    password: String,
    onPasswordChanged: (String) -> Unit,
    checked: Boolean,
    onCheckedChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChanged,
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            label = {
                Text(text = stringResource(id = R.string.password_label))
            },
            singleLine = true
        )
        Row(
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            val checkedTermsText = stringResource(id = R.string.markdown_example_checked)
            val uncheckedTermsText = stringResource(id = R.string.markdown_example_unchecked)
            val processedTermsText by remember(key1 = checked) {
                mutableStateOf(
                    linkifyMarkdown(
                        input = if (checked) {
                            checkedTermsText
                        } else {
                            uncheckedTermsText
                        }
                    )
                )
            }

            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChanged
            )
            LinkifiedText(linksData = processedTermsText)
        }
    }
}

@Preview
@Composable
private fun OtpScreenPreview() {
    OTPTheme {
        OtpScreen()
    }
}
