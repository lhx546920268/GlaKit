package com.lhx.glakit.utils

import android.util.Size
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
    fun fitSize(width: Int, height: Int, maxWidth: Int, maxHeight: Int): Size {

        if(width == height){
            val value = width.coerceAtMost(if (maxWidth > maxHeight) maxHeight else maxWidth)
            return Size(value, value)
        } else {

            var w = width.toDouble()
            var h = height.toDouble()
            val heightScale = if (maxHeight > 0) height / maxHeight else 0
            val widthScale = if (maxWidth > 0) width / maxWidth else 0

            if(heightScale != 0 && widthScale != 0) {
                if(height >= maxHeight && width >= maxWidth){
                    if(heightScale > widthScale){
                        h = floor(height.toDouble() / heightScale)
                        w = floor(width.toDouble() / heightScale)
                    } else {
                        h = floor(height.toDouble() / widthScale)
                        w = floor(width.toDouble() / widthScale)
                    }
                } else {
                    if(height >= maxHeight && width <= maxWidth){
                        h = floor(height.toDouble() / heightScale)
                        w = floor(width.toDouble() / heightScale)
                    } else if (height <= maxHeight && width >= maxWidth){
                        h = floor(height.toDouble() / widthScale)
                        w = floor(width.toDouble() / widthScale)
                    }
                }
            }else if(heightScale == 0){
                h = floor(height.toDouble() / widthScale)
                w = floor(width.toDouble() / widthScale)
            }else {
                h = floor(height.toDouble() / heightScale)
                w = floor(width.toDouble() / heightScale)
            }
            return Size(w.toInt(), h.toInt())
        }
    }
}