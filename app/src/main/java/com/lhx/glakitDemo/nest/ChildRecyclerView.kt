package com.lhx.glakitDemo.nest

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
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

    private val parentRecyclerView: ParentRecyclerView by lazy {
        var p = parent
        while (p != null && p !is ParentRecyclerView) {
            p = p.parent
        }
        p as ParentRecyclerView
    }

    val isScrollTop: Boolean
        get() = !canScrollVertically(-1)

    private var lastY = 0f
    private var parentScrolling = false

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(e: MotionEvent): Boolean {
        if(lastY == 0f) lastY = e.y
        if(isScrollTop) {
            parentScrolling = true
            //如果父RecyclerView已经滑动到底部，需要让子RecyclerView滑动剩余的距离
            parentRecyclerView.apply {
                val deltaY = (lastY - e.y).toInt()
                println("y $deltaY")
                if(deltaY != 0) {
                    scrollBy(0, deltaY)
                    return false
                }
            }
        }
        lastY = e.y
        if (e.action == MotionEvent.ACTION_CANCEL) parentScrolling = false
        return super.onTouchEvent(e)
    }
}