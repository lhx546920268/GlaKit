package com.lhx.glakitDemo.nest

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.lhx.glakit.adapter.ItemType
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.activity.RecyclerActivity
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.extension.gone
import com.lhx.glakit.extension.isVisible
import com.lhx.glakit.extension.visible
import com.lhx.glakit.nested.NestedParentLinearLayoutManager
import com.lhx.glakit.nested.NestedParentRecyclerView
import com.lhx.glakit.nested.NestedScrollHelper
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

//嵌套滑动
class NestedScrollActivity: RecyclerActivity() {

    override val hasRefresh: Boolean
        get() = true

    val parentRecyclerView: NestedParentRecyclerView
        get() = recyclerView as NestedParentRecyclerView

    val magicIndicator: MagicIndicator by lazy { findViewById(R.id.magic_indicator) }

    val topContainer: LinearLayout by lazy { findViewById(R.id.top_container) }

    val industryRole: View by lazy { findViewById(R.id.industry_role) }
    val maxOffset by lazy { -pxFromDip(40f) }
    val indicatorHeight by lazy { pxFromDip(40f) }
    var totalOffset = 0

    var viewPager: ViewPager? = null

    private val helper by lazy {
        NestedScrollHelper(parentRecyclerView) {
        childContainer?.getChildRecyclerView()
    } }

    val titles = arrayOf("水果生鲜", "休闲零食", "男装女装", "日用百货", "母婴用品")

    val onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            totalOffset -= dy
            if (totalOffset > 0) totalOffset = 0
            if (totalOffset < maxOffset) totalOffset = maxOffset

            topContainer.translationY = totalOffset.toFloat()
            val count = parentRecyclerView.childCount
            if (count > 0) {
                val last = parentRecyclerView.getChildAt(count - 1)
                if (last.top <= totalOffset - maxOffset + indicatorHeight
                    && parentRecyclerView.getChildAdapterPosition(last!!) == parentRecyclerView.adapter!!.itemCount - 1) {
                    magicIndicator.visible()
                } else {
                    magicIndicator.gone()
                }
                val container = childContainer
                if (container != null) {
                    var offset = 0
                    if (magicIndicator.isVisible()) {
                        offset = totalOffset - maxOffset - (last.top - indicatorHeight)
                    }
                    container.currentFragment.offset = offset.toFloat()
                }
            } else {
                magicIndicator.gone()
            }
        }
    }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)
        setBarTitle("嵌套滑动")
        val layoutManager = NestedParentLinearLayoutManager(this)
        layoutManager.nestedScrollHelper = helper

        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(recyclerView)

        //到底部就 不让下拉刷新了，不然滑动 indicator 的时候会触发
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val parent = recyclerView as NestedParentRecyclerView
                    smartRefreshLayout?.setEnableRefresh(!parent.isScrollEnd)
                }
            }
        })

        recyclerView.addOnScrollListener(onScrollListener)

        val commonNavigator = CommonNavigator(this)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return titles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.GRAY
                colorTransitionPagerTitleView.selectedColor = Color.BLACK
                colorTransitionPagerTitleView.text = titles[index]
                colorTransitionPagerTitleView.setOnClickListener { viewPager?.currentItem = index }
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
    }

    override fun onRefresh() {
        baseContainer?.postDelayed({
            stopRefresh()
        }, 1000)
    }

    private var childContainer: NestedScrollChildContainer? = null

    override fun getRefreshableContentRes(): Int {
        return R.layout.nested_scroll_activity
    }

    inner class Adapter(recyclerView: RecyclerView) : RecyclerViewAdapter(recyclerView) {

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return when (viewType) {
                0 -> {
                    val view = View(context)
                    view.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, pxFromDip(40f))
                    return RecyclerViewHolder(view)
                }
                R.layout.layout_item -> RecyclerViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
                else -> {
                    val holder = RecyclerViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
                    holder.itemView.layoutParams.height = parent.measuredHeight - indicatorHeight
                    holder
                }
            }
        }

        override fun getItemViewType(position: Int, section: Int, type: ItemType): Int {
            return when (type) {
                ItemType.HEADER -> 0
                ItemType.VIEW -> R.layout.layout_item
                ItemType.FOOTER -> R.layout.nested_scroll_item
            }
        }

        override fun numberOfItems(section: Int): Int {
            return 10
        }

        override fun shouldExistSectionHeader(section: Int): Boolean {
            return true
        }

        override fun shouldExistSectionFooter(section: Int): Boolean {
            return true
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.textView).text = "Item $position"
        }

        override fun onBindSectionFooterViewHolder(viewHolder: RecyclerViewHolder, section: Int) {
            val item = viewHolder.itemView as NestedScrollItem
            if (childContainer == null) {
                val container = LayoutInflater.from(context).inflate(R.layout.nested_scroll_child_container, null) as NestedScrollChildContainer
                container.nestedScrollHelper = helper
                container.onScrollListener = onScrollListener
                viewPager = container.viewPager
                childContainer = container
                ViewPagerHelper.bind(magicIndicator, viewPager)
            }
            item.setView(childContainer!!)
        }

        override fun onItemClick(position: Int, section: Int, item: View) {
            println("click parent")
        }
    }
}