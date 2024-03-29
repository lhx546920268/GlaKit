package com.lhx.glakit.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min


/**
 * 尺寸大小工具类
 */
object SizeUtils {

    /**
     * 将dip转换为px
     * @param dipValue 要转换的dip值
     * @waring 不要这样获取值 pxFormDip(1) + pxFormDip(2), 否则某些手机计算出的值会是错的
     * @return px值
     */
    fun pxFormDip(dipValue: Float, context: Context): Int {
        return if (dipValue == 0f) 0 else ceil(TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dipValue, context.resources.displayMetrics
        )).toInt()
    }

    /**
     * 获取浮点型的，直接获取整型有时候会有偏差
     */
    fun pxFormDipF(dipValue: Float, context: Context): Float {
        return if (dipValue == 0f) 0f else TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, dipValue, context.resources.displayMetrics
        )
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
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
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
     * 获取底部导航栏高度
     */
    @SuppressLint("InternalInsetResource", "DiscouragedApi")
    fun getNavigationBarHeight(context: Context): Int {
        val resId = context.resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resId > 0) {
            context.resources.getDimensionPixelSize(resId)
        } else 0
    }

    /**
     * 获取显示区域高度
     */
    fun getDisplayHeight(context: Context): Int {
        if (context is Activity) {
            val view = context.findViewById<View>(android.R.id.content)
            if (view != null) {
                var height = view.measuredHeight
                if (!AppUtils.isStatusBarImmersive(context)) height += getStatusBarHeight(context)
                return height
            }
        }
        return getWindowHeight(context)
    }

    /**
     * 获取屏幕真实高度
     */
    @Suppress("deprecation")
    fun getWindowRealHeight(context: Context): Int {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            windowManager.currentWindowMetrics.bounds.height()
        } else {
            val display = windowManager.defaultDisplay
            val point = Point()
            display.getRealSize(point)
            point.y
        }
    }

    /**
     * 是否是全面屏
     */
    @Suppress("deprecation")
    fun isFullScreen(context: Context): Boolean {
        // 低于 API 21的，都不会是全面屏。。。
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return false
        }

        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val width: Float
        val height: Float
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val bounds = windowManager.currentWindowMetrics.bounds
            width = min(bounds.width(), bounds.height()).toFloat()
            height = max(bounds.width(), bounds.height()).toFloat()
        } else {
            val display = windowManager.defaultDisplay
            val point = Point()
            display.getRealSize(point)

            width = min(point.x, point.y).toFloat()
            height = max(point.x, point.y).toFloat()
        }

        return width > 0 && height / width >= 1.97f
    }
}