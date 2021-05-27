package com.lhx.glakit.widget

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView

/**
 * 跑马灯
 * android:ellipsize="marquee"
 * android:focusable="true"
 * android:focusableInTouchMode="true"
 * android:marqueeRepeatLimit="marquee_forever"
 * android:singleLine="true"
 * android:scrollHorizontally="true"
 */
class MarqueeTextView: AppCompatTextView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun isFocused(): Boolean {
        return true
    }
}