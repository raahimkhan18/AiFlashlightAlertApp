package com.example.aiflashlight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "CallReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action != TelephonyManager.ACTION_PHONE_STATE_CHANGED) return

            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return
            Log.d(TAG, "Phone state changed: $state")

            val enabled = PrefsHelper.getBoolean(context, PrefsHelper.KEY_CALL_ENABLED, false)
            Log.d(TAG, "Call alert enabled: $enabled")

            if (!enabled) {
                // If disabled, make sure blinking is stopped
                if (FlashBlinkManager.isBlinking()) {
                    FlashBlinkManager.stopBlink()
                }
                return
            }

            // Initialize FlashBlinkManager with application context
            FlashBlinkManager.init(context.applicationContext)

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    val onMs = PrefsHelper.getLong(context, PrefsHelper.KEY_CALL_ON_MS, 500L)
                    val offMs = PrefsHelper.getLong(context, PrefsHelper.KEY_CALL_OFF_MS, 500L)
                    Log.d(TAG, "Starting blink - onMs: $onMs, offMs: $offMs")
                    FlashBlinkManager.startBlinkInfinite(onMs, offMs)
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK,
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    Log.d(TAG, "Stopping blink")
                    FlashBlinkManager.stopBlink()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling call state", e)
        }
    }
}
