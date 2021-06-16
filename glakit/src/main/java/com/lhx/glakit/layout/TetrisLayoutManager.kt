package com.lhx.glakit.layout

import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.abs

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

    override fun isAutoMeasureEnabled(): Boolean {
        return true
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        if (childCount == 0 || dy == 0) {
            return 0
        }

        val absDy = abs(dy)
        var consume = 0
        val direction = if (dy > 0) -1 else 1
        if (dy > 0) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (orientationHelper.getDecoratedEnd(child) <= 0
                    || orientationHelper.getTransformedEndWithDecoration(child) <= 0) {
                    removeAndRecycleViewAt(i, recycler)
                    break
                }
            }
            val child = getChildAt(childCount - 1)
            var top = 0
            var position = 0
            if (child != null) {
                position = getPosition(child)
                top = orientationHelper.getDecoratedEnd(child)
            }
            position ++
            val totalSpace = orientationHelper.totalSpace
            while (position < state.itemCount && top < totalSpace) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val bottom = top + orientationHelper.getDecoratedMeasurement(view)
                layoutDecoratedWithMargins(view, paddingLeft, top,
                    orientationHelper.getDecoratedMeasurementInOther(child), bottom)
                position ++
                top = bottom
            }
        } else {
            for (i in 0 until childCount) {
                val end = orientationHelper.endAfterPadding
                val child = getChildAt(i)
                if (orientationHelper.getDecoratedStart(child) >= end
                    || orientationHelper.getTransformedStartWithDecoration(child) >= 0) {
                    removeAndRecycleViewAt(i, recycler)
                    break
                }
            }
            val child = getChildAt(0)
            var bottom = 0
            var position = 0
            if (child != null) {
                position = getPosition(child)
                bottom = orientationHelper.getDecoratedStart(child)
            }
            position --
            while (position >= 0 && bottom > 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val top = bottom - orientationHelper.getDecoratedMeasurement(view)
                layoutDecoratedWithMargins(view, paddingLeft, top,
                    orientationHelper.getDecoratedMeasurementInOther(child), bottom)
                position ++
                bottom = top
            }
        }

        orientationHelper.offsetChildren(absDy * direction)
        return absDy
    }

    override fun onLayoutChildren(recycler: RecyclerView.Recycler, state: RecyclerView.State) {

        detachAndScrapAttachedViews(recycler)
        println("itemCount ${state.itemCount}")
        val totalSpace = orientationHelper.totalSpace
        var top = orientationHelper.startAfterPadding
        for (index in 0 until state.itemCount) {
            val child = recycler.getViewForPosition(index)
            addView(child)
            measureChildWithMargins(child, 0, 0)
            val bottom = top + orientationHelper.getDecoratedMeasurement(child)
            println("bottom ${child.measuredHeight}")
            layoutDecoratedWithMargins(child, paddingLeft, top,
                orientationHelper.getDecoratedMeasurementInOther(child), bottom)
            top = bottom
            if (top > totalSpace){
                break
            }
        }
        println("childCount $childCount")
    }


    override fun onLayoutCompleted(state: RecyclerView.State?) {

    }
}