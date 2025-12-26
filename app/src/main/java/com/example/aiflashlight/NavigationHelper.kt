package com.example.aiflashlight

import android.app.Activity
import android.content.Intent
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat

fun setupBottomNav(activity: Activity) {

    val navFlashAlert = activity.findViewById<LinearLayout>(R.id.navFlashAlert)
    val navFlashlight = activity.findViewById<LinearLayout>(R.id.navFlashlight)
    val navSettings = activity.findViewById<LinearLayout>(R.id.navSettings)

    highlightSelected(activity)

    navFlashAlert.setOnClickListener {
        if (activity !is FlashAlertActivity) {
            activity.startActivity(Intent(activity, FlashAlertActivity::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            activity.finish()
        }
    }

    navFlashlight.setOnClickListener {
        if (activity !is MainActivity) {
            activity.startActivity(Intent(activity, MainActivity::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            activity.finish()
        }
    }

    navSettings.setOnClickListener {
        if (activity !is SettingsActivity) {
            activity.startActivity(Intent(activity, SettingsActivity::class.java))
            activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            activity.finish()
        }
    }
}

private fun highlightSelected(activity: Activity) {

    val items = listOf(
        Triple(R.id.navFlashAlert, R.drawable.ic_alert, FlashAlertActivity::class),
        Triple(R.id.navFlashlight, R.drawable.ic_flashlight1, MainActivity::class),
        Triple(R.id.navSettings, R.drawable.ic_settings, SettingsActivity::class)
    )

    for ((id, _, screen) in items) {
        val layout = activity.findViewById<LinearLayout>(id)
        val img = layout.getChildAt(0) as ImageView
        val txt = layout.getChildAt(1) as TextView

        if (activity::class == screen) {
            img.setColorFilter(ContextCompat.getColor(activity, R.color.nav_selected))
            txt.setTextColor(ContextCompat.getColor(activity, R.color.nav_selected))
        } else {
            img.setColorFilter(ContextCompat.getColor(activity, R.color.nav_unselected))
            txt.setTextColor(ContextCompat.getColor(activity, R.color.nav_unselected))
        }
    }
}
