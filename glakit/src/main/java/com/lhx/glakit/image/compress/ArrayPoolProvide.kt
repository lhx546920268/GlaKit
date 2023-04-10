package com.lhx.glakit.image.compress

import android.content.ContentResolver
import android.net.Uri
import java.io.Closeable
import java.io.FileInputStream
import java.io.InputStream
import java.util.concurrent.ConcurrentHashMap

class ArrayPoolProvide {

    companion object {
        val sharedProvider: ArrayPoolProvide by lazy { ArrayPoolProvide() }
    }

    /**
     * Uri对应的BufferedInputStreamWrap缓存Key
     */
    private val keyCache = HashSet<String>()

    /**
     * Uri对应的BufferedInputStreamWrap缓存数据
     */
    private val bufferedLruCache: ConcurrentHashMap<String, BufferedInputStreamWrap?> =
        ConcurrentHashMap<String, BufferedInputStreamWrap?>()

    /**
     * byte[]数组的缓存队列
     */
    private val arrayPool: LruArrayPool = LruArrayPool(LruArrayPool.DEFAULT_SIZE)

    /**
     * 获取相应的byte数组
     *
     * @param bufferSize
     */
    operator fun get(bufferSize: Int): ByteArray {
        return arrayPool[bufferSize, ByteArray::class.java]
    }

    /**
     * 缓存相应的byte数组
     *
     * @param buffer
     */
    fun put(buffer: ByteArray?) {
        arrayPool.put(buffer)
    }

    /**
     * ContentResolver openInputStream
     *
     * @param resolver ContentResolver
     * @param uri      data
     * @return
     */
    fun openInputStream(resolver: ContentResolver, uri: Uri): InputStream? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap?
        try {
            bufferedInputStreamWrap = bufferedLruCache[uri.toString()]
            if (bufferedInputStreamWrap != null) {
                bufferedInputStreamWrap.reset()
            } else {
                bufferedInputStreamWrap = wrapInputStream(resolver, uri)
            }
        } catch (e: Exception) {
            bufferedInputStreamWrap = try {
                return resolver.openInputStream(uri)
            } catch (exception: Exception) {
                exception.printStackTrace()
                wrapInputStream(resolver, uri)
            }
        }
        return bufferedInputStreamWrap
    }

    /**
     * open real path FileInputStream
     *
     * @param path data
     * @return
     */
    fun openInputStream(path: String): InputStream? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap?
        try {
            bufferedInputStreamWrap = bufferedLruCache[path]
            if (bufferedInputStreamWrap != null) {
                bufferedInputStreamWrap.reset()
            } else {
                bufferedInputStreamWrap = wrapInputStream(path)
            }
        } catch (e: Exception) {
            bufferedInputStreamWrap = wrapInputStream(path)
        }
        return bufferedInputStreamWrap
    }

    /**
     * BufferedInputStreamWrap
     *
     * @param resolver ContentResolver
     * @param uri      data
     */
    private fun wrapInputStream(resolver: ContentResolver, uri: Uri): BufferedInputStreamWrap? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap? = null
        try {
            bufferedInputStreamWrap = BufferedInputStreamWrap(resolver.openInputStream(uri))
            val available: Int = bufferedInputStreamWrap.available()
            bufferedInputStreamWrap.mark(if (available > 0) available else BufferedInputStreamWrap.DEFAULT_MARK_READ_LIMIT)
            bufferedLruCache[uri.toString()] = bufferedInputStreamWrap
            keyCache.add(uri.toString())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bufferedInputStreamWrap
    }

    /**
     * BufferedInputStreamWrap
     *
     * @param path data
     */
    private fun wrapInputStream(path: String): BufferedInputStreamWrap? {
        var bufferedInputStreamWrap: BufferedInputStreamWrap? = null
        try {
            bufferedInputStreamWrap = BufferedInputStreamWrap(FileInputStream(path))
            val available: Int = bufferedInputStreamWrap.available()
            bufferedInputStreamWrap.mark(if (available > 0) available else BufferedInputStreamWrap.DEFAULT_MARK_READ_LIMIT)
            bufferedLruCache[path] = bufferedInputStreamWrap
            keyCache.add(path)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return bufferedInputStreamWrap
    }


    /**
     * 清空内存占用
     */
    fun clearMemory() {
        for (key in keyCache) {
            val inputStreamWrap: BufferedInputStreamWrap? = bufferedLruCache[key]
            close(inputStreamWrap)
            bufferedLruCache.remove(key)
        }
        keyCache.clear()
        arrayPool.clearMemory()
    }

    fun close(c: Closeable?) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c is Closeable) {
            try {
                c.close()
            } catch (e: Exception) {
                // silence
            }
        }
    }
}