package com.example.aiflashlight

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log

class SMSReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            val bundle: Bundle? = intent.extras
            if (bundle == null) return
            val pdus = bundle["pdus"] as? Array<*> ?: return
            // Build message(s) (not used here much, but needed to confirm an SMS arrived)
            val messages = pdus.mapNotNull { pdu ->
                SmsMessage.createFromPdu(pdu as ByteArray)
            }

            val enabled = PrefsHelper.getBoolean(context, PrefsHelper.KEY_SMS_ENABLED, false)
            if (!enabled) return

            val onMs = PrefsHelper.getLong(context, PrefsHelper.KEY_SMS_ON_MS, 500L)
            val offMs = PrefsHelper.getLong(context, PrefsHelper.KEY_SMS_OFF_MS, 500L)

            // SMS requirement: blink 5 times
            FlashBlinkManager.init(context)
            FlashBlinkManager.startBlinkCount(5, onMs, offMs)
        } catch (e: Exception) {
            Log.e("SMSReceiver", "error parsing sms", e)
        }
    }
}
