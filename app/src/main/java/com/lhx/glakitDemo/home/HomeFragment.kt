package com.lhx.glakitDemo.home

import android.Manifest
import android.graphics.Canvas
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.android.arouter.launcher.ARouter
import com.lhx.glakit.adapter.ItemType
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.base.fragment.RecyclerFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.permission.PermissionHelper
import com.lhx.glakit.permission.PermissionRequester
import com.lhx.glakit.section.SectionInfo
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.utils.ToastUtils
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakit.web.WebConfig
import com.lhx.glakit.web.BaseWebFragment
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.dialog.DialogFragment
import com.lhx.glakitDemo.drawable.CornerDrawableFragment
import com.lhx.glakitDemo.image.ImageScaleFragment
import com.lhx.glakitDemo.pager.PagerFragment
import com.lhx.glakitDemo.scan.CameraXFragment
import com.lhx.glakitDemo.scan.QRCodeScanFragment
import com.lhx.glakitDemo.section.SectionListFragment
import com.lhx.glakitDemo.section.SectionRecycleViewFragment

class HomeFragment: RecyclerFragment(), PermissionRequester, StickAdapter {

    override lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    val items = arrayListOf("Drawable", "RecyclerView", "ListView", "Dialog", "Image", "Web", "Permission", "NestedScroll", "Pager", "Scan", "CameraX")
    private val adapter: Adapter by lazy { Adapter(recyclerView) }

    override fun onCreate(savedInstanceState: Bundle?) {
        createPermissionLauncher()
        super.onCreate(savedInstanceState)
    }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)

        println("首页 $this")
        setBarTitle("首页")
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.addItemDecoration(Decoration())
        recyclerView.adapter = adapter
        recyclerView.stickAdapter = this
    }

    override fun shouldStickAtPosition(position: Int): Boolean {
        return items.size + 1 == position
    }

    override fun getCurrentStickPosition(firstVisibleItem: Int, stickPosition: Int): Int {
        return items.size + 1
    }

    override fun onViewStickChange(stick: Boolean, view: View, position: Int) {
        view.setBackgroundColor(if (stick) Color.RED else Color.WHITE)
    }

    override fun getStickOffset(): Int {
        return SizeUtils.pxFormDip(40f, requireContext())
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
            return items.size
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
                    startActivity(BaseWebFragment::class.java, bundle)
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
                7 -> {
                    ARouter.getInstance().build("/app/nested").navigation()
                }
                8 -> {
                    startActivity(PagerFragment::class.java)
                }
                9 -> {
                    startActivity(QRCodeScanFragment::class.java)
                }
                10 -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        startActivity(CameraXFragment::class.java)
                    }
                }
            }
        }

        override fun shouldExistSectionHeader(section: Int): Boolean {
            return true
        }

        override fun onHeaderClick(section: Int, header: View) {
            println("click header")
        }

        override val toastContainer: View
            get() = recyclerView
    }
}