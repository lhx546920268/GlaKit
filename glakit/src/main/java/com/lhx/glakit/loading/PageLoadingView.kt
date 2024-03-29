package com.lhx.glakit.loading

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import com.lhx.glakit.base.constant.PageStatus
import com.lhx.glakit.base.widget.VoidCallback

/**
 * 页面加载接口
 */
abstract class PageLoadingView: FrameLayout {

    /**
     * 当前状态
     */
    open var status: PageStatus = PageStatus.NORMAL

    /**
     * 刷新回调
     */
    var reloadCallback: VoidCallback? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )
}