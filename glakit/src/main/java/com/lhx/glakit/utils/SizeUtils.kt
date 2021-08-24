package com.lhx.glakit.utils

import android.content.Context
import android.content.res.Configuration
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.Display
import android.view.WindowManager
import kotlin.math.max
import kotlin.math.min


/**
 * 尺寸大小工具类
 */
object SizeUtils {

    /**
     * 将dip转换为px
     * @param dipValue 要转换的dip值
     * @return px值
     */
    fun pxFormDip(dipValue: Float, context: Context): Int {
        return if (dipValue == 0f) 0 else TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dipValue, context.resources.displayMetrics
        ).toInt()
    }

    /**
     * 将px转换为dip
     * @param pxValue 要转换的px值
     * @return dp值
     */
    fun dipFromPx(pxValue: Int, context: Context): Float {
        if (pxValue == 0)
            return 0f
        val scale = context.resources.displayMetrics.density
        return pxValue / scale + 0.5f
    }

    /**
     * sp 转 px
     * @param spValue 要转换的sp值
     * @return sp值
     */
    fun pxFromSp(spValue: Float, context: Context): Int {
        return if (spValue == 0f) 0 else TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, spValue, context.resources.displayMetrics
        ).toInt()

    }

    /**
     * 将px值转换为sp值
     * @param pxValue 要转换的px值
     * @return sp值
     */
    fun spFromPx(pxValue: Int, context: Context): Float {
        if (pxValue == 0)
            return 0f
        val fontScale = context.resources.displayMetrics.scaledDensity
        return pxValue / fontScale + 0.5f
    }


    /**
     * 获取屏幕宽度
     * @return 宽度
     */
    fun getWindowWidth(context: Context): Int {
        return context.resources.displayMetrics.widthPixels
    }

    /**
     * 获取屏幕高度
     * @return 高度
     */
    fun getWindowHeight(context: Context): Int {
        return context.resources.displayMetrics.heightPixels
    }

    /**
     * 获取状态栏高度
     * @param context Context
     * @return Int
     */
    fun getStatusBarHeight(context: Context): Int {
        var height = 0
        val resId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resId > 0) {
            height = context.resources.getDimensionPixelSize(resId)
        }

        if (height == 0) {
            height = pxFormDip(20f, context)
        }

        return height
    }

    /**
     * 获取屏幕高度
     */
    fun getScreenHeight(context: Context): Int {
        return if (!isFullScreen(context)) getWindowHeight(context) else getWindowRealHeight(context)
    }

    private const val PORTRAIT = 0
    private const val LANDSCAPE = 1
    private val realSizes: Array<Point?> = arrayOfNulls(2)

    /**
     * 获取屏幕真实高度
     */
    fun getWindowRealHeight(context: Context): Int {
        var orientation = context.resources.configuration.orientation
        orientation = if (orientation == Configuration.ORIENTATION_PORTRAIT) PORTRAIT else LANDSCAPE
        if (realSizes[orientation] == null) {
            val display = displayCompat(context)
            val point = Point()
            if(display != null) {
                display.getRealSize(point)
            }
            realSizes[orientation] = point
        }
        return realSizes[orientation]!!.y
    }

    /**
     * 是否是全面屏
     */
    fun isFullScreen(context: Context): Boolean {
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false
        }

        val display = displayCompat(context)
        if (display != null) {
            val point = Point()
            display.getRealSize(point)

            val width = min(point.x, point.y)
            val height = max(point.x, point.y)

            return height.toFloat() / width >= 1.97f
        }
        return false
    }

    @Suppress("deprecation")
    fun displayCompat(context: Context): Display? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display
        } else {
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay
        }
    }
}