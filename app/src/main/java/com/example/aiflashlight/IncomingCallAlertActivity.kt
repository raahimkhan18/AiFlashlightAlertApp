package com.example.aiflashlight

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat

class IncomingCallAlertActivity : AppCompatActivity() {

    private lateinit var alertSwitch: SwitchCompat
    private lateinit var statusTxt: TextView

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.all { it.value }
        if (allGranted) {
            alertSwitch.isChecked = true
            statusTxt.text = "Status: On"
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_CALL_ENABLED, true)
        } else {
            Toast.makeText(this, "Permissions required for call alerts", Toast.LENGTH_LONG).show()
            alertSwitch.isChecked = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call_alert)

        FlashBlinkManager.init(this)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        alertSwitch = findViewById(R.id.alertSwitch)
        statusTxt = findViewById(R.id.statusTxt)

        val seekOn = findViewById<SeekBar>(R.id.seekOn)
        val seekOff = findViewById<SeekBar>(R.id.seekOff)
        val valueOn = findViewById<TextView>(R.id.valueOn)
        val valueOff = findViewById<TextView>(R.id.valueOff)

        backBtn.setOnClickListener { finish() }

        val enabled = PrefsHelper.getBoolean(this, PrefsHelper.KEY_CALL_ENABLED, false)
        val onMs = PrefsHelper.getLong(this, PrefsHelper.KEY_CALL_ON_MS, 500L)
        val offMs = PrefsHelper.getLong(this, PrefsHelper.KEY_CALL_OFF_MS, 500L)

        alertSwitch.isChecked = enabled
        statusTxt.text = if (enabled) "Status: On" else "Status: Off"

        seekOn.max = 150
        seekOff.max = 150
        seekOn.progress = (onMs / 10).toInt()
        seekOff.progress = (offMs / 10).toInt()

        valueOn.text = String.format("%.2fs", seekOn.progress / 100.0)
        valueOff.text = String.format("%.2fs", seekOff.progress / 100.0)

        alertSwitch.setOnCheckedChangeListener { _, checked ->
            if (checked && !hasRequiredPermissions()) {
                requestPermissions()
                alertSwitch.isChecked = false
                return@setOnCheckedChangeListener
            }
            statusTxt.text = if (checked) "Status: On" else "Status: Off"
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_CALL_ENABLED, checked)
        }

        seekOn.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, b: Boolean) {
                valueOn.text = String.format("%.2fs", p / 100.0)
                PrefsHelper.setLong(this@IncomingCallAlertActivity, PrefsHelper.KEY_CALL_ON_MS, (p * 10).toLong())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        seekOff.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, b: Boolean) {
                valueOff.text = String.format("%.2fs", p / 100.0)
                PrefsHelper.setLong(this@IncomingCallAlertActivity, PrefsHelper.KEY_CALL_OFF_MS, (p * 10).toLong())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }

    private fun hasRequiredPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        val permissionsToRequest = mutableListOf<String>()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CAMERA)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_PHONE_STATE)
        }
        if (permissionsToRequest.isNotEmpty()) {
            permissionLauncher.launch(permissionsToRequest.toTypedArray())
        }
    }
}
