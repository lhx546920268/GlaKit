package com.lhx.glakit.base.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.*
import androidx.fragment.app.Fragment
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.ActivityStack
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.base.interf.BasePage
import com.lhx.glakit.base.widget.BaseContainer


/**
 * 基础fragment
 */
abstract class BaseFragment : Fragment(), BasePage {

    //容器
    private var _container: BaseContainer? = null
    /**
     * 基础容器
     */
    override val baseContainer: BaseContainer?
        get(){
            return _container
        }

    /**
     * 获取 activity 或者 fragment 绑定的bundle
     */
    override val attachedBundle: Bundle?
        get(){
            if(arguments != null){
                return arguments
            }

            if(activity != null){
                return activity!!.intent.extras
            }

            return null
        }

    /**
     * 获取context
     */
    override val attachedContext: Context?
        get(){
            return context
        }

    /**
     * 关联的activity
     */
    override val attachedActivity: Activity?
        get(){
            return activity
        }

    //是否需要处理 onActivityResult
    var shouldProcessActivityResult = false

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (showBackItem()) {
            setShowBackButton(true)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, viewGroup: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (_container == null) { ///创建容器视图
            _container = BaseContainer(context)
            _container?.onEventHandler = this
            _container?.setShowTitleBar(showTitleBar())
            //内容视图
            initialize(inflater, _container!!, savedInstanceState)
        }
        return _container
    }

    //<editor-fold desc="动画">

    //获取开场动画
    @AnimRes
    fun getEnterAnim(): Int {
        return 0
    }

    //获取出场动画
    @AnimRes
    fun getExitAnim(): Int {
        return 0
    }

    //无动画
    @AnimRes
    fun getNoneAnim(): Int {
        return R.anim.anim_no
    }

    //打开activity 不要动画
    fun closeAnimate() {
        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    //</editor-fold>

    //<editor-fold desc="返回">

    //返回
    fun back() {
        if (activity != null) {
            activity!!.finish()
        }
    }

    fun back(resultCode: Int) {
        if (activity != null) {
            activity!!.setResult(resultCode)
            activity!!.finish()
        }
    }

    fun back(resultCode: Int, data: Intent?) {
        if (activity != null) {
            activity!!.setResult(resultCode, data)
            activity!!.finish()
        }
    }

    fun backToFragment(fragmentClass: Class<out BaseFragment>) {
        backTo(fragmentClass.name)
    }

    fun backTo(fragmentClass: Class<out BaseFragment>, include: Boolean) {
        backToFragment(fragmentClass, include, Int.MAX_VALUE)
    }

    fun backToFragment(fragmentClass: Class<out BaseFragment>, include: Boolean, resultCode: Int) {
        backTo(fragmentClass.name, include, resultCode)
    }

    fun backTo(toName: String) {
        backTo(toName, false, Int.MAX_VALUE)
    }

    fun backTo(toName: String, include: Boolean) {

        backTo(toName, include, Int.MAX_VALUE)
    }

    /**
     * 返回某个指定的 fragment
     * @param toName 对应的fragment类名 或者 activity类名 [BaseActivity.getName()]
     * @param include 是否包含toName
     * @param resultCode [android.app.Activity.setResult]
     */
    fun backTo(toName: String, include: Boolean, resultCode: Int) {
        ActivityStack.finishActivities(toName, include, resultCode)
    }

    /**
     * 返回到底部
     */
    fun backToRoot() {
        ActivityStack.finishActivitiesToRoot()
    }

    //</editor-fold>

    //<editor-fold desc="启动Activity">

    //启动一个带activity的fragment
    fun startActivity(fragmentClass: Class<out BaseFragment>) {
        startActivity(fragmentClass, null)
    }

    fun startActivity(fragmentClass: Class<out BaseFragment>, bundle: Bundle?) {
        startActivityForResult(fragmentClass, 0, bundle)
    }

    fun startActivityForResult(fragmentClass: Class<out BaseFragment>, requestCode: Int) {
        startActivityForResult(fragmentClass, requestCode, null)
    }

    fun startActivityForResult(fragmentClass: Class<out BaseFragment>, requestCode: Int, bundle: Bundle?) {
        val intent = BaseActivity.getIntentWithFragment(context!!, fragmentClass)
        if (bundle != null) {
            bundle.remove(BaseActivity.FRAGMENT_STRING)
            intent.putExtras(bundle)
        }
        if (requestCode != 0) {
            startActivityForResult(intent, requestCode)
        } else {
            startActivity(intent)
        }
    }

    //</editor-fold>

    ///获取子视图
    fun <T : View> findViewById(resId: Int): T? {
        if(_container != null){
            return _container!!.findViewById(resId)
        }
        return null
    }

    //点击物理键
    fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val activity: Activity? = activity
        if (activity != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !activity.isTaskRoot) {
                back()
                return true
            }
        }
        return false
    }

    //分发点击物理键事件
    fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return false
    }

    //屏幕焦点改变
    fun onWindowFocusChanged(hasFocus: Boolean) {}
}