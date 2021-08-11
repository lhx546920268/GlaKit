package com.lhx.glakitDemo.home

import android.Manifest
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.os.Parcel
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
import com.lhx.glakit.permission.PermissionRequester
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.helper.PermissionHelper
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
import java.util.*

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

open class Parent: Parcelable {
    var age = 0
    constructor()

    constructor(parcel: Parcel) : this() {
        age = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(age)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Parent> {
        override fun createFromParcel(parcel: Parcel): Parent {
            return Parent(parcel)
        }

        override fun newArray(size: Int): Array<Parent?> {
            return arrayOfNulls(size)
        }
    }

    fun copy(input: Parcelable): Parcelable {
        var parcel: Parcel?= null
        try {
            parcel = Parcel.obtain()
            parcel.writeParcelable(input,0)
            parcel.setDataPosition(0)
            return parcel.readParcelable(input.javaClass.classLoader)!!
        }finally {
            parcel?.recycle()
        }
    }
}

class Child: Parent, Parcelable {

    var name: String? = null
    constructor()

    constructor(parcel: Parcel) : super(parcel) {
        name = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        super.writeToParcel(parcel, flags)
        parcel.writeString(name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Child> {
        override fun createFromParcel(parcel: Parcel): Child {
            return Child(parcel)
        }

        override fun newArray(size: Int): Array<Child?> {
            return arrayOfNulls(size)
        }
    }
}

abstract class ParcelParent {

    var id = ""
    var expand = false
    var title = ""
}

class ParcelTest: ParcelParent, Parcelable {


    var child2: Child? = null
    var child1: Child? = null
    constructor()

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()!!
        expand = parcel.readInt() == 1
        title = parcel.readString()!!
        child2 = parcel.readParcelable(Child::class.java.classLoader)
        child1 = parcel.readParcelable(Child::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeInt(if (expand) 1 else 0)
        parcel.writeString(title)
        parcel.writeParcelable(child2, flags)
        parcel.writeParcelable(child1, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ParcelTest> {
        override fun createFromParcel(parcel: Parcel): ParcelTest {
            return ParcelTest(parcel)
        }

        override fun newArray(size: Int): Array<ParcelTest?> {
            return arrayOfNulls(size)
        }
    }

    fun copy(input: Parcelable): Parcelable {
        var parcel: Parcel?= null
        try {
            parcel = Parcel.obtain()
            parcel.writeParcelable(input,0)
            parcel.setDataPosition(0)
            return parcel.readParcelable(input.javaClass.classLoader)!!
        }finally {
            parcel?.recycle()
        }
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

        val parent = Parent()
        parent.age = 18
        val copyParent = parent.copy(parent)

        println("parent $parent, $copyParent")

        val child = Child()
        child.age = 15
        child.name = "wifi"
        val copyChild = child.copy(child) as Child

        println("child $child, ${copyChild.age}, ${copyChild.name}")

        val test = ParcelTest()
        test.child2 = child
        test.child1 = copyChild
        val copyTest =  test.copy(test) as ParcelTest
        println("test ${copyTest.child2?.name}")

        setBarTitle("首页")
        recyclerView.layoutManager = MyLayout(requireContext()) //TetrisLayoutManager()
        recyclerView.addItemDecoration(Decoration())
        recyclerView.adapter = adapter
//        recyclerView.stickAdapter = this
    }

    override fun shouldStickAtPosition(position: Int): Boolean {
        return adapter.lastSectionInfo<SectionInfo>()!!.sectionBegin == position
    }

    override fun getCurrentStickPosition(firstVisibleItem: Int, stickPosition: Int): Int {
        return adapter.lastSectionInfo<SectionInfo>()!!.sectionBegin
    }

    override fun onViewStickChange(stick: Boolean, view: View, position: Int) {
        view.setBackgroundColor(if (stick) Color.RED else Color.WHITE)
    }

    private inner class Decoration: RecyclerView.ItemDecoration() {

        val drawable = CornerBorderDrawable()
        init {
            drawable.backgroundColor = Color.WHITE
        }

        override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {

            var left = 0
            var top = 0
            var right = 0
            var bottom = 0

            var isTop = false
            var isBottom = false

            val count = parent.childCount
            for (i in 0 until count) {
                val child = parent.getChildAt(i)
                val position = parent.getChildLayoutPosition(child)
                val info = adapter.sectionInfoForPosition<SectionInfo>(position)!!
                if (info.section == 1) {
                    if (info.isHeaderForPosition(position)) {
                        isTop = true
                    } else if (info.getItemStartPosition() + info.numberItems - 1 == position) {
                        isBottom = true
                    }
                    if (bottom == 0) {
                        top = child.top
                        left = child.left
                        right = child.right
                    }
                    bottom = child.bottom
                }
            }
            if (bottom - top > 0) {
                val radius = pxFromDip(10f)
                drawable.leftTopCornerRadius = if (isTop) radius else 0
                drawable.rightTopCornerRadius = if (isTop) radius else 0
                drawable.leftBottomCornerRadius = if (isBottom) radius else 0
                drawable.rightBottomCornerRadius = if (isBottom) radius else 0

                drawable.setBounds(left, top, right, bottom)
                drawable.draw(c)
            }
        }
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
//            viewHolder.itemView.setBackgroundColor(Color.WHITE)
        }

        override fun onItemClick(position: Int, section: Int, item: View) {
//            count = if (count == 10) 5 else 10
//            notifyDataSetChanged()
            when(position % items.size) {
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
                           ToastUtils.showToast("授权成功")
                       } else {
                           ToastUtils.showToast("授权拒绝")
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