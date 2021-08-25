package com.lhx.glakitDemo.nest

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.nested.NestedChildRecyclerView
import com.lhx.glakit.nested.NestedScrollHelper
import com.lhx.glakitDemo.R
import net.lucode.hackware.magicindicator.MagicIndicator
import net.lucode.hackware.magicindicator.ViewPagerHelper
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView

class NestedScrollChildContainer: LinearLayout {

    val magicIndicator: MagicIndicator by lazy { findViewById(R.id.magic_indicator) }
    val viewPager: ViewPager by lazy { findViewById(R.id.view_pager) }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var nestedScrollHelper: NestedScrollHelper? = null

    val titles = arrayOf("水果生鲜", "休闲零食", "男装女装", "日用百货", "母婴用品")
    val fragments = arrayOf(NestedScrollFragment(),
        NestedScrollFragment(),
        NestedScrollFragment(),
        NestedScrollFragment(),
        NestedScrollFragment())

    fun getChildRecyclerView(): NestedChildRecyclerView? {
        val fragment = fragments[viewPager.currentItem]
        return if (fragment.isInit) fragment.childRecyclerView else null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()

        val commonNavigator = CommonNavigator(context)
        commonNavigator.adapter = object : CommonNavigatorAdapter() {
            override fun getCount(): Int {
                return titles.size
            }

            override fun getTitleView(context: Context, index: Int): IPagerTitleView {
                val colorTransitionPagerTitleView = ColorTransitionPagerTitleView(context)
                colorTransitionPagerTitleView.normalColor = Color.GRAY
                colorTransitionPagerTitleView.selectedColor = Color.BLACK
                colorTransitionPagerTitleView.text = titles[index]
                colorTransitionPagerTitleView.setOnClickListener { viewPager.currentItem = index }
                return colorTransitionPagerTitleView
            }

            override fun getIndicator(context: Context): IPagerIndicator {
                val indicator = LinePagerIndicator(context)
                indicator.mode = LinePagerIndicator.MODE_WRAP_CONTENT
                return indicator
            }
        }
        magicIndicator.navigator = commonNavigator
        ViewPagerHelper.bind(magicIndicator, viewPager)
        
        val activity = context as BaseActivity
        viewPager.adapter = object: FragmentPagerAdapter(activity.supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            override fun getCount(): Int {
                return fragments.size
            }

            override fun getItem(position: Int): Fragment {
                val fragment = fragments[position]
                fragment.nestedScrollHelper = nestedScrollHelper
                return fragment
            }
        }
    }
}