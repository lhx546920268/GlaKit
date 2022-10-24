package com.lhx.glakit.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import com.lhx.glakit.properties.ObservableProperty
import kotlin.math.min
import kotlin.reflect.KProperty

class RoundImageView: AppCompatImageView, ObservableProperty.Callback {

    private val bitmapConfig = Bitmap.Config.ARGB_8888
    private val colorDrawableDimension = 2

    private val mDrawableRect = RectF()
    private val mShaderMatrix = Matrix()
    private val mBitmapPaint = Paint()

    private var mImageAlpha = 255
    private var mBitmap: Bitmap? = null
    private var mDrawableRadius = 0f
    private var mColorFilter: ColorFilter? = null
    private var mInitialized = false
    private var mRebuildShader = false

    //左上角圆角 px
    private var leftTopCornerRadius by ObservableProperty(0f, this)

    //右上角圆角 px
    private var rightTopCornerRadius by ObservableProperty(0f, this)

    //左下角圆角 px
    private var leftBottomCornerRadius by ObservableProperty(0f, this)

    //右下角圆角 px
    private var rightBottomCornerRadius by ObservableProperty(0f, this)

    //是否全圆
    private var shouldAbsoluteCircle by ObservableProperty(false, this)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        init()
    }


    private fun init() {
        mInitialized = true
        mBitmapPaint.isAntiAlias = true
        mBitmapPaint.isDither = true
        mBitmapPaint.isFilterBitmap = true
        mBitmapPaint.alpha = mImageAlpha
        mBitmapPaint.colorFilter = mColorFilter
        super.setScaleType(ScaleType.CENTER_CROP)
    }

    override fun setScaleType(scaleType: ScaleType?) {
        super.setScaleType(scaleType)
        require(scaleType == ScaleType.CENTER_CROP) { String.format("ScaleType %s not supported.", scaleType) }
    }

    override fun setAdjustViewBounds(adjustViewBounds: Boolean) {
        require(!adjustViewBounds) { "adjustViewBounds not supported." }
    }

    @SuppressLint("CanvasSize")
    override fun onDraw(canvas: Canvas) {
        val restoreCount = canvas.save()
        if (mBitmap != null) {
            if (mRebuildShader) {
                mRebuildShader = false
                val bitmapShader = BitmapShader(mBitmap!!, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
                bitmapShader.setLocalMatrix(mShaderMatrix)
                mBitmapPaint.shader = bitmapShader
            }

            canvas.drawPath(getPath(), mBitmapPaint)
        }
        canvas.restoreToCount(restoreCount)
    }

    override fun invalidateDrawable(dr: Drawable) {
        initializeBitmap()
        invalidate()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        updateDimensions()
        invalidate()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setPaddingRelative(start: Int, top: Int, end: Int, bottom: Int) {
        super.setPaddingRelative(start, top, end, bottom)
        updateDimensions()
        invalidate()
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        initializeBitmap()
        invalidate()
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        initializeBitmap()
        invalidate()
    }

    override fun setImageResource(@DrawableRes resId: Int) {
        super.setImageResource(resId)
        initializeBitmap()
        invalidate()
    }

    override fun setImageURI(uri: Uri?) {
        super.setImageURI(uri)
        initializeBitmap()
        invalidate()
    }

    override fun setImageAlpha(alpha: Int) {
        var alpha = alpha
        alpha = alpha and 0xFF
        if (alpha == mImageAlpha) {
            return
        }
        mImageAlpha = alpha

        // This might be called during ImageView construction before
        // member initialization has finished on API level >= 16.
        if (mInitialized) {
            mBitmapPaint.alpha = alpha
            invalidate()
        }
    }

    override fun getImageAlpha(): Int {
        return mImageAlpha
    }

    override fun setColorFilter(cf: ColorFilter) {
        if (cf === mColorFilter) {
            return
        }
        mColorFilter = cf

        // This might be called during ImageView construction before
        // member initialization has finished on API level <= 19.
        if (mInitialized) {
            mBitmapPaint.colorFilter = cf
            invalidate()
        }
    }

    override fun getColorFilter(): ColorFilter? {
        return mColorFilter
    }

    private fun getBitmapFromDrawable(drawable: Drawable?): Bitmap? {
        if (drawable == null) {
            return null
        }
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else try {
            val bitmap: Bitmap = if (drawable is ColorDrawable) {
                Bitmap.createBitmap(colorDrawableDimension, colorDrawableDimension, bitmapConfig)
            } else {
                Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, bitmapConfig)
            }
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun initializeBitmap() {
        mBitmap = getBitmapFromDrawable(drawable)
        if (!mInitialized) {
            return
        }
        if (mBitmap != null) {
            updateShaderMatrix()
        } else {
            mBitmapPaint.shader = null
        }
    }

    private fun updateDimensions() {
        mDrawableRect.set(calculateBounds())
        mDrawableRadius = min(mDrawableRect.height() / 2.0f, mDrawableRect.width() / 2.0f)
        updateShaderMatrix()
    }

    private fun calculateBounds(): RectF {
        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val left = paddingLeft.toFloat()
        val top = paddingTop.toFloat()
        return RectF(left, top, left + availableWidth, top + availableHeight)
    }

    private fun updateShaderMatrix() {
        if (mBitmap == null) {
            return
        }
        val scale: Float
        var dx = 0f
        var dy = 0f
        mShaderMatrix.set(null)
        val bitmapHeight = mBitmap!!.height
        val bitmapWidth = mBitmap!!.width
        if (bitmapWidth * mDrawableRect.height() > mDrawableRect.width() * bitmapHeight) {
            scale = mDrawableRect.height() / bitmapHeight.toFloat()
            dx = (mDrawableRect.width() - bitmapWidth * scale) * 0.5f
        } else {
            scale = mDrawableRect.width() / bitmapWidth.toFloat()
            dy = (mDrawableRect.height() - bitmapHeight * scale) * 0.5f
        }
        mShaderMatrix.setScale(scale, scale)
        mShaderMatrix.postTranslate((dx + 0.5f).toInt() + mDrawableRect.left, (dy + 0.5f).toInt() + mDrawableRect.top)
        mRebuildShader = true
    }

    //获取绘制路径
    private val floatArray = FloatArray(8)
    private val path = Path()
    private fun getPath(): Path {
        path.reset()
        val bounds = mDrawableRect
        if (shouldAbsoluteCircle) {
            //全圆
            val radius = min(bounds.width(), bounds.height()) / 2.0f
            path.addRoundRect(
                bounds, radius, radius, Path.Direction
                    .CW
            )
        } else {
            //从左到右顺时针
            floatArray[0] = leftTopCornerRadius
            floatArray[1] = floatArray[0]
            floatArray[2] = rightTopCornerRadius
            floatArray[3] = floatArray[2]
            floatArray[4] = rightBottomCornerRadius
            floatArray[5] = floatArray[4]
            floatArray[6] = leftBottomCornerRadius
            floatArray[7] = floatArray[6]

            path.addRoundRect(bounds, floatArray, Path.Direction.CW)
        }

        return path
    }

    override fun onPropertyValueChange(oldValue: Any?, newValue: Any?, property: KProperty<*>) {
        invalidate()
    }

    /**
     *设置圆角半径
     * */
    fun setCorner(topLeft: Float, topRight: Float, bottomRight: Float, bottomLeft: Float){
        leftTopCornerRadius = topLeft
        rightTopCornerRadius = topRight
        leftBottomCornerRadius = bottomLeft
        rightBottomCornerRadius = bottomRight
    }
}