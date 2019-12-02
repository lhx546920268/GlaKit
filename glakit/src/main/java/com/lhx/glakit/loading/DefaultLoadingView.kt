package com.lhx.glakit.loading

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.drawable.DrawableUtils
import com.lhx.glakit.drawable.LoadingDrawable
import com.lhx.glakit.utils.SizeUtils
import kotlinx.android.synthetic.main.default_loading_view.view.*

/**
 * 默认的loading
 */
class DefaultLoadingView: LoadingView {

    //菊花
    private var loadingDrawable = LoadingDrawable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init{
        loadingDrawable.intrinsicWidth = SizeUtils.pxFormDip(25f, context)
        loadingDrawable.intrinsicHeight = SizeUtils.pxFormDip(25f, context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val drawable = CornerBorderDrawable()
        drawable.setCornerRadius(SizeUtils.pxFormDip(8f, context))
        drawable.backgroundColor = Color.parseColor("#4c4c4c")
        drawable.attachView(container)

        imageView.setImageDrawable(loadingDrawable)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        loadingDrawable.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        loadingDrawable.stop()
    }

    fun getTextView(): TextView {
        return textView
    }

    override fun getContentView(): View {
        return container
    }

}