package com.lhx.glakit.base.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.IntDef
import androidx.annotation.LayoutRes
import com.lhx.glakit.GlaKitInitializer
import com.lhx.glakit.R
import com.lhx.glakit.base.constant.OverlayArea
import com.lhx.glakit.base.constant.PageStatus
import com.lhx.glakit.loading.DefaultLoadingView
import com.lhx.glakit.loading.InteractionCallback
import com.lhx.glakit.loading.LoadingView
import com.lhx.glakit.utils.StringUtils
import com.lhx.glakit.utils.ToastUtils
import com.lhx.glakit.utils.ViewUtils


//基础视图容器
class BaseContainer: RelativeLayout, InteractionCallback {

    @IntDef(PageStatus.NORMAL, PageStatus.LOADING, PageStatus.FAIL, PageStatus.EMPTY)
    @Retention(AnnotationRetention.SOURCE)
    annotation class ContainerPageStatus

    //内容视图
    var contentView: View? = null
    private set

    //标题栏
    private var titleBar: TitleBar? = null

    //页面状态
    @ContainerPageStatus
    private var pageStatus = PageStatus.NORMAL

    //页面是否正在载入
    private var pageLoadingView: View? = null

    //显示菊花
    private var loadingView: LoadingView? = null
    private var loading = false

    //页面是否载入失败
    private var pageFailView: View? = null

    //显示空视图信息
    private var emptyView: View? = null

    //添加固定在底部的视图
    var bottomView: View? = null
    private set

    //添加固定在顶部的视图
    var topView: View? = null
    private set

    //顶部视图是否悬浮
    private var topViewFloat = false
    set(value){
        if(value != field){
            field = value
            layoutChildren()
        }
    }

    //事件回调
    var onEventHandler: OnEventHandler? = null

    //加载视图覆盖区域 默认都不覆盖
    var overlayArea = 0

