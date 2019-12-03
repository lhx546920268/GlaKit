package com.lhx.glakit.base.activity

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.view.Window
import androidx.annotation.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lhx.glakit.base.activity.ActivityStack.addActivity
import com.lhx.glakit.base.activity.ActivityStack.removeActivity


/**
 * 基础activity
 */
class BaseActivity : AppCompatActivity() {

    //activity里面fragment的类名
    val FRAGMENT_STRING = "fragmentString"

    //登录请求code
    val LOGIN_REQUEST_CODE = 1118

    //登录完成回调
    private var mLoginHandler: LoginHandler? = null

    //是否有登录回调
    private var mHasLoginHandler = false

    //activity 名称 为fragment的类名 或者 activity类名
    private var mName: String? = null

    //当前显示的Fragment
    private var mFragment: AppBaseFragment? = null

    //是否可见
    private var mVisible = false

    fun getName(): String? {
        return mName
    }

    fun setName(name: String?) {
        mName = name
    }

    fun isVisible(): Boolean {
        return mVisible
    }

    override fun onResume() {
        super.onResume()
        mVisible = true
    }

    override fun onPause() {
        super.onPause()
        mVisible = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        super.onCreate(savedInstanceState)
        AppUtil.setStatusBarStyle(
            window, ContextCompat.getColor(this, R.color.status_bar_background_color),
            resources.getBoolean(R.bool.status_bar_is_light)
        )
        val layoutRes = getContentViewRes()
        if (layoutRes != 0) {
            setContentView(layoutRes)
        }
        //java.net.SocketException: sendto failed: ECONNRESET (Connection reset by peer)
        val policy = StrictMode.ThreadPolicy.Builder()
            .permitAll().build()
        StrictMode.setThreadPolicy(policy)
        ///生成fragment实例
        val className = intent.getStringExtra(FRAGMENT_STRING)
        if (className != null && layoutRes != 0) {
            val clazz: Class<*>
            try {
                clazz = Class.forName(className)
                //                if(!clazz.isAssignableFrom(AppBaseFragment.class)){
//                    throw new RuntimeException(className + "必须是AppBaseFragment或者其子类");
//                }
                mName = className
                val currentFragment: AppBaseFragment = clazz.newInstance() as AppBaseFragment
                val bundle = intent.extras
                if (bundle != null) {
                    currentFragment.setArguments(bundle)
                }
                if (currentFragment != null) {
                    setCurrentFragment(currentFragment)
                } else {
                    throw RuntimeException(className + "不能实例化")
                }
                val enter: Int = mFragment.getEnterAnim()
                if (enter != 0) {
                    overridePendingTransition(enter, R.anim.anim_no)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                finish()
            }
        } else {
            mName = javaClass.name
        }
        //添加到堆栈中
        addActivity(this)
    }

    @CallSuper
    override fun onDestroy() { //从堆栈移除
        removeActivity(this)
        super.onDestroy()
    }

    //获取视图内容，如果为0，则忽略 可以包含 fragment_container
    @LayoutRes
    fun getContentViewRes(): Int {
        return R.layout.app_base_activity
    }

    ///设置当前显示的fragment,无动画效果
    fun setCurrentFragment(currentFragment: AppBaseFragment?) {
        setCurrentFragment(currentFragment, 0, 0)
    }

    /**
     * 设置当前显示的fragment,可设置动画效果
     *
     * @param currentFragment 当前要显示的fragment
     * @param enter           进场动画
     * @param exit            出场动画
     */
    fun setCurrentFragment(currentFragment: AppBaseFragment?, @AnimRes enter: Int, @AnimRes exit: Int) {
        mFragment = currentFragment
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        if (enter != 0 && exit != 0) {
            transaction.setCustomAnimations(enter, exit)
        }
        transaction.replace(R.id.fragment_container, mFragment)
        transaction.commitAllowingStateLoss()
    }

    fun addFragment(fragment: AppBaseFragment?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.fragment_container, fragment)
        transaction.commitAllowingStateLoss()
    }

    fun removeFragment(fragment: AppBaseFragment?) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.remove(fragment)
        transaction.commitAllowingStateLoss()
    }

    fun getIntentWithFragment(
        context: Context?,
        fragmentClass: Class<out AppBaseFragment?>
    ): Intent? {
        val intent = Intent(context, AppBaseActivity::class.java)
        intent.putExtra(FRAGMENT_STRING, fragmentClass.name)
        return intent
    }

    //启动一个activity
    fun startActivity(activityClass: Class<out Activity?>?): Intent? {
        val intent = Intent(this, activityClass)
        startActivity(intent)
        return intent
    }

    override fun finish() {
        super.finish()
        if (mFragment != null) {
            val exit: Int = mFragment.getExitAnim()
            if (exit != 0) {
                overridePendingTransition(R.anim.anim_no, exit)
            }
        }
    }

    @CallSuper
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && isTaskRoot) {
            moveTaskToBack(true)
            true
        } else {
            if (mFragment != null && mFragment.onKeyDown(keyCode, event)) {
                true
            } else super.onKeyDown(keyCode, event)
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        return if (mFragment != null && mFragment.dispatchKeyEvent(event)) {
            true
        } else super.dispatchKeyEvent(event)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (mFragment != null) {
            mFragment.onWindowFocusChanged(hasFocus)
        }
    }

    //获取授权
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (mFragment != null) {
            mFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                LOGIN_REQUEST_CODE -> {
                    //登录
                    if (mLoginHandler != null) {
                        mLoginHandler.onLogin()
                        mLoginHandler = null
                    }
                    return
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun startActivityForResult(
        intent: Intent?,
        requestCode: Int,
        loginHandler: LoginHandler?
    ) {
        mLoginHandler = loginHandler
        mHasLoginHandler = true
        super.startActivityForResult(intent, requestCode)
    }

    //获取px
    fun pxFromDip(dip: Float): Int {
        return SizeUtil.pxFormDip(dip, this)
    }

    //获取颜色
    @ColorInt
    fun getColorCompat(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }

    //获取drawable
    fun getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(this, drawableRes)
    }

    ///获取bundle内容
    fun getExtraStringFromBundle(key: String?): String? {
        val nBundle = getBundle() ?: return ""
        return nBundle.getString(key)
    }

    fun getExtraDoubleFromBundle(key: String?): Double {
        val nBundle = getBundle()
        return nBundle?.getDouble(key) ?: 0
    }

    fun getExtraIntFromBundle(key: String?): Int {
        return getExtraIntFromBundle(key, 0)
    }

    fun getExtraIntFromBundle(key: String?, defValue: Int): Int {
        val nBundle = getBundle()
        return nBundle?.getInt(key, defValue) ?: defValue
    }

    fun getExtraLongFromBundle(key: String?): Long {
        val nBundle = getBundle()
        return nBundle?.getLong(key) ?: 0
    }

    fun getExtraBooleanFromBundle(key: String?, def: Boolean): Boolean {
        val nBundle = getBundle()
        return nBundle?.getBoolean(key, def) ?: def
    }

    fun getExtraBooleanFromBundle(key: String?): Boolean {
        return getExtraBooleanFromBundle(key, false)
    }

    fun getExtraStringListFromBundle(key: String?): List<String?>? {
        val nBundle = getBundle()
        return nBundle!!.getStringArrayList(key)
    }

    fun getExtraSerializableFromBundle(key: String?): Serializable? {
        val nBundle = getBundle()
        return nBundle!!.getSerializable(key)
    }

    //获取对应bundle
    fun getBundle(): Bundle? {
        return intent.extras
    }

}