package com.lhx.glakit.base.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.AnimRes
import androidx.annotation.IdRes
import androidx.annotation.Nullable
import androidx.fragment.app.Fragment
import com.lhx.glakit.R
import com.lhx.glakit.api.HttpCancelable
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.base.interf.BasePage
import com.lhx.glakit.base.widget.BaseContainer


/**
 * 基础fragment
 */
@Suppress("unused_parameter")
abstract class BaseFragment : Fragment(), BasePage {

    //容器
    private var _container: BaseContainer? = null

    /**
     * 基础容器
     */
    override val baseContainer: BaseContainer?
        get() = _container

    /**
     * 获取 activity 或者 fragment 绑定的bundle
     */
    override val attachedBundle: Bundle?
        get() = if(arguments != null) arguments else activity?.intent?.extras

    /**
     * 获取context
     */
    override val attachedContext: Context?
        get() = context

    /**
     * 关联的activity
     */
    override val attachedActivity: Activity?
        get() = activity

    //是否需要处理 onActivityResult
    var shouldProcessActivityResult = false

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (showBackItem()) {
            setShowBackButton(true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        viewGroup: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_container == null) { //创建容器视图
            _container = BaseContainer(context)
            _container?.mOnEventCallback = this
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
//        activity?.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    //</editor-fold>

    //<editor-fold desc="返回">

    //返回
    fun back() {
        requireActivity().finish()
    }

    fun back(resultCode: Int, data: Intent? = null) {
        requireActivity().setResult(resultCode)
        requireActivity().finish()
    }

    fun backToFragment(
        fragmentClass: Class<out BaseFragment>,
        include: Boolean = false,
        resultCode: Int = Int.MAX_VALUE
    ) {
        backTo(fragmentClass.name, include, resultCode)
    }

    //</editor-fold>

    //<editor-fold desc="启动Activity">

    //启动一个带activity的fragment
    fun startActivity(fragmentClass: Class<out BaseFragment>, bundle: Bundle? = null) {
        startActivityForResult(fragmentClass, 0, bundle)
    }
    
    fun startActivityForResult(
        fragmentClass: Class<out BaseFragment>,
        requestCode: Int,
        bundle: Bundle? = null
    ) {
        val intent = BaseActivity.getIntentWithFragment(requireContext(), fragmentClass)
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
    fun <T : View> findViewById(@IdRes id: Int): T? {
        return _container?.findViewById(id)
    }

    fun <T : View> requireViewById(@IdRes id: Int): T {
        return findViewById(id)
            ?: throw IllegalArgumentException("ID does not reference a View inside this View")
    }

    //点击物理键
    open fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val activity: Activity? = activity
        if (activity != null) {
            if (keyCode == KeyEvent.KEYCODE_BACK && !requireActivity().isTaskRoot) {
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
    fun onWindowFocusChanged(hasFocus: Boolean) {
    }

    //http可取消的任务
    private var _currentTasks: HashSet<HttpCancelable>? = null
    override var currentTasks: HashSet<HttpCancelable>?
        get() = _currentTasks
        set(value) {
            _currentTasks = value
        }
}