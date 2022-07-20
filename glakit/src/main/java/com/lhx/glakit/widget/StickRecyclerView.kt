package com.lhx.glakit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.extension.WRAP_CONTENT
import com.lhx.glakit.extension.gone
import com.lhx.glakit.extension.visible
import kotlin.math.min


/**
 * 可悬浮item的RecyclerView
 */
open class StickRecyclerView : RecyclerView {

    //悬浮适配器
    var stickAdapter: StickAdapter? = null
    set(value) {
        if(value != field){
            field = value
            setStickEnable(field != null)
        }
    }

    private var _recycler: Recycler? = null

    //外部 ViewCacheExtension
    private var outerViewCacheExtension: ViewCacheExtension? = null

    //滑动监听
    private var _onScrollListener: OnScrollListener? = null

    //是否可以悬浮
    private var _stickEnable = false

    //置顶容器
    private val stickContainer: StickContainer by lazy {
        val container = StickContainer(context)
        require(parent is FrameLayout) {
            "The StickRecyclerView parent must a FrameLayout"
        }
        val frameLayout = parent as FrameLayout
        frameLayout.addView(container, ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT))
        container
    }

    //当前悬浮的position
    private var _stickPosition = NO_POSITION
        set(value) {
            if (field != value) {
                if (field != NO_POSITION && stickContainer.stickItem != null) {
                    stickAdapter?.onViewStickChange(false, stickContainer, field)
                }
                field = value
                if (field != NO_POSITION && stickContainer.stickItem != null) {
                    stickAdapter?.onViewStickChange(true, stickContainer, field)
                    stickContainer.visible()
                } else {
                    stickContainer.stickItem = null
                    stickContainer.gone()
                }
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //设置是否可以悬浮
    private fun setStickEnable(stickEnable: Boolean) {
        if (_stickEnable != stickEnable) {
            _stickEnable = stickEnable
            if (_stickEnable && _onScrollListener == null) {
                _onScrollListener = object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val childCount = recyclerView.childCount
                        if (stickAdapter != null && childCount > 0) {

                            var child = recyclerView.getChildAt(0)
                            val offset = stickAdapter!!.getStickOffset()
                            if (child.bottom <= offset && childCount > 1) {
                                child = recyclerView.getChildAt(1)
                            }
                            val firstVisibleItem = recyclerView.getChildLayoutPosition(child)

                            if (stickAdapter!!.shouldStickAtPosition(firstVisibleItem)) {
                                if (child.top != paddingTop + offset) {

                                    //当前的悬浮item已超出recyclerView 顶部
                                    layoutStickItem(firstVisibleItem, firstVisibleItem)
                                } else {
                                    _stickPosition = NO_POSITION
                                }
                            } else {

                                //悬浮的item已在 firstVisibleItem 前面了
                                val position = stickAdapter!!.getCurrentStickPosition(firstVisibleItem, _stickPosition)
                                if (position < firstVisibleItem && stickAdapter!!.shouldStickAtPosition(position)) {

                                    layoutStickItem(position, firstVisibleItem)
                                } else {
                                    _stickPosition = NO_POSITION
                                }
                            }
                        }
                    }
                }

                setViewCacheExtension(object : ViewCacheExtension() {

                    override fun getViewForPositionAndType(recycler: Recycler, position: Int, type: Int): View? {
                        _recycler = recycler
                        return if (outerViewCacheExtension != null) {
                            outerViewCacheExtension!!.getViewForPositionAndType(recycler, position, type)
                        } else null
                    }
                })
                addOnScrollListener(_onScrollListener!!)
            }
        }
    }

    //布局固定的item
    private fun layoutStickItem(stickPosition: Int, firstVisibleItem: Int) {

        if(adapter != null && stickAdapter != null){

            if (stickContainer.stickItem == null || stickPosition != _stickPosition) {
                stickContainer.stickItem = stickAdapter!!.getStickView(_stickPosition) ?: _recycler!!.getViewForPosition(stickPosition)
            }

            _stickPosition = stickPosition

            //判断下一个item
            val nextPosition = firstVisibleItem + 1
            val y = if (nextPosition < adapter!!.itemCount
                && stickAdapter!!.shouldStickAtPosition(nextPosition)) {
                    val child = getChildAt(1)
                    min(child.top - stickContainer.bottom, 0).toFloat()
                } else {
                    0f
                }
            stickContainer.translationY = y + stickAdapter!!.getStickOffset()
        }
    }
}