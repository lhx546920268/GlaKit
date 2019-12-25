package com.lhx.glakit.refresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import com.lhx.glakit.R
import com.lhx.glakit.drawable.LoadingDrawable
import com.lhx.glakit.utils.SizeUtils
import kotlinx.android.synthetic.main.load_more_footer.view.*

/**
 * 加载更多底部
 */
class LoadMoreFooter : FrameLayout {

    //菊花
    private var loadingDrawable = LoadingDrawable()

    //状态
    var loadingStatus = LoadMoreStatus.NORMAL
    set(value) {
        if(value != field){
            field = value
            update()
        }
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )


    init{
        loadingDrawable.color = Color.GRAY
        loadingDrawable.intrinsicWidth = SizeUtils.pxFormDip(25f, context)
        loadingDrawable.intrinsicHeight = SizeUtils.pxFormDip(25f, context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
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

    //刷新UI
    fun update() {
        when (loadingStatus) {
            LoadMoreStatus.HAS_MORE, LoadMoreStatus.LOADING -> {
                isClickable = false
                imageView.visibility = View.VISIBLE
                loadingDrawable.start()
                textView.text = context.getString(R.string.loading_text)
            }
            LoadMoreStatus.FAIL -> {
                isClickable = true

                imageView.visibility = View.VISIBLE
                loadingDrawable.stop()
                textView.text = context.getString(R.string.load_more_fail)
            }
        }
    }
}