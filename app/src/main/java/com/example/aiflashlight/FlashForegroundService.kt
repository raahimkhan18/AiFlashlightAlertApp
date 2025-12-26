package com.example.aiflashlight

import android.app.*
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class FlashForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "flash_channel"

        const val EXTRA_ON_MS = "onMs"
        const val EXTRA_OFF_MS = "offMs"
        const val EXTRA_COUNT = "count"
        const val EXTRA_INFINITE = "infinite"
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Flash Alert Active")
            .setContentText("Blinking enabled")
            .setSmallIcon(R.drawable.app_logo)
            .build()

        startForeground(1, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        FlashBlinkManager.init(this)

        val onMs = intent?.getLongExtra(EXTRA_ON_MS, 500L) ?: 500L
        val offMs = intent?.getLongExtra(EXTRA_OFF_MS, 500L) ?: 500L
        val count = intent?.getIntExtra(EXTRA_COUNT, 0) ?: 0
        val infinite = intent?.getBooleanExtra(EXTRA_INFINITE, false) ?: false

        if (infinite) {
            FlashBlinkManager.startBlinkInfinite(onMs, offMs)
        } else {
            FlashBlinkManager.startBlinkCount(count, onMs, offMs)
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        FlashBlinkManager.stopBlink()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Flash Alerts",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
