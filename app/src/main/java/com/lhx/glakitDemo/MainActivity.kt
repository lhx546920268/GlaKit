package com.lhx.glakitDemo

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.tab.TabBarActivity
import com.lhx.glakitDemo.home.HomeFragment
import com.lhx.glakitDemo.home.User
import com.lhx.glakitDemo.me.MeFragment


class MainActivity : TabBarActivity() {

    val user = User()

    val titles = arrayOf("首页", "我的")
    val icons = arrayOf(R.drawable.tab_home_n, R.drawable.tab_me_n)
    val checkedIcons = arrayOf(R.drawable.tab_home_s, R.drawable.tab_me_s)

    val fragments = arrayOf(HomeFragment(), MeFragment())
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        application.registerActivityLifecycleCallbacks(LifeCycle)
        user.addObserver(this, arrayOf("title", "subtitle"), {oldValue, newValue, property ->
            if (oldValue != newValue) {
                Log.d("change", "${property.name} did change oldValue = $oldValue, newValue = $newValue")
            }
        }, true)
    }

    override val numberOfTabBarItems: Int
        get() = titles.count()

    override val normalTitleColor: Int
        get() = Color.BLACK

    override val checkedTitleColor: Int
        get() = Color.CYAN

    override fun getFragment(position: Int): BaseFragment {
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

    override fun onCheck(position: Int) {
        user.title = getTitle(position) as String
        user.subtitle = getTitle(position) as String
        count ++
        if(count > 5){
            user.notifyChange()
            count = 0
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}