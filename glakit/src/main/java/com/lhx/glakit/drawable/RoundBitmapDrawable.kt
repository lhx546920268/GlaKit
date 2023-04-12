package com.lhx.glakit.drawable

import android.content.Context
import android.graphics.*
import android.graphics.Matrix.ScaleToFit
import android.widget.ImageView
import kotlin.math.min

class RoundBitmapDrawable: CornerBorderDrawable {

    //位图
    var bitmap: Bitmap? = null
        set(value) {
            if (value != field) {
                field = value
                rebuildShader = true
                invalidateSelf()
            }
        }
    private val bitmapRectF = RectF()

    //位图着色器
    private var bitmapShader: BitmapShader? = null
    private var rebuildShader = false

    //位图画笔
    private val bitmapPaint: Paint = run {
        val paint = Paint()
        paint.shader = bitmapShader
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
        paint
    }
    
    //显示模式
    var scaleType = ImageView.ScaleType.FIT_CENTER
    
    //
    private val shaderMatrix = Matrix()

    /**
     * 通过位图构建
     * @param bitmap 位图
     */
    constructor(bitmap: Bitmap?): super() {
        this.bitmap = bitmap
    }

    /**
     * 通过资源id构建
     * @param res 资源id
     * @param context context
     */
    constructor(res: Int, context: Context): this(BitmapFactory.decodeResource(context.resources, res))

    //根据ScaleType更新ShaderMatrix
    private fun updateShaderMatrix() {
        val bitmap = this.bitmap
        bitmap ?: return

        val bitmapWidth = bitmap.width.toFloat()
        val bitmapHeight = bitmap.height.toFloat()
        val rectF = this.rectF
        var scale = 1.0f
        var left = 0f
        var top = 0f
        var height = rectF.width()
        var width = rectF.height()
        val half = borderWidth / 2f

        shaderMatrix.reset()

        when (scaleType) {
            ImageView.ScaleType.CENTER_INSIDE -> {
                if (bitmapWidth <= rectF.width() && bitmapHeight <= rectF.height()) {
                    height = bitmapHeight
                    width = bitmapHeight
                } else { //bitmap > drawable
                    scale = min(rectF.width() / bitmapHeight,rectF.height() / bitmapHeight)
                    if (rectF.height() < rectF.width()) { //高<宽
                        height = rectF.height()
                        width = bitmapHeight * scale
                    } else if (rectF.height() > rectF.width()) { //宽<高
                        height = bitmapHeight * scale
                        width = rectF.width()
                    } else { //宽=高
                        height = bitmapHeight * scale
                        width = bitmapHeight * scale
                    }
                }
                //X,Y偏移
                left = (rectF.width() - bitmapHeight * scale) * 0.5f + 0.5f
                top = (rectF.height() - bitmapHeight * scale) * 0.5f + 0.5f
                shaderMatrix.setScale(scale, scale)
                shaderMatrix.postTranslate(left, top)
            }
            ImageView.ScaleType.CENTER -> {
                height = min(rectF.height(), bitmapHeight)
                width = min(rectF.width(), bitmapWidth)
                //裁剪或者Margin（如果View大，则 margin Bitmap，如果View小则裁剪Bitmap）
                val diffW = rectF.height() - bitmapHeight
                val diffH = rectF.width() - bitmapWidth
                val halfH = diffH / 2f
                val halfW = diffW / 2f
                top = if (halfH > 0) halfH else 0f
                left = if (halfW > 0) halfW else 0f
                shaderMatrix.postTranslate((left + 0.5f).toInt() + half, (top + 0.5f).toInt() + half)
            }
            ImageView.ScaleType.CENTER_CROP -> {
                var dx = 0f
                var dy = 0f
                if (bitmapHeight * rectF.height() > rectF.width() * bitmapHeight) {
                    scale = rectF.height() / bitmapHeight
                    dx = (rectF.width() - bitmapHeight * scale) * 0.5f
                } else {
                    scale = rectF.width() / bitmapHeight
                    dy = (rectF.height() - bitmapHeight * scale) * 0.5f
                }
                shaderMatrix.setScale(scale, scale)
                shaderMatrix.postTranslate((dx + 0.5f).toInt() + half, (dy + 0.5f).toInt() + half)
            }
            ImageView.ScaleType.FIT_XY -> {
                shaderMatrix.setRectToRect(RectF(0f, 0f, bitmapWidth, bitmapHeight), rectF, ScaleToFit.FILL)
            }
            else -> {
                width = bitmapWidth
                height = bitmapHeight
                val bitmapRect = RectF(0f, 0f, bitmapWidth, bitmapHeight)
                shaderMatrix.setRectToRect(
                    bitmapRect,
                    rectF,
                    scaleTypeToScaleToFit(scaleType)
                )

                val rect = RectF(left, top, left + width, top + height)
                shaderMatrix.mapRect(rect)
                shaderMatrix.setRectToRect(bitmapRect, rect, ScaleToFit.FILL)
            }
        }

        bitmapRectF.set(left, top, left + width, top + height)
        shaderMatrix.setScale(scale, scale)
        rebuildShader = true
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        drawBitmap(canvas)
    }

    //获取位图圆角半径
    private fun drawBitmap(canvas: Canvas) {
        //绘制位图
        if (bitmap != null) {
            if (rebuildShader) {
                bitmapShader = BitmapShader(bitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                rebuildShader = false
            }
            val bounds = RectF(rectF)

            val existBorder = borderWidth > 0 && Color.alpha(borderColor) != 0
            val margin = if (existBorder) borderWidth / 2 else 0
            bounds.inset(margin.toFloat(), margin.toFloat())

            //如果绘制区域不等于位图大小，设置缩放矩阵
            val width = bitmap!!.width.toFloat()
            val height = bitmap!!.height.toFloat()
            if (bounds.width() != width || bounds.height() != height) {
                val matrix = Matrix()
                matrix.setScale(
                    bounds.width() / width,
                    bounds.height() / height
                )
                bitmapShader!!.setLocalMatrix(matrix)
            }

            canvas.drawPath(getPath(bounds), bitmapPaint)
        }
    }

    //以下是父类方法
    override fun getIntrinsicHeight(): Int {
        val height = super.getIntrinsicHeight()
        return if (bitmap != null && height == NO_SIZE) {
            bitmap!!.height
        } else {
            height
        }
    }

    override fun getIntrinsicWidth(): Int {
        val with = super.getIntrinsicWidth()
        return if (bitmap != null && with == NO_SIZE) {
            bitmap!!.width
        } else {
            with
        }
    }

    override fun setFilterBitmap(filter: Boolean) {
        super.setFilterBitmap(filter)
        bitmapPaint.isFilterBitmap = filter
        invalidateSelf()
    }

    override fun copy(): RoundBitmapDrawable {
        return RoundBitmapDrawable(bitmap)
    }

    override fun copyTo(drawable: BaseDrawable) {
        super.copyTo(drawable)
        if (drawable is RoundBitmapDrawable) {
            drawable.bitmap = bitmap
        }
    }

    private fun scaleTypeToScaleToFit(scaleType: ImageView.ScaleType): ScaleToFit {
        /**
         * 根据源码改造  sS2FArray[st.nativeInt - 1]
         */
        return when (scaleType) {
            ImageView.ScaleType.FIT_XY -> ScaleToFit.FILL
            ImageView.ScaleType.FIT_START -> ScaleToFit.START
            ImageView.ScaleType.FIT_END -> ScaleToFit.END
            ImageView.ScaleType.FIT_CENTER -> ScaleToFit.CENTER
            else -> ScaleToFit.CENTER
        }
    }

}