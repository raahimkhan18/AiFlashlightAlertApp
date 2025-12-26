package com.example.aiflashlight

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraManager
import android.os.*
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String
    private var handler = Handler(Looper.getMainLooper())
    private var isFlashOn = false

    private lateinit var sensorManager: SensorManager
    private lateinit var glowAnimation: Animation
    private lateinit var flashBtn: ImageView
    private lateinit var flashStatusText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FlashBlinkManager.init(this)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        flashBtn = findViewById(R.id.flashButton)
        flashStatusText = findViewById(R.id.flashStatusText)
        val sosSeek = findViewById<SeekBar>(R.id.sosSeek)
        val morseBtn = findViewById<Button>(R.id.morseBtn)
        val screenLightBtn = findViewById<Button>(R.id.screenLightBtn)
        val dimmerBtn = findViewById<Button>(R.id.dimmerBtn)

        glowAnimation = AnimationUtils.loadAnimation(this, R.anim.glow)

        setupBottomNav(this)

        // Default state
        flashStatusText.text = "ON"
        flashStatusText.setTextColor(Color.GREEN)
        flashBtn.startAnimation(glowAnimation)
        cameraManager.setTorchMode(cameraId, false)

        flashBtn.setOnClickListener { toggleFlashlight() }
        dimmerBtn.setOnClickListener { showDimmerDialog() }

        sosSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, value: Int, b: Boolean) {
                if (value > 0) startSOS(value) else stopSOS()
            }
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
        })

        morseBtn.setOnClickListener {
            startActivity(Intent(this, MorseActivity::class.java))
        }

        screenLightBtn.setOnClickListener {
            startActivity(Intent(this, ScreenLightActivity::class.java))
        }

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
    }

    private fun toggleFlashlight() {
        isFlashOn = !isFlashOn
        try {
            cameraManager.setTorchMode(cameraId, isFlashOn)
        } catch (e: CameraAccessException) { e.printStackTrace() }

        if (isFlashOn) {
            flashStatusText.text = "OFF"
            flashStatusText.setTextColor(Color.RED)
            flashBtn.clearAnimation()
        } else {
            flashStatusText.text = "ON"
            flashStatusText.setTextColor(Color.GREEN)
            flashBtn.startAnimation(glowAnimation)
        }
    }

    private fun showDimmerDialog() {
        val intent = Intent(this, DimmerActivity::class.java)
        startActivity(intent)
    }

    private fun startSOS(speed: Int) {
        val delay = (1000 / speed)
        handler.post(object : Runnable {
            override fun run() {
                isFlashOn = !isFlashOn
                cameraManager.setTorchMode(cameraId, isFlashOn)
                handler.postDelayed(this, delay.toLong())
            }
        })
    }

    private fun stopSOS() {
        handler.removeCallbacksAndMessages(null)
        cameraManager.setTorchMode(cameraId, false)
        isFlashOn = false
        flashStatusText.text = "ON"
        flashStatusText.setTextColor(Color.GREEN)
        flashBtn.startAnimation(glowAnimation)
    }

    override fun onSensorChanged(event: SensorEvent?) {}
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
