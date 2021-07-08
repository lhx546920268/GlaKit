package com.lhx.glakit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.widget.AbsListView
import android.widget.ListView
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.base.constant.Position
import kotlin.math.abs
import kotlin.math.min


/**
 * 可悬浮item的listView
 */
class StickListView : ListView {

    //悬浮适配器
    var stickAdapter: StickAdapter? = null

    //外部的滑动监听
    private var _outerOnScrollListener: OnScrollListener? = null

    //滑动监听
    private val _onScrollListener = object : OnScrollListener {
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
                        val position = stickAdapter!!.getCurrentStickPosition(firstItem)
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

    //当前要绘制的item
    private var _stickItem: View? = null

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

    //绘制时需要便宜的y，当两个悬浮item接触时，下一个会把上一个顶上去
    private var _translateY = 0f

    //保存按下事件
    private var _touchDownEvent: MotionEvent? = null

    //可以滑动的最小距离
    private var _touchSlop = 0

    //开始的点击位置
    private val _touchPoint = PointF()

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        _touchSlop = ViewConfiguration.get(context).scaledTouchSlop
        setOnScrollListener(_onScrollListener)
    }


    override fun setOnScrollListener(onScrollListener: OnScrollListener) {
        if (onScrollListener !== _onScrollListener) { //保存外部的滑动监听
            _outerOnScrollListener = onScrollListener
        } else {
            super.setOnScrollListener(onScrollListener)
        }
    }

    //布局固定的item
    private fun layoutStickItem(stickPosition: Int, firstVisibleItem: Int) {
        
        var position = stickPosition
        position += headerViewsCount
        if (_stickItem == null || stickPosition != _stickPosition) {
            _stickItem = adapter.getView(stickPosition, null, this)
            var params = _stickItem!!.layoutParams
            if (params == null) {
                params = generateDefaultLayoutParams()
                _stickItem!!.layoutParams = params
            }

            //计算item大小
            var heightMode = MeasureSpec.getMode(params.height)
            val heightSize = MeasureSpec.getSize(params.height)
            if (heightMode == MeasureSpec.UNSPECIFIED) heightMode = MeasureSpec.EXACTLY

            val widthMeasureSpec = MeasureSpec.makeMeasureSpec(width - paddingLeft - paddingRight, MeasureSpec.EXACTLY)
            val heightMeasureSpec = MeasureSpec.makeMeasureSpec(min(height - paddingTop - paddingBottom, heightSize), heightMode)

            _stickItem!!.measure(widthMeasureSpec, heightMeasureSpec)
            _stickItem!!.layout(0, 0, _stickItem!!.measuredWidth, _stickItem!!.measuredHeight)
        }

        _stickPosition = stickPosition

        //判断下一个item
        val nextPosition = firstVisibleItem + 1
        _translateY =
            if (nextPosition < adapter.count && stickAdapter!!.shouldStickAtPosition(nextPosition)) {
                val child: View = getChildAt(1)
                (child.top - _stickItem!!.bottom).toFloat()
            } else {
                0f
            }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (_stickItem != null) {

            //保存系统本身的画板属性
            canvas.save()
            canvas.translate(0f, _translateY)
            drawChild(canvas, _stickItem, drawingTime)
            //恢复以前的属性
            canvas.restore()
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (_stickItem != null) {
            val bottom = _stickItem!!.bottom
            if (ev.y < bottom) {

                when (ev.action) {
                    MotionEvent.ACTION_UP -> {

                        //是点击 悬浮的item
                        if (_touchDownEvent != null) {
                            _stickItem!!.dispatchTouchEvent(ev)
                            _touchDownEvent!!.recycle()
                            _touchDownEvent = null
                            playSoundEffect(SoundEffectConstants.CLICK)
                            _stickItem!!.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED)
                        }
                        //必须的，否则 悬浮的item 的OnClick事件不会触发
                        super.dispatchTouchEvent(ev)
                    }
                    MotionEvent.ACTION_DOWN -> {

                        _stickItem!!.dispatchTouchEvent(ev)
                        _touchDownEvent = MotionEvent.obtain(ev)
                        _touchPoint.x = ev.x
                        _touchPoint.y = ev.y
                    }
                    MotionEvent.ACTION_MOVE -> {

                        //只有大于 _touchSlop 才算滑动
                        if (abs(ev.y - _touchPoint.y) >= _touchSlop) {

                            //滑动了，发送取消事件
                            val event = MotionEvent.obtain(ev)
                            event.action = MotionEvent.ACTION_CANCEL
                            _stickItem!!.dispatchTouchEvent(event)

                            //发送按下事件，否则无法滑动
                            if (_touchDownEvent != null) {
                                super.dispatchTouchEvent(_touchDownEvent)
                                _touchDownEvent!!.recycle()
                                _touchDownEvent = null
                            }
                            super.dispatchTouchEvent(ev)
                        } else {
                            _stickItem!!.dispatchTouchEvent(ev)
                        }
                    }
                    MotionEvent.ACTION_CANCEL -> {

                        _stickItem!!.dispatchTouchEvent(ev)
                        if (_touchDownEvent != null) {
                            _touchDownEvent!!.recycle()
                            _touchDownEvent = null
                        }
                    }
                }
                return true
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}