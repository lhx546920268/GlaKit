package com.lhx.glakit.refresh

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.lhx.glakit.drawable.LoadingDrawable
import com.scwang.smartrefresh.layout.api.RefreshKernel
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import kotlinx.android.synthetic.main.default_refresh_header.view.*


/**
 * 默认下拉刷新头部
 */
class DefaultRefreshHeader : RefreshHeader {

    //菊花
    private var _loadingDrawable = LoadingDrawable()

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        _loadingDrawable.color = Color.GRAY
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        _loadingDrawable = LoadingDrawable()

        imageView.setImageDrawable(_loadingDrawable)
        imageView.visibility = View.GONE
    }

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {

        imageView.visibility = View.GONE
        _loadingDrawable.stop()
        textView.text = "下拉刷新"
    }


    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {

        imageView.visibility = View.GONE
        _loadingDrawable.stop()
        textView!!.text = "刷新成功"

        return if (shouldCloseImmediately) 0 else 200
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.Refreshing -> {
                imageView.visibility = View.VISIBLE
                _loadingDrawable.start()
                textView!!.text = "加载中..."
            }
            RefreshState.ReleaseToRefresh -> textView.text = "松开即可刷新"
            else -> {
                imageView.visibility = View.GONE
                _loadingDrawable.stop()
                textView.text = "下拉刷新"
            }
        }
    }
}