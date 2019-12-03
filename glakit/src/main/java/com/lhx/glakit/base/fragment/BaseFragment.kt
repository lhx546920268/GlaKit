package com.lhx.glakit.base.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.annotation.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.ActivityStack
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.base.interf.BasePage
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.base.widget.TitleBar


/**
 * 基础fragment
 */
abstract class BaseFragment : Fragment(), BasePage {

    //容器
    private var _container: BaseContainer? = null
    val container: BaseContainer?
    get(){
        return _container
    }

    //是否已初始化
    val isInit: Boolean
    get() {
        return container != null
    }

    //是否需要处理 onActivityResult
    var shouldProcessActivityResult = false

    override fun onActivityCreated(@Nullable savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (showBackItem()) {
            setShowBackButton(true)
        }
    }

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


    //子类可重写这个方法设置 contentView
    protected abstract fun initialize(inflater: LayoutInflater?, container: BaseContainer, saveInstanceState: Bundle?)

    //是否需要显示标题栏
    protected fun showTitleBar(): Boolean {
        return true
    }

    //是否需要显示返回按钮
    protected fun showBackItem(): Boolean {
        return activity != null && !activity!!.isTaskRoot
    }

    //获取内容视图
    protected fun getContentView(): View? {
        return container?.getContentView()
    }

    protected fun setContentView(contentView: View?) {
        container?.setContentView(contentView)
    }

    protected fun setContentView(@LayoutRes layoutResId: Int) {
        container?.setContentView(layoutResId)
    }

    //显示返回按钮
    fun setShowBackButton(show: Boolean) {
        container?.setShowBackButton(show)
    }

    //设置标题
    fun setTitle(title: CharSequence?) {
        container?.setTitle(title)
    }

    fun getTitle(): CharSequence? {
        return container?.getTitle()
    }

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

    fun getTitleBar(): TitleBar? {
        return _container?.getTitleBar()
    }


