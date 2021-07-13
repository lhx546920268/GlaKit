package com.lhx.glakitDemo.home

import android.Manifest
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.lhx.glakit.adapter.ItemType
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.base.fragment.RecyclerFragment
import com.lhx.glakit.base.interf.PermissionRequester
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.helper.PermissionHelper
import com.lhx.glakit.layout.TetrisLayoutManager
import com.lhx.glakit.section.SectionInfo
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.utils.ToastUtils
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakit.web.WebConfig
import com.lhx.glakit.web.WebFragment
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.dialog.DialogFragment
import com.lhx.glakitDemo.drawable.CornerDrawableFragment
import com.lhx.glakitDemo.image.ImageScaleFragment
import com.lhx.glakitDemo.section.SectionListFragment
import com.lhx.glakitDemo.section.SectionRecycleViewFragment

class MyLayout: LinearLayoutManager {
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

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        println("onLayoutChildren before $childCount")
        super.onLayoutChildren(recycler, state)

        Log.d("onLayoutChildren", "$childCount")
    }

    override fun getFocusedChild(): View? {
        val view = super.getFocusedChild()
        println("getFocusedChild $view")
        return view
    }
//
//    override fun measureChildWithMargins(child: View, widthUsed: Int, heightUsed: Int) {
//        println("measureChildWithMargins")
//        super.measureChildWithMargins(child, widthUsed, heightUsed)
//    }
//
//    override fun addView(child: View?, index: Int) {
//        println("addView $index")
//        super.addView(child, index)
//    }
//
//    override fun removeViewAt(index: Int) {
//        println("removeViewAt $index")
//        super.removeViewAt(index)
//    }
//
//    override fun removeView(child: View?) {
//        println("removeView $child")
//        super.removeView(child)
//    }
//
//    override fun removeAllViews() {
//        println("removeAllViews")
//        super.removeAllViews()
//    }
//
//    override fun onItemsRemoved(recyclerView: RecyclerView, positionStart: Int, itemCount: Int) {
//        println("onItemsRemoved")
//        super.onItemsRemoved(recyclerView, positionStart, itemCount)
//    }
//
//    override fun removeAndRecycleAllViews(recycler: RecyclerView.Recycler) {
//        println("removeAndRecycleAllViews")
//        super.removeAndRecycleAllViews(recycler)
//    }
//
//    override fun removeAndRecycleViewAt(index: Int, recycler: RecyclerView.Recycler) {
//        println("removeAndRecycleViewAt $index")
//        super.removeAndRecycleViewAt(index, recycler)
//    }
//
//    override fun removeAndRecycleView(child: View, recycler: RecyclerView.Recycler) {
//        println("removeAndRecycleViewAt $child")
//        super.removeAndRecycleView(child, recycler)
//    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler,
        state: RecyclerView.State
    ): Int {
        val result = super.scrollVerticallyBy(dy, recycler, state)
//        Log.d("scrollVerticallyBy", "$childCount, result = $result, dy = $dy, ${getChildAt(0)?.top}")
        return result
    }

    override fun onSaveInstanceState(): Parcelable? {
        println("onSaveInstanceState")
        return super.onSaveInstanceState()
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        println("onRestoreInstanceState")
        super.onRestoreInstanceState(state)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
    }


//    override fun calculateExtraLayoutSpace(state: RecyclerView.State, extraLayoutSpace: IntArray) {
//        super.calculateExtraLayoutSpace(state, extraLayoutSpace)
//        println("calculateExtraLayoutSpace start ${extraLayoutSpace[0]}, end ${extraLayoutSpace[1]}")
//    }
}

class Grid: GridLayoutManager {

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(context: Context?, spanCount: Int) : super(context, spanCount)
    constructor(
        context: Context?,
        spanCount: Int,
        orientation: Int,
        reverseLayout: Boolean
    ) : super(context, spanCount, orientation, reverseLayout)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollVerticallyBy(dy, recycler, state)
    }
}

class Stage: StaggeredGridLayoutManager {

    constructor(
        context: Context?,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    constructor(spanCount: Int, orientation: Int) : super(spanCount, orientation)

    override fun onLayoutChildren(recycler: RecyclerView.Recycler?, state: RecyclerView.State?) {
        super.onLayoutChildren(recycler, state)
    }

    override fun scrollVerticallyBy(
        dy: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        return super.scrollVerticallyBy(dy, recycler, state)
    }
}

class HomeFragment: RecyclerFragment(), PermissionRequester, StickAdapter {

    override lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    val items = arrayOf("Drawable", "RecyclerView", "ListView", "Dialog", "Image", "Web", "Permission")
    private val adapter: Adapter by lazy { Adapter(recyclerView) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)


        setBarTitle("首页")
        recyclerView.layoutManager = MyLayout(requireContext()) //TetrisLayoutManager()
        recyclerView.adapter = adapter
        recyclerView.stickAdapter = this
    }

    override fun shouldStickAtPosition(position: Int): Boolean {
        return adapter.lastSectionInfo<SectionInfo>()!!.sectionBegin == position
    }

    override fun getCurrentStickPosition(firstVisibleItem: Int, stickPosition: Int): Int {
        return adapter.lastSectionInfo<SectionInfo>()!!.sectionBegin
    }

    override fun onViewStickChange(stick: Boolean, view: View, position: Int) {
        println("stick = $stick, position = $position")
        view.setBackgroundColor(if (stick) Color.RED else Color.WHITE)
    }

    private inner class Adapter(recyclerView: RecyclerView): RecyclerViewAdapter(
        recyclerView
    ), ToastContainer {

        var count = 10
        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            if (viewType == ItemType.HEADER.ordinal) {
                return RecyclerViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.section_header,
                        parent,
                        false
                    )
                )
            } else {
                return RecyclerViewHolder(
                    LayoutInflater.from(context).inflate(
                        R.layout.layout_item,
                        parent,
                        false
                    )
                )
            }
        }

        override fun numberOfSections(): Int {
            return count
        }

        override fun numberOfItems(section: Int): Int {
            return items.size * 3
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.textView).text = items[position % items.size]
        }

        override fun onBindSectionHeaderViewHolder(viewHolder: RecyclerViewHolder, section: Int) {
            viewHolder.getView<TextView>(R.id.title).text = "Header $section"
            viewHolder.itemView.setBackgroundColor(Color.WHITE)
        }

        override fun onItemClick(positionInSection: Int, section: Int, item: View) {
//            count = if (count == 10) 5 else 10
//            notifyDataSetChanged()
            when(positionInSection % items.size) {
                0 -> {
                    startActivityForResult(CornerDrawableFragment::class.java) {
                        Log.d("fragment", "back callback")
                    }
                }
                1-> {
                    startActivity(SectionRecycleViewFragment::class.java)
                }
                2-> {
                    startActivity(SectionListFragment::class.java)
                }
                3-> {
                    startActivity(DialogFragment::class.java)
                }
                4-> {
                    startActivity(ImageScaleFragment::class.java)
                }
                5-> {
                    val bundle = Bundle()
                    bundle.putString(WebConfig.URL, "https://www.baidu.com")
                    startActivity(WebFragment::class.java, bundle)
                }
                6 -> {
                   PermissionHelper.requestPermissionsIfNeeded(
                       this@HomeFragment,
                       arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
                   ) {
                       if (it) {
                           ToastUtils.show("授权成功")
                       } else {
                           ToastUtils.show("授权拒绝")
                       }
                   }
                }
            }
        }

        override fun shouldExistSectionHeader(section: Int): Boolean {
            return true
        }

        override val toastContainer: View
            get() = recyclerView
    }
}