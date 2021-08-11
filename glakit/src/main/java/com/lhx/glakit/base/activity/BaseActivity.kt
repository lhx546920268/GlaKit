package com.lhx.glakit.base.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lhx.glakit.R
import com.lhx.glakit.api.HttpCancelable
import com.lhx.glakit.api.HttpProcessor
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.utils.AppUtils

/**
 * 回调 activity result
 */
typealias ResultCallback = (data: Intent?) -> Unit

/**
 * 基础activity
 */
open class BaseActivity : AppCompatActivity(), HttpProcessor {

    //activity 名称 为fragment的类名 或者 activity类名
    var name: String? = null

    //当前显示的Fragment
    private var _fragment: BaseFragment? = null
    protected val fragment: BaseFragment?
        get() = _fragment

    //是否可见
    private var _visible = false
    val isVisible: Boolean
        get() = _visible

    /**
     * 内容视图 需要设置id为 current_content
     */
    protected val currentContentView: View?
        get() = if (_fragment?.baseContainer != null) _fragment!!.baseContainer!!.contentView else findViewById(
            R.id.current_content
        )

    /**
     * activity 启动器
     */
    protected lateinit var activityLauncher: ActivityResultLauncher<Intent>
        private set

    /**
     * 当前回调
     */
    protected var resultCallback: ResultCallback? = null

    //<editor-fold desc="父类方法">

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)

        //状态栏
        if (shouldSetStatusBarStyle) {
            AppUtils.setStatusBarStyle(
                window, ContextCompat.getColor(this, R.color.status_bar_background_color),
                resources.getBoolean(R.bool.status_bar_is_light)
            )
        }

        val layoutRes = getContentViewRes()
        if (layoutRes != 0) {
            setContentView(layoutRes)
        }

        //生成fragment实例
        val className = intent.getStringExtra(FRAGMENT_STRING)
        if (className != null && layoutRes != 0) {
            val clazz: Class<*>
            try {
                clazz = Class.forName(className)
                name = className
                val currentFragment = clazz.newInstance() as BaseFragment
                val bundle = intent.extras
                if (bundle != null) {
                    currentFragment.arguments = bundle
                }
                setFragment(currentFragment)
                val enter = _fragment?.getEnterAnim()
                if (enter != 0) {
                    overridePendingTransition(enter!!, R.anim.anim_no)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            name = javaClass.name
        }

        lifecycle.addObserver(this)
        activityLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (resultCallback != null && it.resultCode == Activity.RESULT_OK) {
                    resultCallback!!(it.data)
                }
                resultCallback = null
            }
    }

    override fun onResume() {
        super.onResume()
        _visible = true
    }

    override fun onPause() {
        super.onPause()
        _visible = false
    }

    //语言国际化
//    override fun attachBaseContext(newBase: Context) {
//        super.attachBaseContext(BaseContextWrapper.wrap(newBase, ""))
//    }

    //</editor-fold>

    //<editor-fold desc="内容">

    //是否需要设置状态栏样式
    open val shouldSetStatusBarStyle = true

    //获取视图内容，如果为0，则忽略 可以包含 fragment_container
    @LayoutRes
    open fun getContentViewRes(): Int {
        return R.layout.base_activity
    }

    /**
     * 设置当前显示的fragment,可设置动画效果
     *
     * @param currentFragment 当前要显示的fragment
     * @param enter           进场动画
     * @param exit            出场动画
     */
    fun setFragment(
        currentFragment: BaseFragment,
        @AnimRes enter: Int = 0,
        @AnimRes exit: Int = 0
    ) {
        _fragment = currentFragment
        val transaction = supportFragmentManager.beginTransaction()
        if (enter != 0 && exit != 0) {
            transaction.setCustomAnimations(enter, exit)
        }
        transaction.replace(R.id.fragment_container, currentFragment)
        transaction.commitAllowingStateLoss()
    }

    fun addFragment(fragment: BaseFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.commitAllowingStateLoss()
    }

    fun removeFragment(fragment: BaseFragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commitAllowingStateLoss()
    }

    companion object {

        //activity里面fragment的类名
        const val FRAGMENT_STRING = "fragmentString"

        //获取 fragment对应的 intent
        fun getIntentWithFragment(
            context: Context,
            fragmentClass: Class<out BaseFragment?>
        ): Intent {
            val intent = Intent(context, BaseActivity::class.java)
            intent.putExtra(FRAGMENT_STRING, fragmentClass.name)
            return intent
        }
    }

    //</editor-fold>


    //启动一个activity
    fun startActivity(activityClass: Class<out Activity>, extras: Bundle? = null) {
        val intent = Intent(this, activityClass)
        if (extras != null) {
            intent.putExtras(extras)
        }
        startActivity(intent)
    }

    fun startActivityForResult(
        activityClass: Class<out Activity>,
        extras: Bundle? = null,
        callback: ResultCallback
    ) {
        val intent = Intent(this, activityClass)
        if (extras != null) {
            intent.putExtras(extras)
        }
        resultCallback = callback
        activityLauncher.launch(intent)
    }

    override fun finish() {
        super.finish()
        if (_fragment != null) {
            val exit = _fragment!!.getExitAnim()
            if (exit != 0) {
                overridePendingTransition(R.anim.anim_no, exit)
            }
        }
    }

    fun finish(resultCode: Int) {
        setResult(resultCode)
        finish()
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (_fragment != null && _fragment!!.dispatchKeyEvent(event)) {
            true
        } else super.dispatchKeyEvent(event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        _fragment?.onWindowFocusChanged(hasFocus)
    }

    //http可取消的任务
    override var currentTasks: HashSet<HttpCancelable>? = null

    //回调
    private var callbackEntities: HashMap<Int, CallbackEntity>? = null

    //添加一个回调 onActivityResult
    fun addCallback(
        callback: ResultCallback,
        requestCode: Int,
        removeAfterUse: Boolean = true
    ) {
        if (callbackEntities == null) {
            callbackEntities = HashMap()
        }
        callbackEntities!![requestCode] = CallbackEntity(callback, removeAfterUse)
    }

    @Suppress("deprecation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK && !callbackEntities.isNullOrEmpty()) {
            val entity = callbackEntities!![requestCode]
            if (entity != null) {
                entity.callback(data)
                if (entity.removeAfterUse) {
                    callbackEntities!!.remove(requestCode)
                }
                return
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    //回调
    private class CallbackEntity(
        val callback: ResultCallback,
        val removeAfterUse: Boolean
    )
}