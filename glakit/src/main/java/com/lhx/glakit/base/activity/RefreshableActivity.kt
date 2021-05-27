package com.lhx.glakit.base.activity

import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.LayoutRes
import com.lhx.glakit.GlaKitInitializer
import com.lhx.glakit.R
import com.lhx.glakit.base.interf.RefreshablePage
import com.lhx.glakit.refresh.RefreshHeader
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.widget.BackToTopButton
import com.scwang.smartrefresh.layout.SmartRefreshLayout

/**
 * 可下拉刷新的
 */
abstract class RefreshableActivity: BaseContainerActivity(), RefreshablePage {

    override var curPage = GlaKitInitializer.HttpFirstPage

    override var refreshing = false
    override var refreshHeader: RefreshHeader? = null
    override val hasRefresh: Boolean = false

    override var smartRefreshLayout: SmartRefreshLayout? = null

    //</editor-fold>

    //<editor-fold desc="回到顶部">

    //回到顶部按钮
    protected var backToTopButton: BackToTopButton? = null
        get() {
            if (!shouldDisplayBackToTop) return null
            if (field == null) {
                field = BackToTopButton(this)
                field?.apply{
                    setImageResource(R.drawable.back_to_top_icon)
                    val params = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    params.setMargins(0, 0, SizeUtils.pxFormDip(20f, context), SizeUtils.pxFormDip(20f, context))
                    params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT)
                    params.addRule(RelativeLayout.ABOVE, R.id.base_fragment_bottom_id)
                    params.alignWithParent = true

                    visibility = View.GONE
                    layoutParams = params
                    baseContainer?.addView(this)
                }
            }
            return field
        }

    //是否需要显示回到顶部按钮
    open val shouldDisplayBackToTop = false

    //返回自定义的 layout res
    @LayoutRes
    open fun getRefreshableContentRes(): Int {
        return 0
    }
}