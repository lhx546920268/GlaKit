package com.lhx.glakitDemo.nest

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.LinearLayoutManager

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

    override fun canScrollVertically(): Boolean {
        if (callback != null) {
            val child = callback!!()
            return child == null || child.isScrollTop
        }
        return super.canScrollVertically()
    }
}