package com.lhx.glakit.base.widget

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.*
import androidx.core.content.ContextCompat
import com.lhx.glakit.api.HttpProcessor
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import com.lhx.glakit.base.constant.PageStatus
import com.lhx.glakit.loading.InteractionCallback
import com.lhx.glakit.utils.SizeUtils
import java.io.Serializable

/**
 * 关联的
 */
interface BaseAttached {

    /**
     * 获取 activity 或者 fragment 绑定的bundle
     */
    val attachedBundle: Bundle?
        get() = attachedActivity?.intent?.extras

    /**
     * 获取context
     */
    val attachedContext: Context?
        get() = attachedActivity

    /**
     * 关联的activity
     */
    val attachedActivity: Activity?
}

/**
 * 基础页面接口
 */
interface BasePage: BaseAttached, BaseContainer.OnEventCallback, InteractionCallback, HttpProcessor {

    /**
     * 基础容器
     */
    val baseContainer: BaseContainer?

    /**
     * 标题栏
     */
    val titleBar: TitleBar?
        get() = baseContainer?.titleBar

    /**
     * 是否已初始化
     */
    val isInit: Boolean
        get() = baseContainer != null

    //<editor-fold desc="Getter">

    //获取颜色
    @ColorInt
    fun getColorCompat(@ColorRes colorRes: Int): Int {
        if(attachedContext != null){
            ContextCompat.getColor(attachedContext!!, colorRes)
        }
        return 0
    }

    //获取drawable
    fun getDrawableCompat(@DrawableRes drawableRes: Int): Drawable? {
        if(attachedContext != null){
            return ContextCompat.getDrawable(attachedContext!!, drawableRes)
        }
        return null
    }

    //获取dime
    fun getDimen(@DimenRes dimen: Int): Float {
        if(attachedContext != null){
            return attachedContext!!.resources.getDimension(dimen)
        }
        return 0f
    }

    fun getDimenIntValue(@DimenRes dimen: Int): Int {
        if(attachedContext != null){
            return attachedContext!!.resources.getDimensionPixelOffset(dimen)
        }
        return 0
    }

    fun getInteger(@IntegerRes intValue: Int): Int {
        if(attachedContext != null){
            return attachedContext!!.resources.getInteger(intValue)
        }

        return 0
    }

    fun getDimensionPixelSize(@DimenRes dimen: Int): Int {
        if(attachedContext != null){
            return attachedContext!!.resources.getDimensionPixelSize(dimen)
        }

        return 0
    }

    //获取px
    fun pxFromDip(dip: Float): Int {
        if(attachedContext != null){
            return SizeUtils.pxFormDip(dip, attachedContext!!)
        }
        return 0
    }

    //</editor-fold>

    //<editor-fold desc="Bundle">

    ///获取bundle内容
    fun <T : Parcelable> getParcelableArrayListFromBundle(key: String?): ArrayList<T>? {
        if(attachedBundle != null){
            return attachedBundle!!.getParcelableArrayList(key)
        }
        return null
    }

    fun <T : Parcelable> getParcelableFromBundle(key: String?): T? {
        if(attachedBundle != null){
            return attachedBundle!!.getParcelable(key)
        }
        return null
    }

    fun getStringFromBundle(key: String?): String? {
        if(attachedBundle != null){
            return attachedBundle!!.getString(key)
        }

        return null
    }

    fun getDoubleFromBundle(key: String?): Double {
        if(attachedBundle != null){
            attachedBundle!!.getDouble(key, 0.0)
        }

        return 0.0
    }

    fun getIntFromBundle(key: String?): Int {
        return getIntFromBundle(key, 0)
    }

    fun getIntFromBundle(key: String?, defValue: Int): Int {
        if(attachedBundle != null){
            return attachedBundle!!.getInt(key, defValue)
        }
        return defValue
    }

    fun getLongFromBundle(key: String?): Long {
        if(attachedBundle != null){
            return attachedBundle!!.getLong(key, 0)
        }

        return 0
    }

    fun getBooleanFromBundle(key: String?): Boolean {
        return getBooleanFromBundle(key, false)
    }

    fun getBooleanFromBundle(key: String?, defValue: Boolean): Boolean {
        if(attachedBundle != null){
            return attachedBundle!!.getBoolean(key, defValue)
        }

        return defValue
    }

    fun getStringListFromBundle(key: String?): List<String>? {
        if(attachedBundle != null){
            return attachedBundle!!.getStringArrayList(key)
        }
        return null
    }

    fun getSerializableFromBundle(key: String?): Serializable? {
        if(attachedBundle != null){
            return attachedBundle!!.getSerializable(key)
        }
        return null
    }

    //</editor-fold>

    //<editor-fold desc="BaseContainer.OnEventHandler">

    /**
     * 页面刷新
     */
    override fun onReloadPage() {}

