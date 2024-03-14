package com.example.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LifecycleStartEffect
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status

@Composable
fun SmsOtpReader(onOtpReceived: (String) -> Unit) {
    val context = LocalContext.current

    LifecycleStartEffect {
        val activity = context.findActivity()

        val smsRetriever = SmsRetriever.getClient(activity)
        smsRetriever.startSmsRetriever()

        val broadcastReceiver = OtpBroadcastReceiver(onOtpReceived)
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        ContextCompat.registerReceiver(
            activity,
            broadcastReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )

        onStopOrDispose {
            activity.unregisterReceiver(broadcastReceiver)
        }
    }
}

class OtpBroadcastReceiver(private val onOtpReceived: (String) -> Unit) : BroadcastReceiver() {

    @Suppress("DEPRECATION") // modern methods (getParcelable) can throw NPE
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent == null) {
            return
        }

        if (intent.action != SmsRetriever.SMS_RETRIEVED_ACTION) {
            return
        }

        val extras = intent.extras ?: return
        val status = extras[SmsRetriever.EXTRA_STATUS] as Status? ?: return

        if (status.statusCode == CommonStatusCodes.SUCCESS) {
            val message = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String
            OTP_REGEX.find(message)?.let { match ->
                onOtpReceived(match.value)
            }
        }
    }

    companion object {
        private val OTP_REGEX = "\\d{5,}".toRegex()
    }
}
