package com.example.aiflashlight

import android.content.Context
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.Button
import android.widget.EditText

class MorseActivity : AppCompatActivity() {

    private lateinit var cameraManager: CameraManager
    private lateinit var cameraId: String

    private val morse = mapOf(
        'a' to ".-", 'b' to "-...", 'c' to "-.-.", 'd' to "-..", 'e' to ".", 'f' to "..-.",
        'g' to "--.", 'h' to "....", 'i' to "..", 'j' to ".---", 'k' to "-.-", 'l' to ".-..",
        'm' to "--", 'n' to "-.", 'o' to "---", 'p' to ".--.", 'q' to "--.-", 'r' to ".-.",
        's' to "...", 't' to "-", 'u' to "..-", 'v' to "...-", 'w' to ".--", 'x' to "-..-",
        'y' to "-.--", 'z' to "--.."
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_morse)

        cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = cameraManager.cameraIdList[0]

        val input = findViewById<EditText>(R.id.inputText)
        val send = findViewById<Button>(R.id.sendBtn)

        send.setOnClickListener {
            val text = input.text.toString().lowercase()
            val sequence = text.map { morse[it] ?: "" }.joinToString(" ")
            transmit(sequence)
        }
    }

    private fun transmit(seq: String){
        Thread {
            for (c in seq){
                when(c){
                    '.' -> flash(200)
                    '-' -> flash(600)
                    ' ' -> Thread.sleep(300)
                }
            }
        }.start()
    }

    private fun flash(d: Long){
        cameraManager.setTorchMode(cameraId, true)
        Thread.sleep(d)
        cameraManager.setTorchMode(cameraId, false)
        Thread.sleep(200)
    }
}