    /**
     * 点击返回按钮
     */
    override fun onBack() {
        if(attachedActivity != null){
            attachedActivity!!.finish()
        }
    }

    /**
     * 页面加载视图显示
     * @param pageLoadingView 页面加载视图
     */
    override fun onShowPageLoadingView(pageLoadingView: View) {}

    /**
     * 空视图显示
     * @param emptyView 空视图
     */
    override fun onShowEmptyView(emptyView: View) {}

    //</editor-fold>

    //<editor-fold desc="InteractionCallback">

    fun isLoading(): Boolean {
        return if(baseContainer != null) baseContainer!!.isLoading() else false
    }

    override fun showLoading(delay: Long, text: CharSequence?) {
        baseContainer?.showLoading(delay, text)
    }

    override fun hideLoading() {
        baseContainer?.hideLoading()
    }

    override fun showToast(text: CharSequence) {
        baseContainer?.showToast(text)
    }

    //</editor-fold>

    //<editor-fold desc="页面状态">

    /**
     * 页面加载
     * @param pageLoading Boolean 是否在页面加载
     */
    fun setPageLoading(pageLoading: Boolean) {
        baseContainer?.setPageStatus(if(pageLoading) PageStatus.LOADING else PageStatus.NORMAL)
    }

    fun isPageLoading(): Boolean {
        return if(baseContainer != null) baseContainer!!.isPageLoading() else false
    }


    /**
     * 页面加载失败
     * @param pageLoadFail Boolean 设置显示页面是否加载失败
     */
    fun setPageLoadFail(pageLoadFail: Boolean) {
        baseContainer?.setPageStatus(if(pageLoadFail) PageStatus.FAIL else PageStatus.NORMAL)
    }

    fun isPageLoadFail(): Boolean {
        return if(baseContainer != null) baseContainer!!.isPageLoadFail() else false
    }

    /**
     * 设置是否显示空视图
     * @param show 是否显示
     */
    fun setShowEmptyView(show: Boolean) {
        baseContainer?.setPageStatus(if(show) PageStatus.EMPTY else PageStatus.NORMAL)
    }

    //</editor-fold>

    //<editor-fold desc="页面内容">

    //设置底部视图
    fun setBottomView(bottomView: View?) {
        baseContainer?.setBottomView(bottomView)
    }

    fun setBottomView(bottomView: View?, height: Int) {
        baseContainer?.setBottomView(bottomView, height)
    }

    fun setBottomView(@LayoutRes res: Int) {
        baseContainer?.setBottomView(res)
    }

    fun getBottomView(): View? {
        return baseContainer?.bottomView
    }

    //设置顶部视图
    fun setTopView(topView: View?) {
        baseContainer?.setTopView(topView)
    }

    fun setTopView(topView: View?, height: Int) {
        baseContainer?.setTopView(topView, height)
    }

    fun setTopView(@LayoutRes res: Int) {
        baseContainer?.setTopView(res)
    }

    fun getTopView(): View? {
        return baseContainer?.topView
    }

    //设置内容视图
    fun setContainerContentView(contentView: View?) {
        baseContainer?.setContentView(contentView)
    }

    fun setContainerContentView(@LayoutRes layoutResId: Int) {
        baseContainer?.setContentView(layoutResId)
    }

    fun getContainerContentView(): View? {
        return baseContainer?.contentView
    }

    //</editor-fold>

    //<editor-fold desc="标题栏">

    //是否需要显示标题栏
    fun showTitleBar(): Boolean {
        return true
    }

    //是否需要显示返回按钮
    fun showBackItem(): Boolean {
        return attachedActivity != null && !attachedActivity!!.isTaskRoot
    }

    //显示返回按钮
    fun setShowBackButton(show: Boolean) {
        baseContainer?.setShowBackButton(show)
    }

    //设置标题
    fun setBarTitle(@StringRes title: Int) {
        baseContainer?.setTitle(attachedContext?.getString(title))
    }

    fun setBarTitle(title: CharSequence?) {
        baseContainer?.setTitle(title)
    }

    fun getBarTitle(): CharSequence? {
        return baseContainer?.getTitle()
    }

    //</editor-fold>

    //子类可重写这个方法设置 contentView
    fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?)

    /**
     * 返回某个指定的 fragment
     * @param toName 对应的fragment类名 或者 activity类名 [BaseActivity.getName()]
     * @param include 是否包含toName
     * @param resultCode [android.app.Activity.setResult]
     */
    fun backTo(toName: String, include: Boolean = false, resultCode: Int = Int.MAX_VALUE) {
        ActivityLifeCycleManager.finishActivities(toName, include, resultCode)
    }

    /**
     * 返回到底部
     */
    fun backToRoot() {
        ActivityLifeCycleManager.finishActivitiesToRoot()
    }
}