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

        val scrollingOffset: Int
        val absDy = abs(dy)
        var consume = 0
        val direction = if (dy > 0) 1 else -1
        if (dy > 0) {
            val start = orientationHelper.startAfterPadding
            for (i in 0 until childCount) {
                val child = getChildAt(i)
//                println("index $i end ${orientationHelper.getDecoratedEnd(child)}")
                if (orientationHelper.getDecoratedEnd(child) <= start
                    || orientationHelper.getTransformedEndWithDecoration(child) <= start) {
                    removeAndRecycleViewAt(i, recycler)
                } else {
                    break
                }
            }
            val child = getChildAt(childCount - 1)
            var top = orientationHelper.endAfterPadding
            var position = Int.MAX_VALUE - 1
            if (child != null) {
                position = getPosition(child)
                top = orientationHelper.getDecoratedEnd(child)
            }
            position ++
            scrollingOffset = top - orientationHelper.endAfterPadding
            var remainSpace = absDy - scrollingOffset

            while (position < state.itemCount && remainSpace > 0) {
                val view = recycler.getViewForPosition(position)
                addView(view)
                measureChildWithMargins(view, 0, 0)
                val measurement = orientationHelper.getDecoratedMeasurement(view)
                val bottom = top + measurement
                layoutDecoratedWithMargins(view, paddingLeft, top,
                    orientationHelper.getDecoratedMeasurementInOther(child), bottom)
//                println("position $position, measurement $measurement")
                position ++
                top = bottom
                consume += measurement
                remainSpace -= measurement
            }
        } else {
            val end = orientationHelper.endAfterPadding
            for (i in childCount - 1 downTo 0) {
                val child = getChildAt(i)
                if (orientationHelper.getDecoratedStart(child) >= end
                    || orientationHelper.getTransformedStartWithDecoration(child) >= end) {
                    removeAndRecycleViewAt(i, recycler)
                } else {
                    break
                }
            }
            val child = getChildAt(0)
            var bottom = orientationHelper.startAfterPadding
            var position = 0
            if (child != null) {
                position = getPosition(child)
                bottom = orientationHelper.getDecoratedStart(child)
            }

            position --
            scrollingOffset = - bottom + orientationHelper.startAfterPadding
            var remainSpace = absDy - scrollingOffset

            while (position >= 0 && remainSpace > 0) {
                val view = recycler.getViewForPosition(position)
                addView(view, 0)
                measureChildWithMargins(view, 0, 0)

                val measurement = orientationHelper.getDecoratedMeasurement(view)
                val top = bottom - measurement
                layoutDecoratedWithMargins(view, paddingLeft, top,
                    orientationHelper.getDecoratedMeasurementInOther(child), bottom)
                position --
                bottom = top
                consume += measurement
                remainSpace -= measurement
            }
        }

        val delta = scrollingOffset + consume
        println("consume = $consume, scrollingOffset = $scrollingOffset")
        if (delta <= 0) {
            return 0
        }

        val scrolled = if (absDy > delta) delta * direction else dy
        orientationHelper.offsetChildren(-scrolled)
        println("childCount $childCount")
        println("dy = $dy, scrolled = $scrolled, delta = $delta")
        return scrolled
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