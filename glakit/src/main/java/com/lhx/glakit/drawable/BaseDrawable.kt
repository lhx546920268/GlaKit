package com.lhx.glakit.drawable

import android.graphics.*
import android.graphics.drawable.Drawable
import android.view.View

/**
 * 基础drawable
 */
@Suppress("unused_parameter")
abstract class BaseDrawable : Drawable() {

    companion object {
        const val NO_SIZE = -1
    }

    //画笔
    protected val paint = Paint()

    //范围
    protected val rectF = RectF()

    //内在宽度
    private var intrinsicWidth = NO_SIZE

    //内在盖度
    private var intrinsicHeight = NO_SIZE

    init {
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.isAntiAlias = true //设置抗锯齿
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java",
        ReplaceWith("PixelFormat.TRANSLUCENT", "android.graphics.PixelFormat")
    )
    override fun getOpacity(): Int {
        return PixelFormat.TRANSLUCENT
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        //必须的，否则会出现不可预料的bug，如键盘弹出后消失，直接getBounds() 返回越来越小的rect
        rectF.left = bounds.left.toFloat()
        rectF.top = bounds.top.toFloat()
        rectF.right = bounds.right.toFloat()
        rectF.bottom = bounds.bottom.toFloat()
    }

    fun setIntrinsicWidth(width: Int){
        intrinsicWidth = width
    }

    fun setIntrinsicHeight(height: Int){
        intrinsicHeight = height
    }

    override fun getIntrinsicWidth(): Int {
        return intrinsicWidth
    }

    override fun getIntrinsicHeight(): Int {
        return intrinsicHeight
    }

    //如果drawable用于多个view, 使用这个方法 关联view 将copy一份
    fun attachView(view: View, shouldCopy: Boolean = false){
        view.background = if(shouldCopy){
            copy()
        }else{
            this
        }
    }

    //复制一份
    abstract fun copy() : BaseDrawable
    open fun copyTo(drawable: BaseDrawable) {

    }
}