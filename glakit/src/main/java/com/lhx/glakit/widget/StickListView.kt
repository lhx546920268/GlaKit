package com.lhx.glakit.widget

import android.content.Context
import android.util.AttributeSet
import android.view.*
import android.widget.AbsListView
import android.widget.FrameLayout
import android.widget.ListView
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.base.constant.Position
import com.lhx.glakit.extension.removeFromParent
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

    //当前要绘制的item
    private var _stickItem: View? = null
        set(value) {
            if (field != value){
                field?.removeFromParent()
                field = value
            }
        }

    //当前悬浮的position
    private var _stickPosition = Position.NO_POSITION
        set(value) {
            if (field != value) {
                if (field != Position.NO_POSITION && _stickItem != null) {
                    stickAdapter?.onViewStickChange(false, _stickItem!!, field)
                }
                field = value
                if (_stickItem != null) {
                    stickAdapter?.onViewStickChange(true, _stickItem!!, field)
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
                                _stickItem = null
                            }
                        } else {

                            //悬浮的item已在 firstVisibleItem 前面了
                            val position = stickAdapter!!.getCurrentStickPosition(firstItem, _stickPosition)
                            if (position < firstItem && stickAdapter!!.shouldStickAtPosition(position)) {

                                layoutStickItem(position, firstItem)
                            } else {
                                _stickPosition = Position.NO_POSITION
                                _stickItem = null
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
        
        var position = stickPosition
        position += headerViewsCount
        if (_stickItem == null || stickPosition != _stickPosition) {
            require(parent is FrameLayout) {
                "The StickListView parent must a FrameLayout"
            }
            val frameLayout = parent as FrameLayout
            _stickItem = adapter.getView(stickPosition, null, frameLayout)
            val params = FrameLayout.LayoutParams(_stickItem!!.layoutParams)
            params.topMargin = 0
            params.leftMargin = 0
            params.rightMargin = 0
            frameLayout.addView(_stickItem, params)
        }

        _stickPosition = stickPosition

        //判断下一个item
        val nextPosition = firstVisibleItem + 1
        val y = if (nextPosition < adapter.count
            && stickAdapter!!.shouldStickAtPosition(nextPosition)) {
                val child: View = getChildAt(1)
                min(child.top - _stickItem!!.bottom, 0).toFloat()
            } else {
                0f
            }
        println("translation $y")
        _stickItem?.translationY = y
    }
}