package com.example.aiflashlight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log

class CallReceiver : BroadcastReceiver() {

    private var ringing = false

    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (intent.action != "android.intent.action.PHONE_STATE") return
            val state = intent.getStringExtra(TelephonyManager.EXTRA_STATE) ?: return

            val enabled = PrefsHelper.getBoolean(context, PrefsHelper.KEY_CALL_ENABLED, false)
            if (!enabled) return

            when (state) {
                TelephonyManager.EXTRA_STATE_RINGING -> {
                    // start blinking until call ends (infinite)
                    val onMs = PrefsHelper.getLong(context, PrefsHelper.KEY_CALL_ON_MS, 500L)
                    val offMs = PrefsHelper.getLong(context, PrefsHelper.KEY_CALL_OFF_MS, 500L)
                    FlashBlinkManager.init(context)
                    FlashBlinkManager.startBlinkInfinite(onMs, offMs)
                    ringing = true
                }
                TelephonyManager.EXTRA_STATE_OFFHOOK -> {
                    // call answered — stop blinking
                    FlashBlinkManager.stopBlink()
                    ringing = false
                }
                TelephonyManager.EXTRA_STATE_IDLE -> {
                    // idle — stop blinking
                    FlashBlinkManager.stopBlink()
                    ringing = false
                }
            }
        } catch (e: Exception) {
            Log.e("CallReceiver", "error handling call state", e)
        }
    }
}
