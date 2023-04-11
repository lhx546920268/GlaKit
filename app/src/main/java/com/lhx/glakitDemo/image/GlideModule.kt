package com.lhx.glakitDemo.image

import android.content.Context
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.cache.DiskCache
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory.CacheDirectoryGetter
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.module.AppGlideModule
import java.io.File

//build.gradle 中添加依赖
//kapt 'com.github.bumptech.glide:compiler:4.11.0'
@GlideModule
class GlideModule: AppGlideModule() {

    override fun isManifestParsingEnabled(): Boolean {
        return false
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        //自定义缓存
        val diskCacheFactory = InternalCacheDiskCacheFactory(context)
        ImageManager.init(diskCacheFactory)
        builder.setDiskCache(diskCacheFactory)
    }
}

//外部存储，最大500MB
class ExternalCacheDiskCacheFactory(val context: Context): DiskLruCacheFactory( CacheDirectoryGetter {
    val cacheDir = context.externalCacheDir
    if (cacheDir == null) {
        null
    } else {
        File(cacheDir, DEFAULT_DISK_CACHE_DIR)
    }
}, 500 * 1024 * 1024) {

    private var diskCache: DiskCache? = null

    override fun build(): DiskCache? {
        if (diskCache == null) {
            diskCache = super.build()
        }
        return diskCache
    }
}