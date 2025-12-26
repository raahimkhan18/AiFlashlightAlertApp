package com.example.aiflashlight

import android.content.Context
import android.content.SharedPreferences

object PrefsHelper {
    private const val PREFS = "ai_flash_prefs"

    // keys (call/sms/notify)
    const val KEY_CALL_ENABLED = "key_call_enabled"
    const val KEY_CALL_ON_MS = "key_call_on_ms"
    const val KEY_CALL_OFF_MS = "key_call_off_ms"

    const val KEY_SMS_ENABLED = "key_sms_enabled"
    const val KEY_SMS_ON_MS = "key_sms_on_ms"
    const val KEY_SMS_OFF_MS = "key_sms_off_ms"

    const val KEY_NOTIFY_ENABLED = "key_notify_enabled"
    const val KEY_NOTIFY_ON_MS = "key_notify_on_ms"
    const val KEY_NOTIFY_OFF_MS = "key_notify_off_ms"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun setBoolean(context: Context, key: String, value: Boolean) {
        prefs(context).edit().putBoolean(key, value).apply()
    }
    fun getBoolean(context: Context, key: String, default: Boolean = false): Boolean =
        prefs(context).getBoolean(key, default)

    fun setLong(context: Context, key: String, value: Long) {
        prefs(context).edit().putLong(key, value).apply()
    }
    fun getLong(context: Context, key: String, default: Long = 500L): Long =
        prefs(context).getLong(key, default)
}
