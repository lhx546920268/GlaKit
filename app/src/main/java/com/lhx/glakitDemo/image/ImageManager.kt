package com.lhx.glakitDemo.image

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.StreamEncoder
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

//写入图片文件
private class DataCacheWriter(
    private val encoder: StreamEncoder,
    private val data: InputStream,
    private val options: Options
) :
    DiskCache.Writer {
    override fun write(file: File): Boolean {
        return encoder.encode(data, file, options)
    }
}

//图片管理
object ImageManager {

    private var diskCacheFactory: DiskCache.Factory? = null
    private var diskCache: DiskCache? = null

    fun init(diskCacheFactory: DiskCache.Factory) {
        this.diskCacheFactory = diskCacheFactory
    }

    //把图片存储到glide缓存
    fun storeImage(context: Context, file: File, url: String) {
        if (diskCacheFactory == null)
            return
        if (file.exists()) {
            var inputStream: InputStream? = null
            try {
                if (diskCache == null) {
                    diskCache = diskCacheFactory?.build()
                }
                val encoder = StreamEncoder(Glide.get(context).arrayPool)
                inputStream = FileInputStream(file)
                diskCache?.put(GlideUrl(url), DataCacheWriter(encoder, inputStream, Options()))
            }catch (e: Exception) {

            }finally {
                inputStream?.close()
            }
        }
    }
}

