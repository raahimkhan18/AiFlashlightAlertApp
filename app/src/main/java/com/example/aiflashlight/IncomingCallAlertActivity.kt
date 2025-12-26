package com.example.aiflashlight

import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class IncomingCallAlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call_alert)

        FlashBlinkManager.init(this)

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        val alertSwitch = findViewById<SwitchCompat>(R.id.alertSwitch)
        val statusTxt = findViewById<TextView>(R.id.statusTxt)

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
}
