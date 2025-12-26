package com.example.aiflashlight

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class NotificationAlertActivity : AppCompatActivity() {

    private lateinit var backBtn: ImageView
    private lateinit var alertSwitch: SwitchCompat
    private lateinit var statusTxt: TextView
    private lateinit var seekOn: SeekBar
    private lateinit var seekOff: SeekBar
    private lateinit var valueOn: TextView
    private lateinit var valueOff: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification_alert)

        FlashBlinkManager.init(this)

        backBtn = findViewById(R.id.backBtn)
        alertSwitch = findViewById(R.id.alertSwitch)
        statusTxt = findViewById(R.id.statusTxt)
        seekOn = findViewById(R.id.seekOn)
        seekOff = findViewById(R.id.seekOff)
        valueOn = findViewById(R.id.valueOn)
        valueOff = findViewById(R.id.valueOff)

        backBtn.setOnClickListener { finish() }

        val enabled = PrefsHelper.getBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, false)
        val onMs = PrefsHelper.getLong(this, PrefsHelper.KEY_NOTIFY_ON_MS, 500L)
        val offMs = PrefsHelper.getLong(this, PrefsHelper.KEY_NOTIFY_OFF_MS, 500L)

        alertSwitch.isChecked = enabled
        statusTxt.text = if (enabled) "Status: On" else "Status: Off"

        seekOn.max = 150
        seekOff.max = 150
        seekOn.progress = (onMs / 10).toInt()
        seekOff.progress = (offMs / 10).toInt()

        valueOn.text = String.format("%.2fs", seekOn.progress / 100.0)
        valueOff.text = String.format("%.2fs", seekOff.progress / 100.0)

        alertSwitch.setOnCheckedChangeListener { _, checked ->
            statusTxt.text = if (checked) "Status: On" else "Status: Off"
            PrefsHelper.setBoolean(this, PrefsHelper.KEY_NOTIFY_ENABLED, checked)
        }

        seekOn.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, b: Boolean) {
                valueOn.text = String.format("%.2fs", p / 100.0)
                PrefsHelper.setLong(this@NotificationAlertActivity, PrefsHelper.KEY_NOTIFY_ON_MS, (p * 10).toLong())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        seekOff.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, p: Int, b: Boolean) {
                valueOff.text = String.format("%.2fs", p / 100.0)
                PrefsHelper.setLong(this@NotificationAlertActivity, PrefsHelper.KEY_NOTIFY_OFF_MS, (p * 10).toLong())
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })
    }
}
