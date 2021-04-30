package com.lhx.glakit.widget

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import com.lhx.glakit.R
import com.lhx.glakit.drawable.CornerBorderDrawable
import kotlin.math.max


/**
 * 角标
 */
class BadgeValueTextView : AppCompatTextView {

    //背景
    private var drawable = CornerBorderDrawable()

    //背景颜色
    @ColorInt
    private var fillColor = Color.RED
    set(value) {
        if(value != field){
            field = value
            drawable.backgroundColor = field
        }
    }

    //边框颜色
    @ColorInt
    private var strokeColor = Color.TRANSPARENT
    set(value) {
        if(value != field){
            field = value
            drawable.borderColor = field
        }
    }

    //边框
    private var strokeWidth = 0
    set(value) {
        if(value != field){
            field = value
            drawable.borderWidth = field
        }
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){

        if (attrs != null) {
            val array = getContext().obtainStyledAttributes(attrs, R.styleable.BadgeValueTextView)

            fillColor = array.getColor(R.styleable.BadgeValueTextView_badge_fill_color, Color.RED)
            strokeColor = array.getColor(R.styleable.BadgeValueTextView_badge_stroke_color, Color.TRANSPARENT)
            strokeWidth = array.getDimensionPixelOffset(R.styleable.BadgeValueTextView_badge_stroke_width, 0)

            array.recycle()
        }
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
    }

    init {
        drawable.backgroundColor = fillColor
        drawable.borderColor = strokeColor
        drawable.borderWidth = strokeWidth
        drawable.shouldAbsoluteCircle = true
        drawable.attachView(this)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val width = measuredWidth
        val height = measuredHeight
        if (width < height) {
            val size = max(width, height)
            val widthSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
            val heightSpec = MeasureSpec.makeMeasureSpec(size, MeasureSpec.EXACTLY)
            super.onMeasure(widthSpec, heightSpec)
        }
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        hideIfNeeded()
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        var value = text
        if (text != null && text.toString().toInt() > 99) {
            value = "99+"
        }
        super.setText(value, type)
        hideIfNeeded()
    }

    //判断是否需要隐藏
    private fun hideIfNeeded() {
        visibility = if (TextUtils.isDigitsOnly(text)) {
            val value = text.toString().toInt()
            if (value > 0) View.VISIBLE else View.INVISIBLE
        } else {
            if (TextUtils.isEmpty(text)) View.INVISIBLE else View.VISIBLE
        }
    }
}