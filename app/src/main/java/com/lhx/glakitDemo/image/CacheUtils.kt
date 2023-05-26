package com.lhx.glakitDemo.image

import android.content.Context
import android.text.format.Formatter
import com.bumptech.glide.Glide
import com.lhx.glakit.base.widget.ValueCallback
import com.lhx.glakit.base.widget.VoidCallback
import com.lhx.glakit.utils.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 缓存工具类
 */
object CacheUtils {

    // 删除缓存目录
    fun deleteCacheFolder(context: Context, completion: VoidCallback?) {
        CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
            Glide.get(context).clearDiskCache()
            withContext(Dispatchers.Main) {
                if (completion != null) {
                    completion()
                }
            }
        }
    }

    // 获取缓存大小
    fun getCacheSize(context: Context, completion: ValueCallback<String>?) {
        CoroutineScope(SupervisorJob()).launch(Dispatchers.IO) {
            val file = Glide.get(context).getCacheDirectory()
            val size = Formatter.formatFileSize(context, FileUtils.getFileSize(file))
            withContext(Dispatchers.Main) {
                if (completion != null) {
                    completion(size)
                }
            }
        }
    }
}