package com.lhx.glakit.base.activity

import android.content.Intent
import android.os.Bundle
import com.lhx.glakit.base.activity.ActivityStack.finishActivities
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer


/**
 * 基础视图activity 和 appBaseFragment 类似 不要通过 setContentView 设置内容视图
 */
open class BaseContainerActivity : BaseActivity() {

    //容器
    private var _container: BaseContainer? = null
    /**
     * 基础容器
     */
    override val baseContainer: BaseContainer?
        get(){
            return _container
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (_container == null) {
            _container = BaseContainer(this)
            _container?.run{
                setShowTitleBar(showTitleBar())
                onEventHandler = this@BaseContainerActivity
            }
            setContentView(_container)
            initialize(layoutInflater, _container!!, savedInstanceState)
        }
        name = (javaClass.name)
    }

    override fun getContentViewRes(): Int {
        return 0
    }


    //打开activity 不要动画
    fun closeAnimate() {
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    //<editor-fold desc="返回">

    fun backToActivity(activityClass: Class<out BaseActivity?>, resultCode: Int = Int.MAX_VALUE) {
        backTo(activityClass.name, resultCode)
    }

    /**
     * 返回某个指定的 fragment
     * @param toName 对应的fragment类名 或者 activity类名 [BaseActivity.name]
     * @param resultCode [android.app.Activity.setResult]
     */
    fun backTo(toName: String, resultCode: Int = Int.MAX_VALUE) {
        finishActivities(toName, resultCode)
    }

    //</editor-fold>

    //<editor-fold desc="Fragment">

    //启动一个带activity的fragment

    fun startFragment(fragmentClass: Class<out BaseFragment>, bundle: Bundle? = null) {
        startFragmentForResult(fragmentClass, 0, bundle)
    }

    fun startFragmentForResult(fragmentClass: Class<out BaseFragment>, requestCode: Int = 0, bundle: Bundle? = null) {
        val intent: Intent = getIntentWithFragment(this, fragmentClass)
        if (bundle != null) {
            bundle.remove(FRAGMENT_STRING)
            intent.putExtras(bundle)
        }
        if (requestCode != 0) {
            startActivityForResult(intent, requestCode)
        } else {
            startActivity(intent)
        }
    }

    //</editor-fold>
}