    //<editor-fold desc="constructor">

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        setBackgroundColor(Color.WHITE)
    }

    //</editor-fold>

    //<editor-fold desc="标题栏">

    //设置是否需要显示标题栏
    fun setShowTitleBar(show: Boolean) {
        if (show) { //创建导航栏
            if (titleBar == null) {
                titleBar = TitleBar(context)
                val params = LayoutParams(
                    LayoutParams.MATCH_PARENT,
                    context.resources.getDimensionPixelOffset(R.dimen.title_bar_height)
                )
                titleBar!!.id = R.id.base_fragment_title_bar_id
                addView(titleBar, 0, params)
                layoutChildren()
            }
            titleBar!!.visibility = View.VISIBLE
        } else {
            if (titleBar != null) {
                titleBar!!.visibility = View.GONE
            }
        }
    }

    fun isTitleBarShowing(): Boolean {
        return titleBar != null && titleBar!!.visibility == View.VISIBLE
    }

    //设置标题
    fun setTitle(title: CharSequence?) {
        titleBar?.setTitle(title)
    }

    fun getTitle(): CharSequence? {
        return titleBar?.getTitle()
    }

    fun getTitleBar(): TitleBar? {
        return titleBar
    }

    //显示返回按钮
    fun setShowBackButton(show: Boolean) {
        if (titleBar != null) {
            val textView = titleBar!!.setShowBackButton(show)
            textView?.setOnClickListener(object : OnSingleClickListener() {
                override fun onSingleClick(v: View) {
                    if (onEventHandler != null) {
                        onEventHandler!!.onBack()
                    }
                }
            })
        }
    }

    //</editor-fold>

    //<editor-fold desc="内容">

    fun setContentView(@LayoutRes layoutResId: Int) {
        setContentView(LayoutInflater.from(context).inflate(layoutResId, null, false))
    }

    fun setContentView(view: View?){
        if(contentView != view){
            if(contentView != null){
                removeView(contentView)
            }

            contentView = view
            if(contentView != null){
                contentView!!.apply {
                    id = R.id.base_fragment_content_id
                    val params = if (layoutParams is LayoutParams) {
                        layoutParams as LayoutParams
                    } else {
                        LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
                    }
                    params.alignWithParent = true
                    layoutParams = params

                    if (parent !== this@BaseContainer) {
                        ViewUtils.removeFromParent(this)
                        addView(this, 0)
                    }
                }
            }
            layoutChildren()
        }
    }

    //重新布局子视图
    private fun layoutChildren() {
        if (contentView != null) {
            val params = contentView!!.layoutParams as LayoutParams

            params.addRule(BELOW, 0)
            if (topView != null && !topViewFloat) {
                params.addRule(BELOW, R.id.base_fragment_top_id)
            } else {
                params.addRule(BELOW, R.id.base_fragment_title_bar_id)
            }

            params.addRule(ABOVE, 0)
            if (bottomView != null) {
                params.addRule(ABOVE, R.id.base_fragment_bottom_id)
            }
        }

        if(pageLoadingView != null){

            val params = pageLoadingView!!.layoutParams as LayoutParams

            params.addRule(BELOW, 0)
            if (topView != null && !topViewFloat && (overlayArea and OverlayArea.PAGE_LOADING_TOP == OverlayArea.PAGE_LOADING_TOP)) {
                params.addRule(BELOW, R.id.base_fragment_top_id)
            } else {
                params.addRule(BELOW, R.id.base_fragment_title_bar_id)
            }

            params.addRule(ABOVE, 0)
            if (bottomView != null && (overlayArea and OverlayArea.PAGE_LOADING_BOTTOM == OverlayArea.PAGE_LOADING_BOTTOM)) {
                params.addRule(ABOVE, R.id.base_fragment_bottom_id)
            }
        }

        if(emptyView != null){

            val params = emptyView!!.layoutParams as LayoutParams

            params.addRule(BELOW, 0)
            if (topView != null && !topViewFloat && (overlayArea and OverlayArea.EMPTY_TOP == OverlayArea.EMPTY_TOP)) {
                params.addRule(BELOW, R.id.base_fragment_top_id)
            } else {
                params.addRule(BELOW, R.id.base_fragment_title_bar_id)
            }

            params.addRule(ABOVE, 0)
            if (bottomView != null && (overlayArea and OverlayArea.EMPTY_BOTTOM == OverlayArea.EMPTY_BOTTOM)) {
                params.addRule(ABOVE, R.id.base_fragment_bottom_id)
            }
        }
    }

    //</editor-fold>

    //<editor-fold desc="InteractionCallback">

    override fun showLoading(delay: Long, text: CharSequence?) {
        if (!loading) {
            loading = true
            if (GlaKitInitializer.loadViewClass != null) {
                try {
                    loadingView = GlaKitInitializer.loadViewClass!!.getConstructor(Context::class.java).newInstance(context)
                } catch (e: Exception) {
                    throw IllegalStateException("loadViewClass 无法通过context实例化")
                }
            } else {
                loadingView = LayoutInflater.from(context).inflate(R.layout.default_loading_view, this, false) as LoadingView?
            }
            loadingView!!.delay = delay
            if (loadingView is DefaultLoadingView) {
                val loadingText = if(StringUtils.isEmpty(text)) context.getString(R.string.loading_text) else text
                (loadingView as DefaultLoadingView).getTextView().text = loadingText
            }
            val params = loadingView!!.layoutParams as LayoutParams
            params.alignWithParent = true
            params.addRule(BELOW, R.id.base_fragment_title_bar_id)
            params.addRule(ALIGN_PARENT_BOTTOM)
            addView(loadingView)
        }
    }

    override fun hideLoading() {
        if(loading){
            removeView(loadingView)
            loadingView = null
        }
    }

    override fun showToast(text: CharSequence, icon: Int) {
        ToastUtils.showToast(context, text, icon)
    }

    //</editor-fold>

    //<editor-fold desc="Loading">

    fun setPageStatus(@ContainerPageStatus status: Int){
        if(status != pageStatus){
            when(pageStatus){
                PageStatus.LOADING -> {
                    if(status != PageStatus.FAIL){
                        removeView(pageLoadingView)
                        pageLoadingView = null
                    }
                }
                PageStatus.FAIL -> {
                    if(status != PageStatus.LOADING){
                        removeView(pageLoadingView)
                        pageLoadingView = null
                    }
                }
                PageStatus.EMPTY -> {
                    removeView(emptyView)
                    emptyView = null
                }
            }
            pageStatus = status

            when(pageStatus){
                PageStatus.LOADING, PageStatus.FAIL -> {
                    loadPageLoadingViewIfNeeded()
                    onEventHandler?.onShowPageLoadingView(pageLoadingView!!)
                }
                PageStatus.EMPTY -> {
                    loadEmptyViewIfNeeded()
                    onEventHandler?.onShowEmptyView(emptyView!!)
                }
            }

            layoutChildren()
        }
    }

    //加载 pageLoading 如果需要
    private fun loadPageLoadingViewIfNeeded(){
        if((pageStatus == PageStatus.LOADING || pageStatus == PageStatus.FAIL) && pageLoadingView == null){
            if (GlaKitInitializer.pageLoadingViewClass != null) {
                pageLoadingView = try {
                    GlaKitInitializer.pageLoadingViewClass!!.getConstructor(Context::class.java)
                        .newInstance(context)
                } catch (e: Exception) {
                    throw IllegalStateException("pageLoadingViewClass 无法通过context实例化")
                }
            } else {
                pageLoadingView = LayoutInflater.from(context).inflate(R.layout.page_loading_view, this, false)
            }
            pageLoadingView!!.setOnClickListener(object : OnSingleClickListener(){
                override fun onSingleClick(v: View) {
                    if(pageStatus == PageStatus.FAIL){
                        onEventHandler?.onReloadPage()
                    }
                }
            })
            addView(pageLoadingView)
        }
    }

    //加载空视图如果需要
    private fun loadEmptyViewIfNeeded(){
        if(pageStatus == PageStatus.EMPTY && emptyView == null){
            emptyView = LayoutInflater.from(context).inflate(R.layout.page_empty_view, this, false)
            emptyView!!.isClickable = true
            addView(emptyView)
        }
    }

    fun isPageLoading(): Boolean {
        return pageStatus == PageStatus.LOADING
    }

    fun isPageLoadFail(): Boolean {
        return pageStatus == PageStatus.FAIL
    }

    fun isLoading(): Boolean {
        return loading
    }

    //</editor-fold>

    //<editor-fold desc="顶部底部视图">

    //设置底部视图

    fun setBottomView(view: View?, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
        if (bottomView !== view) {
            if (bottomView != null) {
                removeView(bottomView)
            }
            bottomView = view
            if (bottomView != null){
                bottomView?.apply {
                    val params = if (layoutParams is LayoutParams) {
                        layoutParams as LayoutParams
                    } else {
                        LayoutParams(LayoutParams.MATCH_PARENT, height)
                    }

                    params.addRule(ALIGN_PARENT_BOTTOM)
                    layoutParams = params
                    id = R.id.base_fragment_bottom_id
                    if (parent !== this@BaseContainer) {
                        ViewUtils.removeFromParent(this)
                        addView(this)
                    }
                }
            }

            layoutChildren()
        }
    }

    fun setBottomView(@LayoutRes res: Int) {
        setBottomView(LayoutInflater.from(context).inflate(res, this, false))
    }

    //设置顶部视图
    fun setTopView(view: View?, height: Int = ViewGroup.LayoutParams.WRAP_CONTENT) {
        if (topView !== view) {
            if (topView != null) {
                removeView(topView)
            }
            topView = view
            if(topView != null){
                topView!!.apply {
                    val params = if (layoutParams is LayoutParams) {
                        layoutParams as LayoutParams
                    } else {
                        LayoutParams(LayoutParams.MATCH_PARENT, height)
                    }
                    params.addRule(BELOW, R.id.base_fragment_title_bar_id)
                    layoutParams = params

                    id = R.id.base_fragment_top_id
                    if (parent !== this@BaseContainer) {
                        ViewUtils.removeFromParent(this)
                        addView(topView)
                    }
                }
            }

            layoutChildren()
        }
    }

    fun setTopView(@LayoutRes res: Int) {
        setTopView(LayoutInflater.from(context).inflate(res, this, false))
    }

    //</editor-fold>


    //事件回调
    interface OnEventHandler {

        /**
         * 页面刷新
         */
        fun onReloadPage()

        /**
         * 点击返回按钮
         */
        fun onBack()

        /**
         * 页面加载视图显示
         * @param pageLoadingView 页面加载视图
         */
        fun onShowPageLoadingView(pageLoadingView: View)

        /**
         * 空视图显示
         * @param emptyView 空视图
         */
        fun onShowEmptyView(emptyView: View)
    }
}