package com.lhx.glakitDemo.nest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.widget.StickRecyclerView
import kotlin.math.abs

class ChildRecyclerView: StickRecyclerView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val flingHelper by lazy { FlingHelper(context) }

    private val parentRecyclerView: ParentRecyclerView by lazy {
        var p = parent
        while (p != null && p !is ParentRecyclerView) {
            p = p.parent
        }
        p as ParentRecyclerView
    }

    val isScrollTop: Boolean
        get() = !canScrollVertically(-1)

    //滑动
    private var totalDyConsumed = 0

    init {
        addOnScrollListener(object : OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                println("onChildScrollStateChanged $newState")
                if (newState == SCROLL_STATE_IDLE) {
                    println("onChildScrolled fling $totalDyConsumed")
                    parentFlingIfEnabled()
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (flingStarting) {
                    totalDyConsumed += dy
                }
            }
        })
    }

    private var lastY = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        println("child y ${e.y}")
        when(e.action) {
            MotionEvent.ACTION_MOVE -> {
                val deltaY = (lastY - e.y).toInt()
                if((isScrollTop && deltaY < 0) || !parentRecyclerView.isScrollEnd) {
                    //如果父RecyclerView已经滑动到底部，需要让子RecyclerView滑动剩余的距离
                    println("child scroll y $deltaY")
                    if(deltaY != 0) {
                        parentRecyclerView.scrollBy(0, deltaY)
                        return false
                    }
                }
            }
        }
        lastY = e.y
        return super.onTouchEvent(e)
    }

    private var targetVelocityY = 0
    private var flingStarting = false
    override fun fling(velocityX: Int, velocityY: Int): Boolean {
        println("child fling $velocityY")
        val fling = super.fling(velocityX, velocityY)
        if (fling && velocityY < 0) {
            //向下快速滑动了，如果滑动距离超过父视图的可滑动范围，继续让子视图滑动
            flingStarting = true
            totalDyConsumed = 0
            targetVelocityY = velocityY
        } else {
            flingStarting = false
            targetVelocityY = 0
        }
        return fling
    }

    private fun parentFlingIfEnabled() {
        if (flingStarting) {
            flingStarting = false
            val distance = flingHelper.getSplineFlingDistance(targetVelocityY)
            val remain = distance - abs(totalDyConsumed)
            println("child fling remain $remain $totalDyConsumed $distance")
            if (remain > 0) {
                val velocity = flingHelper.getSplineFlingVelocity(remain)
                parentRecyclerView.fling(0, -velocity)
            }
        }
    }
}