    ///获取子视图
    fun <T : View> findViewById(resId: Int): T? {
        if(container != null){
            return container!!.findViewById(resId)
        }
        return null
    }

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
        val intent: Intent = BaseActivity.getIntentWithFragment(mContext, fragmentClass)
        if (bundle != null) {
            bundle.remove(AppBaseActivity.FRAGMENT_STRING)
            intent.putExtras(bundle)
        }
        if (requestCode != 0) {
            startActivityForResult(intent, requestCode)
        } else {
            startActivity(intent)
        }
    }

    /**
     * 页面加载失败后重新加载 子类可重写
     */
    protected fun onReloadPage() {}

    /**
     * 点击页面加载失败视图
     */
    fun onClickPageLoadFai() {
        onReloadPage()
    }

    /**
     * 点击返回按钮
     */
    fun onBack() {
        back()
    }

    /**
     * 页面加载视图显示
     *
     * @param pageLoadingView 页面加载视图
     * @param params          页面加载视图布局参数
     */
    fun onPageLoadingShow(
        pageLoadingView: View?,
        params: RelativeLayout.LayoutParams?
    ) {
    }

    /**
     * 页面加载失败视图显示
     *
     * @param pageLoadFailView 页面加载失败视图
     * @param params           布局参数
     */
    fun onPageLoadFailShow(
        pageLoadFailView: View?,
        params: RelativeLayout.LayoutParams?
    ) {
    }

    /**
     * 空视图显示
     *
     * @param emptyView 空视图
     * @param params    布局参数
     */
    fun onShowEmptyView(emptyView: View?, params: RelativeLayout.LayoutParams?) {}

    //打开activity 不要动画
    fun closeAnimate() {
        mActivity!!.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun setPageLoading(pageLoading: Boolean) {
        mContainer.setPageLoading(pageLoading)
    }

    /**
     * 设置页面载入
     * @param pageLoading 是否载入
     * @param loadingText 载入文字
     */
    fun setPageLoading(pageLoading: Boolean, loadingText: String?) {
        mContainer.setPageLoading(pageLoading, loadingText)
    }

    fun isPageLoading(): Boolean {
        return mContainer.isPageLoading()
    }

    fun setLoading(loading: Boolean) {
        setLoading(loading, 0, "加载中...")
    }

    fun setLoading(loading: Boolean, delay: Long) {
        setLoading(loading, delay, "加载中...")
    }

    fun setLoading(
        loading: Boolean,
        delay: Long,
        text: String?
    ) {
        mContainer.setLoading(loading, delay, text)
    }

    fun setPageLoadFail(pageLoadFail: Boolean) {
        mContainer.setPageLoadFail(pageLoadFail)
    }

    fun getPageLoadFailView(): View? {
        return mContainer.getPageLoadFailView()
    }

    /**
     * 设置页面载入失败
     * @param pageLoadFail 是否载入失败
     * @param logoResId logo
     * @param title 标题
     * @param subtitle 副标题
     */
    fun setPageLoadFail(
        pageLoadFail: Boolean, @DrawableRes logoResId: Int,
        title: String?,
        subtitle: String?
    ) {
        mContainer.setPageLoadFail(pageLoadFail, logoResId, title, subtitle)
    }

    fun isPageLoadFail(): Boolean {
        return mContainer.isPageLoadFail()
    }

    fun setShowEmptyView(text: String?) {
        mContainer.setShowEmptyView(true, text, 0)
    }

    fun showEmptyView(show: Boolean, text: String?) {
        mContainer.setShowEmptyView(show, text, 0)
    }

    /**
     * 设置显示空视图
     * @param text 显示的信息
     * @param iconRes 图标logo
     */
    fun setShowEmptyView(
        show: Boolean,
        text: String?, @DrawableRes iconRes: Int
    ) {
        mContainer.setShowEmptyView(show, text, iconRes)
    }

    //显示空视图
    fun setShowEmptyView(show: Boolean, @LayoutRes layoutRes: Int) {
        mContainer.setShowEmptyView(show, layoutRes)
    }

    fun setShowEmptyView(show: Boolean, emptyView: View?) {
        mContainer.setShowEmptyView(show, emptyView)
    }

    //设置底部视图
    fun setBottomView(bottomView: View?) {
        mContainer.setBottomView(bottomView)
    }

    fun setBottomView(bottomView: View?, height: Int) {
        mContainer.setBottomView(bottomView, height)
    }

    fun setBottomView(@LayoutRes res: Int) {
        mContainer.setBottomView(res)
    }

    fun getBottomView(): View? {
        return if (mContainer == null) null else mContainer.getBottomView()
    }

    fun setTopView(topView: View?) {
        mContainer.setTopView(topView)
    }

    //设置顶部视图
    fun setTopView(topView: View?, height: Int) {
        mContainer.setTopView(topView, height)
    }

    fun setTopView(@LayoutRes res: Int) {
        mContainer.setTopView(res)
    }

    fun getTopView(): View? {
        return if (mContainer == null) null else mContainer.getTopView()
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

    //获取颜色
    @ColorInt
    fun getColor(@ColorRes colorRes: Int): Int {
        return ContextCompat.getColor(mContext, colorRes)
    }

    //获取drawable
    fun getDrawable(@DrawableRes drawableRes: Int): Drawable? {
        return ContextCompat.getDrawable(mContext, drawableRes)
    }

    //获取dime
    fun getDimen(@DimenRes dimen: Int): Float {
        return mContext.getResources().getDimension(dimen)
    }

    fun getDimenIntValue(@DimenRes dimen: Int): Int {
        return mContext.getResources().getDimensionPixelOffset(dimen)
    }

    fun getInteger(@IntegerRes intValue: Int): Int {
        return mContext.getResources().getInteger(intValue)
    }

    fun getDimensionPixelSize(@DimenRes dimen: Int): Int {
        return mContext.getResources().getDimensionPixelSize(dimen)
    }

    //获取px
    fun pxFromDip(dip: Float): Int {
        return SizeUtil.pxFormDip(dip, mContext)
    }

    ///获取bundle内容
    fun <T : Parcelable?> getExtraParcelableArrayListFromBundle(key: String?): ArrayList<T>? {
        val nBundle = getBundle() ?: return null
        return nBundle.getParcelableArrayList<Parcelable>(key)
    }

    fun <T : Parcelable?> getExtraParcelableFromBundle(key: String?): T? {
        val nBundle = getBundle() ?: return null
        return nBundle.getParcelable(key)
    }

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
        var bundle = arguments
        if (bundle != null) {
            return bundle
        }
        if (mActivity != null) {
            bundle = mActivity!!.intent.extras
            if (bundle != null) {
                return bundle
            }
        }
        return null
    }
}