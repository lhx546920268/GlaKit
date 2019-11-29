package com.lhx.glakit.drawable

import androidx.annotation.ColorInt
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.animation.LinearInterpolator
import android.animation.ValueAnimator
import android.graphics.*
import android.graphics.drawable.Animatable
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import com.lhx.glakit.utils.ColorUtils
import com.lhx.glakit.utils.MathUtils
import kotlin.math.min


///加载菊花
class LoadingDrawable : BaseDrawable(), Animatable {

    //是否在执行
    private var running: Boolean = false

    //动画对象
    private val valueAnimator: ValueAnimator

    //花瓣数量
    @IntRange(from = 1)
    var petalsCount = 13
    set(value) {
        if(value != field){
            field = value
            invalidateSelf()
        }
    }

    //花瓣大小 px
    var petalsStrokeWidth = 0
    set(value) {
        if(value != field){
            field = value
            invalidateSelf()
        }
    }

    //颜色
    @ColorInt
    var color = Color.WHITE
    set(value) {
        if(value != field){
            field = value
            invalidateSelf()
        }
    }

    //渐变数量
    var fadeCount = 5

    //当前开始位置
    private var start: Int = 0

    //花瓣路径
    private val petalsPath: Path

    //内圆比例
    @FloatRange(from = 0.0, to = 1.0)
    var innerCircleRatio = 3.0 / 5.0
    set(value) {
        if (value != field) {
            field = value
            invalidateSelf()
        }
    }

    //监听动画改变
    private var mAnimatorUpdateListener: ValueAnimator.AnimatorUpdateListener =
        ValueAnimator.AnimatorUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            start = (progress * petalsCount).toInt()
            invalidateSelf()
        }

    init {

        paint.pathEffect = CornerPathEffect(petalsStrokeWidth.toFloat() / 2)
        paint.style = Paint.Style.STROKE

        valueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        valueAnimator.repeatCount = ValueAnimator.INFINITE
        valueAnimator.repeatMode = ValueAnimator.RESTART
        valueAnimator.addUpdateListener(mAnimatorUpdateListener)
        valueAnimator.duration = 1000
        valueAnimator.interpolator = LinearInterpolator()
        valueAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationRepeat(animation: Animator) {
                start = 0
            }

            override fun onAnimationStart(animation: Animator) {
                start = 0
            }
        })

        petalsPath = Path()
    }

    override fun start() {
        if (isRunning)
            return

        running= true
        valueAnimator.start()
        invalidateSelf()
    }

    override fun stop() {
        if (!isRunning)
            return

        running = false
        valueAnimator.end()
        invalidateSelf()
    }

    override fun isRunning(): Boolean {
        return running
    }

    override fun draw(canvas: Canvas) {
        val saveCount = canvas.save()

        var strokeWidth = petalsStrokeWidth.toFloat()


        //线占一份，间隔占2份
        if (strokeWidth == 0f) {
            val radius = min(rectF.width(), rectF.height())
            val length = (Math.PI * radius.toDouble() * 2.0).toFloat()
            strokeWidth = length / ((petalsCount - 1) * 6 + petalsCount)
        }

        val rectF = RectF(rectF)
        rectF.inset(strokeWidth, strokeWidth)

        paint.pathEffect = CornerPathEffect(strokeWidth / 2)
        paint.strokeWidth = strokeWidth

        //绘制菊花
        petalsPath.reset()
        val center = Point(rectF.centerX().toInt(), rectF.centerY().toInt())
        val radius1 = min(rectF.width(), rectF.height()) / 2
        val radius2 = radius1 * innerCircleRatio

        val arc = 360.0f / petalsCount

        val start = this.start
        var end = start + fadeCount
        if (end > petalsCount) {
            end -= petalsCount
        }


        for (i in 0 until petalsCount) {
            val point1 = MathUtils.pointInCircle(center, radius1.toInt(), arc * i)
            val point2 = MathUtils.pointInCircle(center, radius2.toInt(), arc * i)

            petalsPath.moveTo(point1.x.toFloat(), point1.y.toFloat())
            petalsPath.lineTo(point2.x.toFloat(), point2.y.toFloat())

            var normal = i in end until start
            if (start < end)
                normal = i < start || i >= end



            if (normal) {
                paint.color = ColorUtils.colorWithAlpha(color, 0.5f)
            } else {
                var alpha = 1.0f - i * 0.1f
                if (i >= start)
                    alpha = 1.0f - fadeCount * 0.1f + 0.1f + (i - start) * 0.1f

                paint.color = ColorUtils.colorWithAlpha(color, alpha)
            }

            canvas.drawPath(petalsPath, paint)

            petalsPath.reset()
        }

        canvas.restoreToCount(saveCount)
    }

    override fun copy(): BaseDrawable {
        val drawable = LoadingDrawable()
        drawable.color = color
        drawable.innerCircleRatio = innerCircleRatio
        drawable.petalsCount = petalsCount
        drawable.petalsStrokeWidth = petalsStrokeWidth

        return drawable
    }
}