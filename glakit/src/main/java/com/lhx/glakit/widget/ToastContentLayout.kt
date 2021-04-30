package com.lhx.glakit.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.TextView
import com.lhx.glakit.R
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.utils.ColorUtils
import com.lhx.glakit.utils.SizeUtils

class ToastContentLayout: FrameLayout {

    val textView: TextView by lazy { findViewById(R.id.text) }
    val container: FrameLayout by lazy { findViewById(R.id.container) }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr){

        isClickable = false
        fitsSystemWindows = true
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val padding = SizeUtils.pxFormDip(15.0f, context)
        container.setPadding(padding, padding, padding, padding)

        val drawable = CornerBorderDrawable()
        drawable.setCornerRadius(SizeUtils.pxFormDip(12.0f, context))
        drawable.backgroundColor = ColorUtils.whitePercentColor(0.0f, 0.8f)
        drawable.attachView(container)
    }
}