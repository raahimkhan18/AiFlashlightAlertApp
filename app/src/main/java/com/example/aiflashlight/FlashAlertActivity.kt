package com.example.aiflashlight

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class FlashAlertActivity : AppCompatActivity() {

    private lateinit var flashAlertSwitch: SwitchCompat
    private lateinit var statusText: TextView

    private val requiredPermissions = mutableListOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECEIVE_SMS
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some permissions denied. Flash alerts may not work.", Toast.LENGTH_LONG).show()
            // Disable the switch if permissions not granted
            flashAlertSwitch.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flash_alert_activity)

        FlashBlinkManager.init(this)
        setupBottomNav(this)

        flashAlertSwitch = findViewById(R.id.flashAlertSwitch)
        statusText = findViewById(R.id.statusText)

        val cardIncoming = findViewById<LinearLayout>(R.id.cardIncoming)
        val cardSMS = findViewById<LinearLayout>(R.id.cardSMS)
        val cardNotify = findViewById<LinearLayout>(R.id.cardNotify)

        flashAlertSwitch.setOnCheckedChangeListener { _, isOn ->
            if (isOn && !hasRequiredPermissions()) {
                requestPermissions()
                flashAlertSwitch.isChecked = false
                return@setOnCheckedChangeListener
            }
            statusText.text = if (isOn) "Status: On" else "Status: Off"
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_CALL_ENABLED, isOn)
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_SMS_ENABLED, isOn)
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, isOn)
        }

        cardIncoming.setOnClickListener {
            if (!hasRequiredPermissions()) {
                requestPermissions()
                return@setOnClickListener
            }
            startActivity(Intent(this, IncomingCallAlertActivity::class.java))
        }

        cardSMS.setOnClickListener {
            if (!hasRequiredPermissions()) {
                requestPermissions()
                return@setOnClickListener
            }
            startActivity(Intent(this, SMSAlertActivity::class.java))
        }

        cardNotify.setOnClickListener {
            startActivity(Intent(this, NotificationAlertActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        updateSwitchState()
    }

    private fun updateSwitchState() {
        val enabled = PrefsHelper.getBoolean(this, PrefsHelper.KEY_CALL_ENABLED, false) ||
                PrefsHelper.getBoolean(this, PrefsHelper.KEY_SMS_ENABLED, false) ||
                PrefsHelper.getBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, false)

        flashAlertSwitch.setOnCheckedChangeListener(null)
        flashAlertSwitch.isChecked = enabled
        statusText.text = if (enabled) "Status: On" else "Status: Off"

        flashAlertSwitch.setOnCheckedChangeListener { _, isOn ->
            if (isOn && !hasRequiredPermissions()) {
                requestPermissions()
                flashAlertSwitch.isChecked = false
                return@setOnCheckedChangeListener
            }
            statusText.text = if (isOn) "Status: On" else "Status: Off"
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_CALL_ENABLED, isOn)
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_SMS_ENABLED, isOn)
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, isOn)
        }
    }

    private fun hasRequiredPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun requestPermissions() {
        val permissionsToRequest = requiredPermissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest)
        }
    }
}
