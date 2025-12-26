package com.example.aiflashlight

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.*
import android.widget.RelativeLayout

class ScreenLightActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen_light)

        val view = findViewById<RelativeLayout>(R.id.colorView)

        view.setOnTouchListener { _, event ->
            val x = event.x / view.width
            val y = event.y / view.height
            val color = Color.HSVToColor(floatArrayOf(x * 360, 1f, y))
            view.setBackgroundColor(color)
            true
        }
    }
}
