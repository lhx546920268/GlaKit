package com.lhx.glakit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ListView
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.base.constant.Position
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.extension.WRAP_CONTENT
import com.lhx.glakit.extension.gone
import com.lhx.glakit.extension.visible
import kotlin.math.min

/**
 * 可悬浮item的listView
 */
class StickListView : ListView {

    //悬浮适配器
    var stickAdapter: StickAdapter? = null
        set(value) {
            field = value
            if (field != null) {
                initListener()
            }
        }

    //外部的滑动监听
    private var _outerOnScrollListener: OnScrollListener? = null

    //滑动监听
    private var _onScrollListener: OnScrollListener? = null

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
    private var _stickPosition = RecyclerView.NO_POSITION
        set(value) {
            if (field != value) {
                if (field != RecyclerView.NO_POSITION && stickContainer.stickItem != null) {
                    stickAdapter?.onViewStickChange(false, stickContainer, field)
                }
                field = value
                if (field != RecyclerView.NO_POSITION && stickContainer.stickItem != null) {
                    stickAdapter?.onViewStickChange(true, stickContainer, field)
                    stickContainer.visible()
                } else {
                    stickContainer.stickItem = null
                    stickContainer.gone()
                }
            }
        }

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun setOnScrollListener(onScrollListener: OnScrollListener) {
        if (onScrollListener !== _onScrollListener) { //保存外部的滑动监听
            _outerOnScrollListener = onScrollListener
        } else {
            super.setOnScrollListener(onScrollListener)
        }
    }

    private fun initListener() {
        if (_onScrollListener == null) {
            _onScrollListener = object : OnScrollListener {
                override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {
                    if (_outerOnScrollListener != null) {
                        _outerOnScrollListener!!.onScrollStateChanged(view, scrollState)
                    }
                }

                override fun onScroll(view: AbsListView, firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {

                    var firstItem = firstVisibleItem
                    if (_outerOnScrollListener != null) {
                        _outerOnScrollListener!!.onScroll(view, firstItem, visibleItemCount, totalItemCount)
                    }
                    if (stickAdapter != null && childCount > 0) {

                        firstItem -= headerViewsCount
                        if (stickAdapter!!.shouldStickAtPosition(firstItem)) {

                            val child = getChildAt(0)
                            if (child.top != paddingTop) {

                                //当前的悬浮item已超出listView 顶部
                                layoutStickItem(firstItem, firstItem)
                            } else {
                                _stickPosition = Position.NO_POSITION
                            }
                        } else {

                            //悬浮的item已在 firstVisibleItem 前面了
                            val position = stickAdapter!!.getCurrentStickPosition(firstItem, _stickPosition)
                            if (position < firstItem && stickAdapter!!.shouldStickAtPosition(position)) {

                                layoutStickItem(position, firstItem)
                            } else {
                                _stickPosition = Position.NO_POSITION
                            }
                        }
                    }
                }
            }
            setOnScrollListener(_onScrollListener!!)
        }
    }

    //布局固定的item
    private fun layoutStickItem(stickPosition: Int, firstVisibleItem: Int) {
        if (adapter != null && stickAdapter != null) {
            var position = stickPosition
            position += headerViewsCount
            if (stickContainer.stickItem == null || stickPosition != _stickPosition) {
                stickContainer.stickItem = stickAdapter!!.getStickView(_stickPosition) ?: adapter.getView(_stickPosition, null, stickContainer)
            }

            _stickPosition = stickPosition

            //判断下一个item
            val nextPosition = firstVisibleItem + 1
            val y = if (nextPosition < adapter.count
                && stickAdapter!!.shouldStickAtPosition(nextPosition)) {
                val child: View = getChildAt(1)
                min(child.top - stickContainer.bottom, 0).toFloat()
            } else {
                0f
            }

            stickContainer.translationY = y
        }
    }
}