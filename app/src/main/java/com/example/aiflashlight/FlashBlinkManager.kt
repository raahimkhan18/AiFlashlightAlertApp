package com.example.aiflashlight

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Handler
import android.os.Looper
import android.util.Log

object FlashBlinkManager {
    private const val TAG = "FlashBlinkManager"

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private val handler = Handler(Looper.getMainLooper())
    private var runnable: Runnable? = null
    private var isBlinking = false
    private var isInitialized = false

    fun init(context: Context) {
        if (isInitialized && cameraManager != null && cameraId != null) return
        try {
            val appContext = context.applicationContext
            cameraManager = appContext.getSystemService(Context.CAMERA_SERVICE) as CameraManager
            cameraId = cameraManager?.cameraIdList?.firstOrNull { id ->
                try {
                    cameraManager?.getCameraCharacteristics(id)
                        ?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) == true
                } catch (e: Exception) {
                    false
                }
            }
            isInitialized = cameraId != null
            Log.d(TAG, "init: cameraId=$cameraId, initialized=$isInitialized")
        } catch (e: Exception) {
            Log.e(TAG, "init error", e)
            cameraId = null
            isInitialized = false
        }
    }

    fun startBlinkInfinite(onMs: Long = 500L, offMs: Long = 500L) {
        if (cameraId == null) {
            Log.w(TAG, "startBlinkInfinite: cameraId null; call init(context) first")
            return
        }
        stopBlink()
        isBlinking = true
        var state = false
        runnable = object : Runnable {
            override fun run() {
                if (!isBlinking) return
                state = !state
                setTorch(state)
                val delay = if (state) onMs else offMs
                handler.postDelayed(this, delay)
            }
        }
        handler.post(runnable!!)
    }

    fun startBlinkCount(count: Int, onMs: Long = 500L, offMs: Long = 500L) {
        if (cameraId == null) {
            Log.w(TAG, "startBlinkCount: cameraId null; call init(context) first")
            return
        }
        if (count <= 0) return
        stopBlink()
        isBlinking = true
        var remaining = count * 2
        var state = false
        runnable = object : Runnable {
            override fun run() {
                if (!isBlinking) return
                if (remaining <= 0) {
                    stopBlink()
                    return
                }
                state = !state
                setTorch(state)
                remaining--
                val delay = if (state) onMs else offMs
                handler.postDelayed(this, delay)
            }
        }
        handler.post(runnable!!)
    }

    fun stopBlink() {
        isBlinking = false
        runnable?.let { handler.removeCallbacks(it); runnable = null }
        setTorch(false)
    }

    private fun setTorch(on: Boolean) {
        try {
            cameraId?.let { id -> cameraManager?.setTorchMode(id, on) }
        } catch (e: CameraAccessException) {
            Log.e(TAG, "setTorch error", e)
        } catch (e: Exception) {
            Log.e(TAG, "setTorch unexpected error", e)
        }
    }

    fun isBlinking(): Boolean = isBlinking
}
