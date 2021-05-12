package com.lhx.glakit.utils

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import kotlin.math.floor

/**
 * 图片工具类
 */
object ImageUtils {

    data class Size(val width: Int, val height: Int)

    /**
    通过给定的大小，获取等比例缩小后的尺寸，如果maxWidth或者maxHeight小于等于0，则缩小相对的值，
    比如maxWidth=0，则缩小height的值
     *@return 返回要缩小的尺寸
     */
    fun fitSize(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Size {
        return fitSize(bitmap.width, bitmap.height, maxWidth, maxHeight)
    }

    fun fitSize(width: Int, height: Int, maxWidth: Int, maxHeight: Int): Size {

        if (width == height) {
            val value = width.coerceAtMost(if (maxWidth > maxHeight) maxHeight else maxWidth)
            return Size(value, value)
        } else {

            var w = width.toDouble()
            var h = height.toDouble()
            val heightScale: Double =
                if (maxHeight > 0) height.toDouble() / maxHeight.toDouble() else 0.0
            val widthScale: Double =
                if (maxWidth > 0) width.toDouble() / maxWidth.toDouble() else 0.0

            if (heightScale != 0.0 && widthScale != 0.0) {
                if (height >= maxHeight && width >= maxWidth) {
                    if (heightScale > widthScale) {
                        h = floor(height.toDouble() / heightScale)
                        w = floor(width.toDouble() / heightScale)
                    } else {
                        h = floor(height.toDouble() / widthScale)
                        w = floor(width.toDouble() / widthScale)
                    }
                } else {
                    if (height >= maxHeight && width <= maxWidth) {
                        h = floor(height.toDouble() / heightScale)
                        w = floor(width.toDouble() / heightScale)
                    } else if (height <= maxHeight && width >= maxWidth) {
                        h = floor(height.toDouble() / widthScale)
                        w = floor(width.toDouble() / widthScale)
                    }
                }
            } else if (heightScale == 0.0) {
                h = floor(height.toDouble() / widthScale)
                w = floor(width.toDouble() / widthScale)
            } else {
                h = floor(height.toDouble() / heightScale)
                w = floor(width.toDouble() / heightScale)
            }
            return Size(w.toInt(), h.toInt())
        }
    }

    /**
     * 缩放图片
     */
    fun scaleDown(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val size = fitSize(bitmap.width, bitmap.height, maxWidth, maxHeight)
        return Bitmap.createScaledBitmap(bitmap, size.width, size.height, true)
    }

    /**
     * 生成临时图片文件
     * @param quality 压缩质量 0 - 100
     * @param keepAlpha 是否保留透明通道，只有PNG才有效
     */
    fun writeToFile(
        context: Context,
        bitmap: Bitmap,
        quality: Int,
        keepAlpha: Boolean = false
    ): File? {
        val format =
            if (keepAlpha && bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG
        return writeToFileInternal(context, bitmap, format, quality)
    }

    fun writeToFile(
        context: Context,
        bitmaps: Array<Bitmap>,
        quality: Int,
        keepAlpha: Boolean = false,
        failBitmaps: ArrayList<Bitmap>? = null
    ): List<File> {
        val files = ArrayList<File>()
        for (bitmap in bitmaps) {
            val file = writeToFile(context, bitmap, quality, keepAlpha)
            if (file != null) {
                files.add(file)
            } else {
                failBitmaps?.add(bitmap)
            }
        }
        return files
    }

    private fun writeToFileInternal(
        context: Context,
        bitmap: Bitmap,
        format: Bitmap.CompressFormat,
        quality: Int
    ): File? {
        val extension = if (format == Bitmap.CompressFormat.PNG) "png" else "jpeg"
        val file = FileUtils.createTemporaryFile(context, extension)

        var success = false
        val outputStream = ByteArrayOutputStream()
        val fos = FileOutputStream(file)
        try {
            bitmap.compress(format, quality, outputStream)
            fos.write(outputStream.toByteArray())
            fos.flush()
            success = true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            outputStream.close()
            fos.close()
        }

        if (!success) {
            file.delete()
            return null
        }
        return file
    }
}