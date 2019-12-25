package com.lhx.glakit.base.fragment


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.CallSuper
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import com.lhx.glakit.GlaKitInitializer
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.refresh.DefaultRefreshHeader
import com.lhx.glakit.refresh.RefreshHeader
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.widget.BackToTopButton
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import kotlinx.android.synthetic.main.list_refresh_fragment.*


/**
 * 可下拉刷新的
 */
@Suppress("unused_parameter")
abstract class RefreshableFragment: BaseFragment(), RefreshHeader.RefreshOnScrollHandler, OnRefreshListener {

    //<editor-fold desc="变量">
    
    //当前第几页
    protected var curPage = 0

    //是否正在下拉刷新
    private var _refreshing = false
    val isRefreshing: Boolean
    get() = _refreshing

    //回到顶部按钮
    private var _backToTopButton: BackToTopButton? = null

    //回到顶部按钮图标
    @DrawableRes
    protected var scrollToTopIconRes = 0

    //当前用来下拉刷新的视图
    private var _refreshView: View? = null

    //刷新头部
    protected var _refreshHeader: RefreshHeader? = null

    //是否有下拉刷新功能
    fun hasRefresh(): Boolean {
        return false
    }

    //</editor-fold>

    //<editor-fold desc="刷新">

    //开始下拉刷新，子类重写
    final override fun onRefresh(refreshLayout: RefreshLayout) {
        _refreshHeader?.shouldCloseImmediately = false
        _refreshing = true
        onRefresh()
    }

    //
    fun onRefresh() {}

    @CallSuper
    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {
        curPage = GlaKitInitializer.HttpFirstPage
        if (shouldDisplayBackToTop()) {
            scrollToTopIconRes = R.drawable.back_to_top_icon
        }
    }


    override fun setContainerContentView(contentView: View?) {
        super.setContainerContentView(contentView)
        initRefreshLayout()
    }

    override fun setContainerContentView(layoutResId: Int) {
        super.setContainerContentView(layoutResId)
        initRefreshLayout()
    }

    //设置是否可以刷新
    fun setRefreshEnable(enable: Boolean) {
        smartRefreshLayout?.setEnableRefresh(enable)
    }

    //初始化刷新控件
    private fun initRefreshLayout() {
        if (hasRefresh()) {
            _refreshHeader = getRefreshHeader()
            _refreshHeader?.apply {

                onScrollHandler = this@RefreshableFragment
                smartRefreshLayout?.apply {
                    setHeaderHeight(50f)
                    setRefreshHeader(_refreshHeader!!)
                    setOnRefreshListener(this@RefreshableFragment)
                    setEnableAutoLoadMore(false)
                    setEnableLoadMore(false)
                }
            }

        }
    }

    //获取下拉刷新头部
    protected fun getRefreshHeader(): RefreshHeader {
        return if (GlaKitInitializer.refreshHeaderClass != null) {
            try {
                GlaKitInitializer.refreshHeaderClass!!.getConstructor(Context::class.java).newInstance(context)
            } catch (e: Exception) {
                throw IllegalStateException("refreshHeaderClass 无法通过context实例化")
            }
        } else {
            LayoutInflater.from(context).inflate(R.layout.default_refresh_header, null) as DefaultRefreshHeader
        }
    }

    //停止下拉刷新
    @CallSuper
    fun stopRefresh() {
        stopRefresh(false)
    }

    //停止下拉刷新
    @CallSuper
    fun stopRefresh(closeImmediately: Boolean) {
        if (smartRefreshLayout != null && _refreshing) {
            _refreshing = false
            _refreshHeader!!.shouldCloseImmediately = closeImmediately
            smartRefreshLayout.finishRefresh()
        }
    }

    //手动刷新
    fun autoRefresh() {
        if (smartRefreshLayout != null && !_refreshing) {
            smartRefreshLayout.autoRefresh()
        }
    }

    fun setRefreshView(refreshView: View) {
        if (_refreshView !== refreshView) {
            _refreshView = refreshView
        }
    }

    override fun onScroll(isDragging: Boolean, percent: Float, offset: Int) {

    }

    //</editor-fold>

    //<editor-fold desc="回到顶部">

    //是否需要显示回到顶部按钮
    fun shouldDisplayBackToTop(): Boolean {
        return true
    }

    fun getBackToTopButton(): BackToTopButton? {
        if (scrollToTopIconRes == 0) return null

        if (_backToTopButton == null) {
            _backToTopButton = BackToTopButton(context)
            _backToTopButton?.apply{
                setImageResource(scrollToTopIconRes)
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
        return _backToTopButton
    }

    //</editor-fold>

    //返回自定义的 layout res
    @LayoutRes
    fun getContentRes(): Int {
        return 0
    }
}