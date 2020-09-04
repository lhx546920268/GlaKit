package com.lhx.glakitDemo

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import androidx.recyclerview.widget.RecyclerView.SmoothScroller
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.viewholder.RecyclerViewHolder
import kotlinx.android.synthetic.main.layout_item.view.*
import kotlinx.android.synthetic.main.main_activity.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    lateinit var helper: MySnapHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        titleBar.setTitle("这是一个标题")
        titleBar.setLeftItem("返回", null)
        titleBar.setRightItem("完成", null)


        recyclerView.layoutManager = CenterLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        recyclerView.adapter = Adapter(WeakReference(recyclerView))

//         helper = MySnapHelper()
//        helper.attachToRecyclerView(recyclerView)
    }

    private inner class Adapter(recyclerViewReference: WeakReference<RecyclerView>): RecyclerViewAdapter(
        recyclerViewReference
    ) {

        var selectedPosition: Int = 0
        set(value) {
            if(field != value){
                field = value
                notifyDataSetChanged()
                Handler().postDelayed({
                    recyclerView?.smoothScrollToPosition(selectedPosition)
                }, 100)
            }
        }

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return RecyclerViewHolder(
                LayoutInflater.from(context!!).inflate(
                    R.layout.layout_item,
                    parent,
                    false
                )
            )
        }

        override fun numberOfItems(section: Int): Int {
            return 20
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.itemView.textView.text = "index $position"
            viewHolder.itemView.textView.setTextColor(if (position == selectedPosition) Color.RED else Color.BLACK)
        }

        override fun onItemClick(positionInSection: Int, section: Int, item: View) {
            selectedPosition = positionInSection
        }
    }

    class CenterLayoutManager : LinearLayoutManager {
        constructor(context: Context?) : super(context) {}
        constructor(context: Context?, orientation: Int, reverseLayout: Boolean) : super(
            context,
            orientation,
            reverseLayout
        ) {
        }

        constructor(
            context: Context?,
            attrs: AttributeSet?,
            defStyleAttr: Int,
            defStyleRes: Int
        ) : super(context, attrs, defStyleAttr, defStyleRes) {
        }

        override fun smoothScrollToPosition(
            recyclerView: RecyclerView,
            state: RecyclerView.State,
            position: Int
        ) {
            val smoothScroller: SmoothScroller = CenterSmoothScroller(recyclerView.context)
            smoothScroller.targetPosition = position
            startSmoothScroll(smoothScroller)
        }

        private class CenterSmoothScroller internal constructor(context: Context?) :
            LinearSmoothScroller(context) {
            override fun calculateDtToFit(
                viewStart: Int,
                viewEnd: Int,
                boxStart: Int,
                boxEnd: Int,
                snapPreference: Int
            ): Int {
                Log.d("calculateDtToFit", "viewStart=$viewStart; viewEnd=$viewEnd; boxStart=$boxStart; boxEnd=$boxEnd; snapPreference=$snapPreference;")
                return boxStart + (boxEnd - boxStart) / 2 - (viewStart + (viewEnd - viewStart) / 2)
            }
        }
    }

    class MySnapHelper : LinearSnapHelper() {
        private var mVerticalHelper: OrientationHelper? = null
        private var mHorizontalHelper: OrientationHelper? = null
        private var mRecyclerView: RecyclerView? = null
        override fun calculateDistanceToFinalSnap(
            layoutManager: RecyclerView.LayoutManager,
            targetView: View
        ): IntArray? {
            val out = IntArray(2)
            if (layoutManager.canScrollHorizontally()) {
                out[0] = distanceToCenter(targetView, getHorizontalHelper(layoutManager))
            } else {
                out[0] = 0
            }
            if (layoutManager.canScrollVertically()) {
                out[1] = distanceToCenter(targetView, getVerticalHelper(layoutManager))
            } else {
                out[1] = 0
            }
            return out
        }

        @Throws(IllegalStateException::class)
        override fun attachToRecyclerView(recyclerView: RecyclerView?) {
            this.mRecyclerView = recyclerView
            super.attachToRecyclerView(recyclerView)
        }

        private fun distanceToCenter(targetView: View, helper: OrientationHelper?): Int {
            //如果已经滚动到尽头 并且判断是否是第一个item或者是最后一个，直接返回0，不用多余的滚动了
            if (helper!!.getDecoratedStart(targetView) == 0 && mRecyclerView!!.getChildAdapterPosition(
                    targetView
                ) == 0
                || (helper.getDecoratedEnd(targetView) == helper.endAfterPadding
                        && mRecyclerView!!.getChildAdapterPosition(targetView) == mRecyclerView!!.adapter!!.itemCount - 1)
            ) return 0
            val viewCenter =
                helper.getDecoratedStart(targetView) + (helper.getDecoratedEnd(targetView) - helper.getDecoratedStart(
                    targetView
                )) / 2
            val correctCenter = (helper.endAfterPadding - helper.startAfterPadding) / 2
            return viewCenter - correctCenter
        }

        private fun getVerticalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
            if (mVerticalHelper == null) {
                mVerticalHelper = OrientationHelper.createVerticalHelper(layoutManager)
            }
            return mVerticalHelper
        }

        private fun getHorizontalHelper(layoutManager: RecyclerView.LayoutManager): OrientationHelper? {
            if (mHorizontalHelper == null) {
                mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager)
            }
            return mHorizontalHelper
        }
    }
}