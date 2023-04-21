package com.lhx.glakit.scan.core

import android.os.Handler
import android.os.Looper

/**
 * 扫描线程
 */
@Suppress("deprecation")
class ScanThread(val handler: Handler, val scanDecoder: ScanDecoder, val cameraManager: CameraManager): Thread() {

    //运行环，防止线程被系统杀掉
    private var looper : Looper? = null

    //是否已停止
    private var stopped = false

    override fun run() {
        Looper.prepare()
        looper = Looper.myLooper()
        Looper.loop()
    }

    //停止解码
    fun stopDecode(){
        stopped = true
        if(looper != null){
            looper!!.quit()
            looper = null
        }
    }

    fun decode(data: ByteArray?) {
        val rect = cameraManager.getScanRect()!!
        val size = cameraManager.getPreviewSize()
        if (size == null) {
            if (!stopped) handler.sendEmptyMessage(CameraHandler.MESSAGE_DECODE_FAIL)
            return
        }

        val result = scanDecoder.decode(data, rect, size.width, size.height)
        if(!stopped){
            if (result != null) {
                //如果解析结果不为空，就是解析成功了，则发送成功消息，将结果放到message中
                val message = handler.obtainMessage(CameraHandler.MESSAGE_DECODE_SUCCESS)
                message.obj = result
                message.sendToTarget()
            } else {
                //解码失败
                handler.sendEmptyMessage(CameraHandler.MESSAGE_DECODE_FAIL)
            }
        }
    }
}