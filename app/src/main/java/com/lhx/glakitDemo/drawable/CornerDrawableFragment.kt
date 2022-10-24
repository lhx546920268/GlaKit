package com.lhx.glakitDemo.drawable

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.DrawableWrapper
import android.os.Bundle
import android.text.Html
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.base.widget.VoidCallback
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.drawable.LoadingDrawable
import com.lhx.glakit.properties.ObservableProperty
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.CornerDrawableFragmentBinding
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KProperty

class SliderPoint: AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var isRight = false
    var callback: VoidCallback? = null

    var touchX = 0f
    var totalOffset = 0f
        set(value) {
            if (value != field) {
                field = value
                translationX = totalOffset
                callback?.also {
                    it()
                }
            }
        }
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
//        println("x = ${event.x}, ${event.action}")
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                touchX = event.x
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val viewGroup = parent as ViewGroup
                var offset: Float
                if (isRight) {
                    val min = -(viewGroup.width - width)
                    offset = event.x - touchX
                    if (offset + totalOffset < min) offset = min - totalOffset
                    if (offset + totalOffset > 0) offset = -totalOffset
                } else {
                    val max = viewGroup.width - width
                    offset = floor(event.x - touchX)
                    if (offset + totalOffset < 0) offset = -totalOffset
                    if (offset + totalOffset > max) offset = max - totalOffset
                }
                totalOffset += offset
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}

class SliderProgressBar: View, ObservableProperty.Callback {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //画笔
    private val paint = Paint()

    //路径
    private val path = Path()

    private val rectF = RectF()

    //进度
    var min by ObservableProperty(0f, this)
    var max by ObservableProperty(1.0f, this)

    override fun onPropertyValueChange(oldValue: Any?, newValue: Any?, property: KProperty<*>) {
        postInvalidate()
    }

    init {
        setWillNotDraw(false)
        paint.isAntiAlias = true
        paint.style = Paint.Style.FILL
    }

    //进度条颜色
    private val trackColor = Color.parseColor("#D8D8D8")
    private val progressColor = ContextCompat.getColor(context, R.color.red)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val saveCount: Int = canvas.save()
        path.reset()
        paint.color = trackColor

        val w = width.toFloat()
        val h = height.toFloat()

        rectF.left = 0f
        rectF.right = w
        rectF.top = 0f
        rectF.bottom = h

        path.addRoundRect(rectF, h / 2, h / 2, Path.Direction.CCW)
        canvas.drawPath(path, paint)

        path.reset()
        paint.color = progressColor

        rectF.left = min * w
        rectF.right = w * max

        path.addRoundRect(rectF, h / 2, h / 2, Path.Direction.CCW)
        canvas.drawPath(path, paint)

        canvas.restoreToCount(saveCount)
    }
}

//滑块
class Slider: FrameLayout {

    //左右2个点
    val leftImageView = SliderPoint(context)
    val rightImageView = SliderPoint(context)

    //进度
    val progressBar = SliderProgressBar(context)

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val drawable = ContextCompat.getDrawable(context, R.drawable.slide_point)!!
        var params = LayoutParams(LayoutParams.MATCH_PARENT, SizeUtils.pxFormDip(4f, context))
        params.gravity = Gravity.CENTER_VERTICAL
        params.leftMargin = drawable.minimumWidth / 2
        params.rightMargin = drawable.minimumWidth / 2
        addView(progressBar, params)

        leftImageView.setImageDrawable(drawable)
        params = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        leftImageView.callback = {
            progressBar.min = min
        }
        addView(leftImageView, params)

        rightImageView.setImageDrawable(drawable)
        rightImageView.isRight = true
        params = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.gravity = Gravity.END or Gravity.CENTER_VERTICAL
        rightImageView.callback = {
            progressBar.max = max
        }
        addView(rightImageView, params)
    }

    //用于测量后
    private var targetMin = 0f
    private var targetMax = 0f

    //最大最小值 0 ~ 1.0
    var min: Float
        set(aValue) {
            var value = aValue
            if (value < 0f) value = 0f
            if (value > 1.0f) value = 1.0f
            if (isLaidOut) {
                leftImageView.totalOffset = (width - leftImageView.width) * value
            } else {
                targetMin = value
            }
        }
        get() {
            val value = width - leftImageView.width
            return min(leftImageView.totalOffset / value, 1.0f - abs(rightImageView.totalOffset) / value)
        }

    var max: Float
        set(aValue) {
            var value = aValue
            if (value < 0f) value = 0f
            if (value > 1.0f) value = 1.0f

            if (isLaidOut) {
                rightImageView.totalOffset = -(width - leftImageView.width) * (1.0f - value)
            } else {
                targetMax = 1.0f - value
            }
        }
        get() {
            val value = width - leftImageView.width
            return max(leftImageView.totalOffset / value, 1.0f - abs(rightImageView.totalOffset) / value)
        }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (targetMin != 0f) {
            leftImageView.totalOffset = (width - leftImageView.width) * targetMin
            targetMin = 0f
        }

        if (targetMax != 0f) {
            rightImageView.totalOffset = -(width - leftImageView.width) * targetMax
            targetMax = 0f
        }
    }
}

class MeasureParent: ViewGroup {
    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            child.layout(paddingLeft, paddingTop, paddingLeft + child.measuredWidth, paddingTop + child.measuredHeight)
        }
    }
}

class MeasureChild: View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)

        println("child width size = $widthSize, mode $widthMode")

        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)

        println("height size = $heightSize, mode $heightMode")

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}

@Suppress("deprecation")
class CornerDrawableFragment: BaseFragment() {

    val viewBinding by lazy { CornerDrawableFragmentBinding.bind(getContainerContentView()!!) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        container.setContentView(R.layout.corner_drawable_fragment)
        setBarTitle("Drawable")

        viewBinding.apply {
            val height = cornerText.paint.fontMetrics.descent - cornerText.paint.fontMetrics.ascent
            val html = "<img src='http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg'/> 圆角圆角圆角圆角圆角圆角圆角圆角"
            cornerText.text = Html.fromHtml(html,
                {
                    val drawable = CornerBorderDrawable()
                    drawable.borderColor = Color.RED
                    drawable.borderWidth = pxFromDip(10.0f)
                    drawable.shouldAbsoluteCircle = true
                    drawable.backgroundColor = Color.BLUE
                    drawable.intrinsicWidth = height.toInt()
                    drawable.intrinsicHeight = height.toInt()
                    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

                    drawable
                }, null)

            val drawable = CornerBorderDrawable()
            drawable.borderColor = Color.RED
            drawable.borderWidth = pxFromDip(10.0f)
            drawable.shouldAbsoluteCircle = true
            drawable.backgroundColor = Color.BLUE

            drawable.attachView(cornerText1, true)

            val loadingDrawable = LoadingDrawable()
            loadingDrawable.intrinsicWidth = pxFromDip(37.0f)
            loadingDrawable.intrinsicHeight = pxFromDip(37.0f)

            imgSquare.setImageDrawable(loadingDrawable)
            loadingDrawable.start()

            drawable.borderColor = 0
            drawable.borderWidth = 0
            drawable.shouldAbsoluteCircle = false
            drawable.backgroundColor = Color.RED
            drawable.leftTopCornerRadius = pxFromDip(10f)
            drawable.rightBottomCornerRadius = pxFromDip(10f)
            drawable.attachView(imgRect, true)

            slider.min = 0.2f
            slider.max = 0.6f
        }
    }
}

