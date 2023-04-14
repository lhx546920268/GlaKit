package com.lhx.glakit.scan.core

import android.os.Handler

@Suppress("deprecation")
class CameraAutoFocusCallback: android.hardware.Camera.AutoFocusCallback {

    //自动聚焦间隔
    private val autoFocusInterval: Long = 15000

    //延迟
    private var handler: Handler? = null

    @Deprecated("Deprecated in Java")
    override fun onAutoFocus(success: Boolean, camera: android.hardware.Camera?) {
        if (handler != null) {
            handler!!.sendEmptyMessageDelayed(CameraHandler.MESSAGE_AUTO_FOCUS, autoFocusInterval)
        }
    }

    //开始聚焦
    fun startFocus(handler: Handler) {
        this.handler = handler
    }

    //是否已聚焦
    fun isAutoFocusing(): Boolean {
        return handler != null
    }

    //停止聚焦
    fun stopFocus() {
        if (handler != null) {
            handler!!.removeMessages(CameraHandler.MESSAGE_AUTO_FOCUS)
            handler = null
        }
    }
}