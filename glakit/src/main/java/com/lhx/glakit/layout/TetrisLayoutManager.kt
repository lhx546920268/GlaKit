package com.lhx.glakit.layout

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.OrientationHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.lhx.glakit.adapter.ItemType
import com.lhx.glakit.section.EdgeInsets
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 网格布局，尽量填充
 */
class TetrisLayoutManager: RecyclerView.LayoutManager() {

    var orientationHelper: OrientationHelper = OrientationHelper.createOrientationHelper(this, RecyclerView.VERTICAL)

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

        var position = 0
        val view = findReferenceChild(state)
        var top = orientationHelper.startAfterPadding
        if (view != null) {
            position = getPosition(view)
            top = orientationHelper.getDecoratedStart(view)
        }

        detachAndScrapAttachedViews(recycler)
        println("itemCount ${state.itemCount}")
        val totalSpace = orientationHelper.totalSpace
        for (index in position until state.itemCount) {
            val child = recycler.getViewForPosition(index)
            addView(child)
            top += layoutChild(child)
            if (top > totalSpace){
                break
            }
        }
        println("childCount $childCount")
        if (!state.isPreLayout) {
            orientationHelper.onLayoutComplete()
        }
    }

    // overridden by GridLayoutManager
    fun findReferenceChild(state: RecyclerView.State): View? {

        // Determine which direction through the view children we are going iterate.
        val start = 0
        val end = childCount
        val diff = 1

        val itemCount = state.itemCount
        val boundsStart = orientationHelper.startAfterPadding
        val boundsEnd = orientationHelper.endAfterPadding
        var invalidMatch: View? = null
        var bestFirstFind: View? = null
        var bestSecondFind: View? = null
        var i = start
        while (i != end) {
            val view = getChildAt(i)
            val position = getPosition(view!!)
            val childStart = orientationHelper.getDecoratedStart(view)
            val childEnd = orientationHelper.getDecoratedEnd(view)
            if (position in 0 until itemCount) {
                if ((view.layoutParams as RecyclerView.LayoutParams).isItemRemoved) {
                    if (invalidMatch == null) {
                        invalidMatch = view // removed item, least preferred
                    }
                } else {
                    // b/148869110: usually if childStart >= boundsEnd the child is out of
                    // bounds, except if the child is 0 pixels!
                    val outOfBoundsBefore = childEnd <= boundsStart && childStart < boundsStart
                    val outOfBoundsAfter = childStart >= boundsEnd && childEnd > boundsEnd
                    if (outOfBoundsBefore || outOfBoundsAfter) {
                        // The item is out of bounds.
                        // We want to find the items closest to the in bounds items and because we
                        // are always going through the items linearly, the 2 items we want are the
                        // last out of bounds item on the side we start searching on, and the first
                        // out of bounds item on the side we are ending on.  The side that we are
                        // ending on ultimately takes priority because we want items later in the
                        // layout to move forward if no in bounds anchors are found.
                        if (outOfBoundsBefore) {
                            bestFirstFind = view
                        } else if (bestSecondFind == null) {
                            bestSecondFind = view
                        }
                    } else {
                        // We found an in bounds item, greedily return it.
                        return view
                    }
                }
            }
            i += diff
        }
        // We didn't find an in bounds item so we will settle for an item in this order:
        // 1. bestSecondFind
        // 2. bestFirstFind
        // 3. invalidMatch
        return bestSecondFind ?: (bestFirstFind ?: invalidMatch)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return LayoutParams(RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT)
    }

    override fun generateLayoutParams(lp: ViewGroup.LayoutParams?): RecyclerView.LayoutParams {
        return LayoutParams(lp)
    }

    override fun generateLayoutParams(
        c: Context,
        attrs: AttributeSet?
    ): RecyclerView.LayoutParams {
        return LayoutParams(c, attrs)
    }

    class LayoutParams: RecyclerView.LayoutParams {

        constructor(c: Context, attrs: AttributeSet?) : super(c, attrs)
        constructor(width: Int, height: Int) : super(width, height)
        constructor(source: ViewGroup.MarginLayoutParams?) : super(source)
        constructor(source: ViewGroup.LayoutParams?) : super(source)
        constructor(source: RecyclerView.LayoutParams?) : super(source)

        var type = ItemType.VIEW
        var section = NO_POSITION
        var item = NO_POSITION
        var inset = EdgeInsets.zero()
        var mainSpacing = 0
        var crossSpacing = 0
    }

    val crossTotalSpace: Int
        get() = width - paddingLeft - paddingRight

    val crossStart: Int
        get() = paddingLeft

    fun layoutChild(child: View): Int {

        val mainMost = layoutHelper.mainMost
        measureChildWithMargins(child, 0, 0)
        val width = orientationHelper.getDecoratedMeasurement(child)
        val height = orientationHelper.getDecoratedMeasurementInOther(child)
        val params = child.layoutParams as LayoutParams
        var left = layoutHelper.crossMost
        var top = layoutHelper.offset
        orientationHelper.totalSpace

        fun newline() {
            resetLayoutHelper(params)
            left = layoutHelper.crossMost
        }

        if (params.section != layoutHelper.section) {
            newline()
        }

        if (width + params.crossSpacing + params.inset.right + layoutHelper.crossMost >= crossTotalSpace) {
            if (layoutHelper.outMostRects.size < 2) {
                //这一行已经没有位置可以放item了
                newline()
            } else {
                val i = layoutHelper.getSuitableRect(width)
                if (i == null) {
                    newline()
                } else {
                    val rect = layoutHelper.outMostRects[i]
                    left = rect.left
                    top = rect.top + rect.bottom + params.mainSpacing
                    val newRect = Rect(left, top, left + width,
                        top + height)
                    if (width < rect.width()) {
                        //只挡住上面的item的一部分
                            rect.left = left + width + params.crossSpacing
                        layoutHelper.outMostRects.add(i, newRect)
                    } else {
                        //已完全挡住上一个item
                        layoutHelper.outMostRects[i] = newRect
                    }
                    layoutHelper.updateMostRect(newRect)
                    layoutHelper.combineIfNeeded(i)
                }
            }
        } else {
            //右边还有位置可以放item
            left = layoutHelper.crossMost + params.crossSpacing
            layoutHelper.crossMost = left + width
            val rect = Rect(left, top, left + width, top + height)
            if (layoutHelper.outMostRects.size == 0) {
                layoutHelper.outMostRects.add(rect)
            } else {
                val last = layoutHelper.outMostRects.last()
                //相邻的item等高，合并
                if (rect.bottom == last.bottom) {
                    last.right = rect.right
                } else {
                    layoutHelper.outMostRects.add(rect)
                }
            }

            layoutHelper.updateMostRect(rect)
        }

        child.layout(left, top, left + width, top + height)
        return layoutHelper.mainMost - mainMost
    }

    fun resetLayoutHelper(params: LayoutParams) {
        layoutHelper.section = params.section
        layoutHelper.crossMost = crossStart + params.inset.left
        layoutHelper.offset = params.inset.top
        layoutHelper.outMostRects.clear()
        if (params.section == 0) {
            layoutHelper.offset += orientationHelper.startAfterPadding
        } else {
            layoutHelper.offset += layoutHelper.mainMost
        }
    }

    private val layoutHelper = LayoutHelper()

    class LayoutHelper {

        var section = NO_POSITION
        var crossMost = 0
        var offset = 0
        var mainMost = 0
        var outMostRects = ArrayList<Rect>()

        val point = Point()

        //
        fun getSuitableRect(width: Int): Int? {
            var rect = outMostRects[0]
            var result = 0
            for (i in 1 until outMostRects.size) {
                val tmp = outMostRects[i]
                if (tmp.bottom <= rect.bottom && rect.width() >= width) {
                    if (tmp.bottom == rect.bottom) {
                        if (tmp.left < rect.left) {
                            rect = tmp
                            result = i
                        }
                    } else {
                        rect = tmp
                        result = i
                    }
                }
            }
            return if (width > rect.width()) {
                null
            } else {
                result
            }
        }

        //更新最高的frame
        fun updateMostRect(rect: Rect) {
            if (rect.bottom > mainMost) {
                mainMost = rect.bottom
            }
        }

        //合并相邻的相同高度的item
        fun combineIfNeeded(position: Int) {
            var rect = outMostRects[position]
            if (position > 0) {
                //前一个
                val pRect = outMostRects[position - 1]
                if (abs(pRect.bottom - rect.bottom) < 1) {
                    pRect.left = min(rect.left, pRect.left)
                    pRect.right = max(rect.right, pRect.right)

                    outMostRects.removeAt(position)
                    rect = pRect
                }
            }

            if (position + 1 < outMostRects.size) {
                //后一个
                val pRect = outMostRects[position + 1]
                if (abs(pRect.bottom - rect.bottom) < 1) {
                    pRect.left = min(rect.left, pRect.left)
                    pRect.right = max(rect.right, pRect.right)

                    outMostRects.removeAt(position)
                }
            }
        }
    }


}