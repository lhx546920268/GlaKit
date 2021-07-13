package com.lhx.glakit.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.PointF
import android.util.AttributeSet
import android.view.*
import android.view.accessibility.AccessibilityEvent
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.StickAdapter
import kotlin.math.abs
import kotlin.math.min


/**
 * 可悬浮item的RecyclerView
 */
class StickRecyclerView : RecyclerView {

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

    //当前要绘制的item
    private var _stickItem: View? = null

    //当前悬浮的position
    private var _stickPosition = NO_POSITION
        set(value) {
            if (field != value) {
                if (field != NO_POSITION && _stickItem != null) {
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
    private var _touchPoint: PointF? = null

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        _touchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    //设置是否可以悬浮
    private fun setStickEnable(stickEnable: Boolean) {
        if (_stickEnable != stickEnable) {
            _stickEnable = stickEnable
            if (_stickEnable && _onScrollListener == null) {
                _touchPoint = PointF()
                _onScrollListener = object : OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        val childCount = recyclerView.childCount
                        if (stickAdapter != null && childCount > 0) {

                            val child = recyclerView.getChildAt(0)
                            val firstVisibleItem = recyclerView.getChildLayoutPosition(child)

                            if (stickAdapter!!.shouldStickAtPosition(firstVisibleItem)) {
                                if (child.top != paddingTop) {

                                    //当前的悬浮item已超出recyclerView 顶部
                                    layoutStickItem(firstVisibleItem, firstVisibleItem)
                                } else {
                                    _stickPosition = NO_POSITION
                                    _stickItem = null
                                }
                            } else {

                                //悬浮的item已在 firstVisibleItem 前面了
                                val position = stickAdapter!!.getCurrentStickPosition(firstVisibleItem, _stickPosition)
                                if (position < firstVisibleItem && stickAdapter!!.shouldStickAtPosition(position)) {

                                    layoutStickItem(position, firstVisibleItem)
                                } else {
                                    _stickPosition = NO_POSITION
                                    _stickItem = null
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

        if(adapter != null){

            if (_stickItem == null || stickPosition != _stickPosition) {
                _stickItem = _recycler!!.getViewForPosition(stickPosition)
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
            _translateY = if (nextPosition < adapter!!.itemCount && stickAdapter!!.shouldStickAtPosition(nextPosition)) {

                    val child = getChildAt(1)
                    (child.top - _stickItem!!.bottom).toFloat()
                } else {
                    0f
                }
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (_stickItem != null) { //保存系统本身的画板属性
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
                        _touchPoint!!.x = ev.x
                        _touchPoint!!.y = ev.y
                    }
                    MotionEvent.ACTION_MOVE -> {

                        //只有大于 _touchSlop 才算滑动
                        if (abs(ev.y - _touchPoint!!.y) >= _touchSlop) {

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