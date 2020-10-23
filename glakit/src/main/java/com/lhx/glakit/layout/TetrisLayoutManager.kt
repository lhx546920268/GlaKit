package com.lhx.glakit.layout

import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView

/**
 * 网格布局，尽量填充
 */
class TetrisLayoutManager: RecyclerView.LayoutManager() {

    var orientationHelper: OrientationHelper = OrientationHelper.createOrientationHelper(this, RecyclerView.VERTICAL)

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun canScrollHorizontally(): Boolean {
        return false
    }

    override fun canScrollVertically(): Boolean {
        return true
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {
        println("childCount ${state.itemCount}")
        for (index in 0 until state.itemCount) {
            val child = recycler.getViewForPosition(index)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            layoutDecoratedWithMargins(child, 0, 0,
                orientationHelper.getDecoratedMeasurement(child),
                orientationHelper.getDecoratedMeasurementInOther(child))
        }
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {

    }
}