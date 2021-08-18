package com.lhx.glakitDemo.nest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.widget.StickRecyclerView

class ParentRecyclerView: StickRecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val flingHelper by lazy { FlingHelper(context) }

    var callback: (() -> ChildRecyclerView?)? = null

    val isScrollEnd: Boolean
        get() = !canScrollVertically(1)

    private val currentChildRecyclerView: ChildRecyclerView?
        get() = if (callback != null) callback!!() else null

    init {
        addOnScrollListener(object : OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                println("onScrollStateChanged $newState")
                if (newState == SCROLL_STATE_IDLE) {
                    childFlingIfEnabled()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (flingStarting) {
                    totalDyConsumed += dy
                    println("onScrolled fling $totalDyConsumed")
                } else {
                    totalDyConsumed = 0
                }
            }
        })
    }

    //滑动
    private var totalDyConsumed = 0

    private var lastY = 0f
    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if(lastY == 0f) lastY = e.y

        if(callback != null && isScrollEnd) {
            //如果父RecyclerView已经滑动到底部，需要让子RecyclerView滑动剩余的距离
            val childRecyclerView = callback!!()
            childRecyclerView?.apply {
                val deltaY = (lastY - e.y).toInt()
                if(deltaY != 0) {
                    scrollBy(0, deltaY)
                }
            }
        }
        lastY = e.y
        return super.onTouchEvent(e)
    }

    private var targetVelocityY = 0
    private var flingStarting = false
    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        println("fling $velocityY")
        val fling = super.fling(velocityX, velocityY)
        if (fling && velocityY > 0) {
            //向下快速滑动了，如果滑动距离超过父视图的可滑动范围，继续让子视图滑动
            flingStarting = true
            targetVelocityY = velocityY
        } else {
            flingStarting = false
            targetVelocityY = 0
        }
        return fling
    }

    private fun childFlingIfEnabled() {
        if (flingStarting) {
            flingStarting = false
            val distance = flingHelper.getSplineFlingDistance(targetVelocityY)
            val remain = distance - totalDyConsumed
            if (remain > 0) {
                val velocity = flingHelper.getSplineFlingVelocity(remain)
                currentChildRecyclerView?.fling(0, velocity)
            }
            totalDyConsumed = 0
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        println("dispatchTouchEvent")
        return super.dispatchTouchEvent(ev)
    }

    override fun onNestedFling(
        target: View?,
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        println("onNestedFling ${target ?: "null"}")
        return super.onNestedFling(target, velocityX, velocityY, consumed)
    }

    override fun onNestedPreFling(target: View?, velocityX: Float, velocityY: Float): Boolean {
        println("onNestedPreFling ${target ?: "null"}")
        return super.onNestedPreFling(target, velocityX, velocityY)
    }
}