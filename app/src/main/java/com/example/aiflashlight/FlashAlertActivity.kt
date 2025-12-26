package com.example.aiflashlight

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class FlashAlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flash_alert_activity)

        FlashBlinkManager.init(this)
        setupBottomNav(this)

        val flashAlertSwitch = findViewById<SwitchCompat>(R.id.flashAlertSwitch)
        val statusText = findViewById<TextView>(R.id.statusText)

        val cardIncoming = findViewById<LinearLayout>(R.id.cardIncoming)
        val cardSMS = findViewById<LinearLayout>(R.id.cardSMS)
        val cardNotify = findViewById<LinearLayout>(R.id.cardNotify)

        // load saved setting
        val enabled = PrefsHelper.getBoolean(this, PrefsHelper.KEY_CALL_ENABLED, false) ||
                PrefsHelper.getBoolean(this, PrefsHelper.KEY_SMS_ENABLED, false) ||
                PrefsHelper.getBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, false)

        flashAlertSwitch.isChecked = enabled
        statusText.text = if (enabled) "Status: On" else "Status: Off"

        flashAlertSwitch.setOnCheckedChangeListener { _, isOn ->
            statusText.text = if (isOn) "Status: On" else "Status: Off"
            // This master switch could be wired to global preference if you want.
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_CALL_ENABLED, isOn)
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_SMS_ENABLED, isOn)
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, isOn)
        }

        cardIncoming.setOnClickListener {
            startActivity(Intent(this, IncomingCallAlertActivity::class.java))
        }

        cardSMS.setOnClickListener {
            startActivity(Intent(this, SMSAlertActivity::class.java))
        }

        cardNotify.setOnClickListener {
            startActivity(Intent(this, NotificationAlertActivity::class.java))
        }
    }
}
