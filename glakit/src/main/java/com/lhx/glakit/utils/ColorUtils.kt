package com.lhx.glakit.utils

import android.graphics.Color.parseColor
import androidx.annotation.ColorInt

//颜色工具类
object ColorUtils {

    /**
     * 获取白色的百分比颜色 可用于获取某种程度的灰色 或黑色
     * @param percent 白色百分比 0 ~ 1.0
     * @param alpha 透明度 0 ~ 1.0
     * @return 颜色
     */
    fun whitePercentColor(percent: Float, alpha: Float): Int {

        val v = (0xff * percent).toInt()
        val a = (0xff * alpha).toInt()

        return a shl 24 or (v shl 16) or (v shl 8) or v
    }

    /**
     * 生成带透明度的颜色
     * @param color 颜色
     * @param alpha 透明度 0-1.0
     * @return 颜色值
     */
    fun colorWithAlpha(@ColorInt color: Int, alpha: Float): Int {

        val alphaHex = (alpha * 255).toInt()
        return color and 0x00ffffff or (alphaHex shl 24)
    }

    /**
     * 生成带透明度的颜色
     * @param colorString 颜色16进制 不带透明度
     * @param alpha 透明度 0-1.0
     * @return 颜色值
     */
    fun colorWithAlpha(colorString: String, alpha: Float): Int {
        if (colorString.length == 7) {
            return parseColor("#" + hexFromFloat(alpha) + colorString.replaceFirst("#".toRegex(), ""))
        }

        return parseColor(colorString)
    }

    /**
     * 把颜色转成16进制字符串
     */
    fun colorToHex(color: Int): String {
        return String.format("#%06X", (0xFFFFFF and color))
    }

    //小数转16进制字符串
    private fun hexFromFloat(value: Float): String {
        var tValue = value
        if (value < 0) {
            tValue = 0f
        } else if (value > 1.0f) {
            tValue = 1.0f
        }

        val a = (0xff * tValue).toInt()
        return hexFromInt(a / 16) + hexFromInt(a % 16)
    }

    private fun hexFromInt(value: Int): String {
        when (value) {
            10 -> return "A"
            11 -> return "B"
            12 -> return "C"
            13 -> return "D"
            14 -> return "E"
            15 -> return "F"
        }
        return value.toString()
    }
}