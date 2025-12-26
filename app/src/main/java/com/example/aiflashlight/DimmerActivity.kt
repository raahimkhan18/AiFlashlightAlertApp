package com.example.aiflashlight

import android.hardware.camera2.CameraManager
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity

class DimmerActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private lateinit var brightnessSeek: SeekBar
    private var brightnessLevel = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dimmer)

        // Make this overlay full-screen and translucent
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        cameraManager = getSystemService(CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]
        brightnessSeek = findViewById(R.id.brightnessSeek)

        brightnessSeek.progress = brightnessLevel

        brightnessSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, fromUser: Boolean) {
                brightnessLevel = value
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val level = (value / 100f)
                        cameraManager.turnOnTorchWithStrengthLevel(cameraId, (level * 100).toInt().coerceAtLeast(1))
                    } else {
                        cameraManager.setTorchMode(cameraId, value > 0)
                    }
                } catch (e: Exception) { e.printStackTrace() }
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })
    }

    // Tap anywhere outside slider to close
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        finish()
        return true
    }
}
