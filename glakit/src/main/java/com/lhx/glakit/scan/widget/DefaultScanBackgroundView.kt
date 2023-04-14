package com.lhx.glakit.scan.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.lhx.glakit.R
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.extension.getColorCompat
import com.lhx.glakit.extension.gone
import com.lhx.glakit.extension.visible
import com.lhx.glakit.utils.ColorUtils
import com.lhx.glakit.utils.SizeUtils
import kotlin.math.min

/**
 * 二维码扫码背景
 */
open class DefaultScanBackgroundView: FrameLayout {

    //扫描框
    private var scanContainer: FrameLayout? = null

    //画笔
    private val paint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint
    }

    //绘制路径
    private val path1 = Path()
    private val path2 = Path()

    //扫描框
    private val rect by lazy {
        val parent = parent as View
        val size = SizeUtils.pxFormDip(240f, context)
        val left = (parent.measuredWidth - size) / 2
        val top = min((parent.measuredHeight - size) / 2, SizeUtils.pxFormDip(150f, context))
        Rect(left, top, left + size, top + size)
    }

    //扫描网
    private val animationView: View by lazy {
        val view = View(context)
        val height = SizeUtils.pxFormDip(3f, context)
        val drawable = CornerBorderDrawable()
        drawable.apply {
            backgroundColor = context.getColorCompat(R.color.theme_color)
            setCornerRadius(height / 2)
        }
        drawable.attachView(view)
        val margin = SizeUtils.pxFormDip(10f, context)
        val params = LayoutParams(rect.width() - margin * 2, height)
        params.topMargin = rect.top + margin
        params.leftMargin = rect.left + margin
        addView(view, params)
        view
    }

    //扫描动画
    private val scanAnimation: TranslateAnimation by lazy {
        val toYDelta = rect.height().toFloat() - SizeUtils.pxFormDip(20f, context)
        val animation = TranslateAnimation(0f, 0f, 0f, toYDelta)
        animation.apply {
            duration = 1800
            repeatCount = Animation.INFINITE
            repeatMode = Animation.RESTART
        }
        animation
    }

    //获取扫描框位置
    val scanRect: Rect
        get() = rect

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        setWillNotDraw(false)
    }

    final override fun setWillNotDraw(willNotDraw: Boolean) {
        super.setWillNotDraw(willNotDraw)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val saveCount = canvas.save()
        path1.reset()
        path2.reset()
        paint.color = ColorUtils.colorWithAlpha(Color.BLACK, 0.3f)
        paint.style = Paint.Style.FILL
        val width = width.toFloat()
        val height = height.toFloat()

        val boxLeft = scanRect.left.toFloat()
        val boxTop = scanRect.top.toFloat()
        val boxRight = scanRect.right.toFloat()
        val boxBottom = scanRect.bottom.toFloat()

        //绘制中间镂空半透明背景
        path1.addRect(0f, 0f, width, height, Path.Direction.CCW)
        path2.addRect(boxLeft, boxTop, boxRight, boxBottom, Path.Direction.CCW)
        path1.op(path2, Path.Op.DIFFERENCE)

        canvas.drawPath(path1, paint)

        //绘制边框
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = SizeUtils.pxFormDip(1.0f, context).toFloat()
        canvas.drawPath(path2, paint)

        path1.reset()
        //绘制角
        paint.color = ContextCompat.getColor(context, R.color.theme_color)
        val lineWidth = SizeUtils.pxFormDip(2.5f, context).toFloat()
        paint.strokeWidth = lineWidth
        val size = SizeUtils.pxFormDip(20f, context)

        //左上
        path1.moveTo(boxLeft + lineWidth, boxTop + size + lineWidth)
        path1.lineTo(boxLeft + lineWidth, boxTop + lineWidth)
        path1.lineTo(boxLeft + lineWidth + size, boxTop + lineWidth)

        //右上
        path1.moveTo(boxRight - lineWidth - size, boxTop + lineWidth)
        path1.lineTo(boxRight - lineWidth, boxTop + lineWidth)
        path1.lineTo(boxRight - lineWidth, boxTop + lineWidth + size)

        //右下
        path1.moveTo(boxRight - lineWidth, boxBottom - size - lineWidth)
        path1.lineTo(boxRight - lineWidth, boxBottom - lineWidth)
        path1.lineTo(boxRight - lineWidth - size, boxBottom - lineWidth)

        //左下
        path1.moveTo(boxLeft + lineWidth + size, boxBottom - lineWidth)
        path1.lineTo(boxLeft + lineWidth, boxBottom - lineWidth)
        path1.lineTo(boxLeft + lineWidth, boxBottom - lineWidth - size)
        canvas.drawPath(path1, paint)
        canvas.restoreToCount(saveCount)
    }

    //开始扫描动画
    open fun startScanAnimate() {
        animationView.visible()
        animationView.startAnimation(scanAnimation)
    }

    //停止扫描动画
    open fun stopScanAnimate() {
        animationView.clearAnimation()
        animationView.gone()
    }
}