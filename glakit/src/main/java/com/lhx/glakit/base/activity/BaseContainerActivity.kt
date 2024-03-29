package com.lhx.glakit.base.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import com.lhx.glakit.base.activity.ActivityLifeCycleManager.finishActivities
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.base.widget.BaseContainerPage


/**
 * 基础视图activity 和 appBaseFragment 类似 不要通过 setContentView 设置内容视图
 */
abstract class BaseContainerActivity : BaseActivity(), BaseContainerPage {

    //容器
    private var _container: BaseContainer? = null

    /**
     * 基础容器
     */
    override val baseContainer: BaseContainer?
        get() = _container

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (_container == null) {
            _container = BaseContainer(this)
            _container?.run {
                setShowTitleBar(showTitleBar())
                if (showBackItem()) {
                    setShowBackButton(true)
                }
                mOnEventCallback = this@BaseContainerActivity
            }
            setContentView(_container)
            initialize(layoutInflater, _container!!, savedInstanceState)
        }
        name = javaClass.name
    }

    override fun getContentViewRes(): Int {
        return 0
    }

    //返回键
    private var onBackPressedCallback: OnBackPressedCallback? = null

    override fun onStart() {
        super.onStart()
        if (showBackItem()) {
            if (onBackPressedCallback == null) {
                onBackPressedCallback = object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        finish()
                    }
                }
                onBackPressedDispatcher.addCallback(onBackPressedCallback!!)
            }
            onBackPressedCallback!!.isEnabled = true
        }
    }

    override fun onStop() {
        super.onStop()
        onBackPressedCallback?.isEnabled = false
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
        finishActivities(toName, resultCode = resultCode)
    }

    //</editor-fold>

    //<editor-fold desc="Fragment">

    //启动一个带activity的fragment
    fun startFragment(fragmentClass: Class<out BaseFragment>, extras: Bundle? = null) {
        val intent = getIntentWithFragment(this, fragmentClass)
        if (extras != null) {
            extras.remove(FRAGMENT_STRING)
            intent.putExtras(extras)
        }
        startActivity(intent)
    }

    fun startFragmentForResult(
        fragmentClass: Class<out BaseFragment>,
        extras: Bundle? = null,
        callback: ResultCallback
    ) {
        val intent = getIntentWithFragment(this, fragmentClass)
        if (extras != null) {
            extras.remove(FRAGMENT_STRING)
            intent.putExtras(extras)
        }
        resultCallback = callback
        activityLauncher.launch(intent)
    }

    //</editor-fold>
}