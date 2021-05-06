package com.lhx.glakit.refresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.lhx.glakit.R
import com.lhx.glakit.drawable.LoadingDrawable
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState


/**
 * 默认下拉刷新头部
 */
class DefaultRefreshHeader : RefreshHeader {

    //菊花
    private val loadingDrawable: LoadingDrawable by lazy {
        val drawable = LoadingDrawable()
        drawable.color = Color.GRAY
        drawable
    }

    private val imageView: ImageView by lazy { findViewById(R.id.imageView) }
    private val textView: TextView by lazy { findViewById(R.id.textView) }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()

        imageView.setImageDrawable(loadingDrawable)
        imageView.visibility = View.GONE
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

        imageView.visibility = View.GONE
        loadingDrawable.stop()
        textView.text = "下拉刷新"
    }


    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        return if (shouldCloseImmediately) 0 else 200
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.Refreshing -> {
                imageView.visibility = View.VISIBLE
                loadingDrawable.start()
                textView.text = "加载中..."
            }
            RefreshState.ReleaseToRefresh, RefreshState.RefreshReleased -> textView.text = "松开即可刷新"
            RefreshState.LoadFinish, RefreshState.RefreshFinish -> {
                imageView.visibility = View.GONE
                loadingDrawable.stop()
                textView.text = "刷新成功"
            }
            else -> {
                imageView.visibility = View.GONE
                loadingDrawable.stop()
                textView.text = "下拉刷新"
            }
        }
    }
}