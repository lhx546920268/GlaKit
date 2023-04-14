package com.lhx.glakit.scan.core

import android.content.Context
import android.content.res.Configuration
import android.graphics.SurfaceTexture
import com.lhx.glakit.utils.Size

internal abstract class Camera(val cameraManager: CameraManager, val callBack: CallBack) {

    val surface: SurfaceTexture get() = cameraManager.surface
    val surfaceWidth: Int get() = cameraManager.surfaceWidth
    val surfaceHeight: Int get() = cameraManager.surfaceHeight
    val previewCallback: CameraPreviewCallback get() = cameraManager.previewCallback
    val context: Context get() = cameraManager.fragment.requireContext()

    //获取屏幕方向 判断是否是竖屏
    fun isPortrait(): Boolean {
        val configuration = context.resources.configuration
        return configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    }

    abstract fun open()

    abstract fun start()

    abstract fun stop()

    abstract fun getPreviewSize(): Size?

    abstract fun getPreviewFormat(): Int?

    //获取预览格式字符串
    abstract fun getPreviewFormatString(): String?

    abstract fun setOpenLamp(open: Boolean): Boolean

    abstract fun setOptimalPreviewSize(width: Int, height: Int)

    interface CallBack {

        fun onCameraOpen(success: Boolean)
    }
}