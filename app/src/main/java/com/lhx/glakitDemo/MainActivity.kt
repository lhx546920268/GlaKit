package com.lhx.glakitDemo

import android.graphics.Color
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.tab.TabBarActivity
import com.lhx.glakitDemo.home.HomeFragment
import com.lhx.glakitDemo.me.MeFragment


class MainActivity : TabBarActivity() {

    val titles = arrayOf("首页", "我的")
    val icons = arrayOf(R.drawable.tab_home_n, R.drawable.tab_me_n)
    val checkedIcons = arrayOf(R.drawable.tab_home_s, R.drawable.tab_me_s)

    val fragments = arrayOf(HomeFragment(), MeFragment())

    override val numberOfTabBarItems: Int
        get() = titles.count()

    override val normalTitleColor: Int
        get() = Color.BLACK

    override val checkedTitleColor: Int
        get() = Color.CYAN

    override fun getFragment(position: Int): BaseFragment? {
        return fragments[position]
    }

    override fun getTitle(position: Int): CharSequence {
        return titles[position]
    }

    override fun getNormalIconRes(position: Int): Int {
        return icons[position]
    }

    override fun getCheckedIconRes(position: Int): Int {
        return checkedIcons[position]
    }
}