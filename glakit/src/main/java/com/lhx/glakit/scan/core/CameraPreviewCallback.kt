package com.lhx.glakit.scan.core

import android.media.ImageReader
import android.os.Handler

/**
 * 相机预览回调
 */
@Suppress("deprecation")
class CameraPreviewCallback(val handler: Handler, val cameraManager: CameraManager)
    : android.hardware.Camera.PreviewCallback,
    ImageReader.OnImageAvailableListener {

    //解码器
    private val scanDecoder by lazy { ScanDecoder() }

    //解码线程
    private var scanThread: ScanThread? = null

    //是否正在解码
    private var decoding = false

    //开始解码
    fun startDecode() {
        if (scanThread == null) {
            scanThread = ScanThread(handler, scanDecoder, cameraManager)
            scanThread!!.start()
        }
        decoding = false
    }

    //停止解码
    fun stopDecode() {
        if (scanThread != null) {
            scanThread!!.stopDecode()
            scanThread = null
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onPreviewFrame(data: ByteArray?, camera: android.hardware.Camera?) {
        if (!cameraManager.isAutoFocusing()) {
            cameraManager.autoFocus()
        }
        decodeData(data)
    }

    override fun onImageAvailable(reader: ImageReader) {
        val image = reader.acquireLatestImage()
        val buffer = image.planes.firstOrNull()?.buffer
        if (buffer != null) {
            val data = ByteArray(buffer.capacity())
            buffer.get(data)
            decodeData(data)
        }
        image?.close()
    }

    private fun decodeData(data: ByteArray?) {
        if (!decoding && scanThread != null) {
            decoding = true
            scanThread!!.decode(data)
        }
    }
}