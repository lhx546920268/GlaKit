package com.lhx.glakitDemo.nest

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ParentLayoutManager: LinearLayoutManager {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
        context,
        orientation,
        reverseLayout
    )

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    var callback: (() -> ChildRecyclerView?)? = null

    var currentScrollState = RecyclerView.SCROLL_STATE_IDLE

    override fun canScrollVertically(): Boolean {
        if (callback != null) {
            val child = callback!!()
            val result = child == null || child.isScrollTop || currentScrollState != RecyclerView.SCROLL_STATE_IDLE
//            println("canScrollVertically $result")
            return result
        }
        return super.canScrollVertically()
    }
}