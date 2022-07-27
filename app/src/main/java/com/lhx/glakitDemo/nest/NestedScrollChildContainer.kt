package com.lhx.glakitDemo.nest

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.nested.NestedChildRecyclerView
import com.lhx.glakit.nested.NestedScrollHelper
import com.lhx.glakitDemo.R

class NestedScrollChildContainer: LinearLayout {

    val viewPager: ViewPager by lazy { findViewById(R.id.view_pager) }
    var onScrollListener: RecyclerView.OnScrollListener? = null
        set(value) {
            field = value
            for (fragment in fragments) {
                fragment.onScrollListener = value
            }
        }

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var nestedScrollHelper: NestedScrollHelper? = null

    val fragments = arrayOf(NestedScrollFragment(),
        NestedScrollFragment(),
        NestedScrollFragment(),
        NestedScrollFragment(),
        NestedScrollFragment())

    val currentFragment: NestedScrollFragment
        get() = fragments[viewPager.currentItem]

    fun getChildRecyclerView(): NestedChildRecyclerView? {
        val fragment = fragments[viewPager.currentItem]
        return if (fragment.isInit) fragment.childRecyclerView else null
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        
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