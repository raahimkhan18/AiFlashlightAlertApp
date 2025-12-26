package com.example.aiflashlight

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class MyNotificationListenerService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        FlashBlinkManager.init(this)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        try {
            val enabled = PrefsHelper.getBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, false)
            if (!enabled) return

            val onMs = PrefsHelper.getLong(this, PrefsHelper.KEY_NOTIFY_ON_MS, 500L)
            val offMs = PrefsHelper.getLong(this, PrefsHelper.KEY_NOTIFY_OFF_MS, 500L)

            // notification policy: blink 3 times (as requested)
            FlashBlinkManager.startBlinkCount(3, onMs, offMs)
        } catch (e: Exception) {
            Log.e("MyNotificationService", "error", e)
        }
    }
}
