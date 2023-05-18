package com.lhx.glakit.utils


import android.content.Context
import android.text.format.Formatter
import java.io.*

/**
 * 缓存工具类
 */
object CacheUtils {

    // 删除缓存目录
    fun deleteCacheFolder(context: Context, runnable: Runnable?) {
        val folder = FileUtils.getImageCacheFolder(context)
        object : Thread() {
            override fun run() {
                FileUtils.deleteAllFiles(File(folder))
                if (runnable != null) {
                    ThreadUtils.runOnMainThread(runnable)
                }
            }
        }.start()
    }

    // 获取缓存大小
    fun getCacheSize(context: Context, callback: (size: String) -> Unit) {
        val folder = FileUtils.getImageCacheFolder(context)
        object : Thread() {
            override fun run() {
                val size =
                    Formatter.formatFileSize(context, FileUtils.getFileSize(File(folder)))
                ThreadUtils.runOnMainThread {
                    callback(size)
                }
            }
        }.start()
    }
}