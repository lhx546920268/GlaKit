package com.lhx.glakitDemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.myanmartools.ZawgyiDetector
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.tab.TabBarActivity
import com.lhx.glakitDemo.home.HomeFragment
import com.lhx.glakitDemo.home.User
import com.lhx.glakitDemo.me.MeFragment

@Route(path = "/app/index")
class MainActivity : TabBarActivity() {

    val user = User()

    val titles = arrayOf("首页", "我的")
    val icons = arrayOf(R.drawable.tab_home_n, R.drawable.tab_me_n)
    val checkedIcons = arrayOf(R.drawable.tab_home_s, R.drawable.tab_me_s)

    val fragments = arrayOf(HomeFragment(), MeFragment())
    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        println("MainActivity $this")
        application.registerActivityLifecycleCallbacks(LifeCycle)
        user.addObserver(this, arrayOf("title", "subtitle"), {oldValue, newValue, property ->
            if (oldValue != newValue) {
                Log.d("change", "${property.name} did change oldValue = $oldValue, newValue = $newValue")
            }
        }, true)

        val zawgyiDetector = ZawgyiDetector()

        println("mm3 Post စစ်ဆေးနေပါသည်၊ ပင်မစာမျက်နှာသို့သွားကြစို့ ${zawgyiDetector.getZawgyiProbability("Post စစ်ဆေးနေပါသည်၊ ပင်မစာမျက်နှာသို့သွားကြစို့")}")
        println("mm3 Post စစ်ဆေးနေပါသည်၊ ${zawgyiDetector.getZawgyiProbability("Post စစ်ဆေးနေပါသည်၊")}")
        println("zawgyi Post စစ္ေဆးေနပါသည္၊ ပင္မစာမ်က္ႏွာသို႔သြားၾကစို႔ ${zawgyiDetector.getZawgyiProbability("သင့္ကုန္ပစၥည္းႏွင့္dataကို သူတပါးလ်င္ျမန္စြာရွာေဖြႏိုင္ရန္ ေအာက္ပါအခ်က္လက္မ်ားကိုျပည့္စုံစြာျဖည့္ဆည္းေပးပါ")}")

        println("mm3 တင် ${zawgyiDetector.getZawgyiProbability("တင်")}")
        println("zawgyi တင္ ${zawgyiDetector.getZawgyiProbability("တင္")}")
        println("mm3 + zawgyi တင် စစ္ေဆးေနပါသည္၊ ${zawgyiDetector.getZawgyiProbability("တင် စစ္ေဆးေနပါသည္၊")}")
        println("zawgyi + mm3 တင္ ရွေးချယ်ရန် ${zawgyiDetector.getZawgyiProbability("တင္ ရွေးချယ်ရန်")}")
        println("nothing ${zawgyiDetector.getZawgyiProbability("No subscribers registered for event class org.greenrobot.eventbus.NoSubscriberEvent")}")

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        println("onSaveInstanceState $outState")
    }
}