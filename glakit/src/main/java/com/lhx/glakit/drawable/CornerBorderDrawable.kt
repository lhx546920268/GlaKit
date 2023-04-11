package com.lhx.glakit.drawable

import android.graphics.*
import com.lhx.glakit.properties.ObservableProperty
import kotlin.math.min
import kotlin.reflect.KProperty


/**
 * 圆角边框drawable
 */
@Suppress("unused_parameter")
open class CornerBorderDrawable : BaseDrawable(), ObservableProperty.Callback{

    //左上角圆角 px
    var leftTopCornerRadius by ObservableProperty(0, this)

    //右上角圆角 px
    var rightTopCornerRadius by ObservableProperty(0, this)

    //左下角圆角 px
    var leftBottomCornerRadius by ObservableProperty(0, this)

    //右下角圆角 px
    var rightBottomCornerRadius by ObservableProperty(0, this)

    //是否全圆
    var shouldAbsoluteCircle by ObservableProperty(false, this)

    //边框线条厚度 px
    var borderWidth by ObservableProperty(0, this)

    //边框线条颜色
    var borderColor by ObservableProperty(Color.TRANSPARENT, this)

    //背景填充颜色
    var backgroundColor by ObservableProperty(Color.TRANSPARENT, this)

    override fun onPropertyValueChange(oldValue: Any?, newValue: Any?, property: KProperty<*>) {
        invalidateSelf()
    }

    override fun draw(canvas: Canvas) {

        drawBorder(canvas)
        drawBackground(canvas)
    }

    //获取绘制路径
    private val floatArray = FloatArray(8)
    private val path = Path()
    protected fun getPath(bounds: RectF): Path {

        path.reset()
        if (shouldAbsoluteCircle) {
            //全圆
            val radius = min(bounds.width(), bounds.height()) / 2.0f
            path.addRoundRect(
                bounds, radius, radius, Path.Direction
                    .CW
            )
        } else {
            //从左到右顺时针
            floatArray[0] = leftTopCornerRadius.toFloat()
            floatArray[1] = floatArray[0]
            floatArray[2] = rightTopCornerRadius.toFloat()
            floatArray[3] = floatArray[2]
            floatArray[4] = rightBottomCornerRadius.toFloat()
            floatArray[5] = floatArray[4]
            floatArray[6] = leftBottomCornerRadius.toFloat()
            floatArray[7] = floatArray[6]

            path.addRoundRect(bounds, floatArray, Path.Direction.CW)
        }

        return path
    }

    //设置圆角半径
    fun setCornerRadius(cornerRadius: Int){
        setCornerRadius(cornerRadius, cornerRadius, cornerRadius, cornerRadius)
    }

    //设置圆角半径
    fun setCornerRadius(leftTopCornerRadius: Int, leftBottomCornerRadius: Int, rightTopCornerRadius: Int, rightBottomCornerRadius: Int){

        this.leftTopCornerRadius = leftTopCornerRadius
        this.leftBottomCornerRadius = leftBottomCornerRadius
        this.rightTopCornerRadius = rightTopCornerRadius
        this.rightBottomCornerRadius = rightBottomCornerRadius
        invalidateSelf()
    }

    //复制一份
    override fun copy(): CornerBorderDrawable {
        val drawable = CornerBorderDrawable()
        copyTo(drawable)
        return drawable
    }

    override fun copyTo(drawable: BaseDrawable) {
        super.copyTo(drawable)
        if (drawable is CornerBorderDrawable) {
            drawable.shouldAbsoluteCircle = shouldAbsoluteCircle
            drawable.backgroundColor = backgroundColor
            drawable.borderColor = borderColor
            drawable.borderWidth = borderWidth
            drawable.leftTopCornerRadius = leftTopCornerRadius
            drawable.leftBottomCornerRadius = leftBottomCornerRadius
            drawable.rightTopCornerRadius = rightTopCornerRadius
            drawable.rightBottomCornerRadius = rightBottomCornerRadius
        }
    }

    //绘制边框
    private fun drawBorder(canvas: Canvas) {

        val existBorder = borderWidth > 0 && Color.alpha(borderColor) != 0

        //绘制边框
        if (existBorder) {

            val bounds = RectF(rectF)
            bounds.inset(borderWidth / 2.0f, borderWidth / 2.0f)

            paint.style = Paint.Style.STROKE
            paint.strokeWidth = borderWidth.toFloat()
            paint.color = borderColor
            canvas.drawPath(getPath(bounds), paint)
        }
    }

    //绘制背景
    private fun drawBackground(canvas: Canvas) {

        //绘制背景
        if (Color.alpha(backgroundColor) != 0) {

            val bounds = RectF(rectF)

            val existBorder = borderWidth > 0 && Color.alpha(borderColor) != 0
            val margin = if (existBorder) borderWidth else 0
            bounds.inset(margin.toFloat(), margin.toFloat())

            paint.color = backgroundColor
            paint.style = Paint.Style.FILL
            canvas.drawPath(getPath(bounds), paint)
        }
    }
}