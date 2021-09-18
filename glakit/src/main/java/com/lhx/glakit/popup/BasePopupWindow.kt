package com.lhx.glakit.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.VoidCallback
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.extension.getColorCompat
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.utils.ViewUtils

/**
 * 基础弹窗
 */
abstract class BasePopupWindow(val context: Context) : PopupWindow(), PopupAnimation, PopupWindow.OnDismissListener {

    //触发消失的操作
    enum class DismissAction {
        OUTSIDE, //点击外部
        BACK_PRESSED, //返回键
        BACKGROUND, //点击背景
        API, //直接调用api
    }

    //动画方式
    override var animationStyle = AnimationStyle.TRANSLATE
    override val popupBackgroundView: View
        get() = backgroundView
    override val popupContentView: View?
        get() = _contentView
    override val popupContainer: View
        get() = container

    //点击弹窗以外的位置是否关闭弹窗
    var cancelable = true

    //弹窗消失回调
    private val onDismissHandlers: HashSet<VoidCallback> by lazy { HashSet() }

    //将要消失回调 返回是否需要消失
    var willDismissHandler: ((action: DismissAction) -> Boolean)? = null

    //是否是自己设置消失回调
    private var setByThis = false

    //容器
    protected val container: FrameLayout by lazy {FrameLayout(context)}

    //内容
    protected var _contentView: View? = null

    //背景
    protected val backgroundView: View by lazy {
        val view = View(context)
        view.setBackgroundColor(context.getColorCompat(R.color.dialog_background))
        view.setOnClickListener {
            dismiss(true, DismissAction.BACKGROUND)
        }
        view.layoutParams = FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        view
    }

    //是否需要动画
    private var shouldAnimate = true

    //添加弹窗消失回调
    fun addOnDismissHandler(onDismissHandler: VoidCallback) {
        onDismissHandlers.add(onDismissHandler)
    }

    //移除
    fun removeOnDismissHandler(onDismissHandler: VoidCallback) {
        onDismissHandlers.remove(onDismissHandler)
    }

    //禁止外部通过这个方法设置弹窗消失回调
    override fun setOnDismissListener(onDismissListener: OnDismissListener?) {
        if (!setByThis) {
            try {
                throw IllegalAccessException("请使用addOnDismissHandler")
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            return
        }
        super.setOnDismissListener(onDismissListener)
    }

    ///初始化
    @SuppressLint("ClickableViewAccessibility")
    private fun initialize() {
        if(contentView != null)
            return
        setByThis = true
        setOnDismissListener(this)
        setByThis = false

        isFocusable = false
        isTouchable = true
        isOutsideTouchable = true

        setTouchInterceptor { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE && !this@BasePopupWindow.isFocusable) {
                //如果焦点不在popupWindow上，且点击了外面，不再往下dispatch事件：
                if(cancelable){
                    dismiss(true, DismissAction.OUTSIDE)
                }
                cancelable
            } else false
            //否则default，往下dispatch事件:关掉popupWindow，
        }

        setAnimationStyle(R.anim.anim_no_duration)
        container.addView(backgroundView, 0)

        contentView = container

        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (shouldAnimate) {
            container.visibility = View.INVISIBLE
            container.post {
                executeShowAnimation()
            }
        }else{
            onPopupShow()
        }

        if (context is AppCompatActivity) {
            val activity = context
            activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        }
    }

    //返回键
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            dismiss(true, DismissAction.BACK_PRESSED)
        }
    }

    //动画显示弹窗，默认有动画
    fun showAsDropDown(anchor: View, animate: Boolean) {
        shouldAnimate = animate
        showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View) {
        require(_contentView != null) {
            "必须先调用 setPopupContentView"
        }
        initialize()
        //安卓7.0 以上 会占全屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            height = SizeUtils.getDisplayHeight(context) - rect.bottom
        }
        super.showAsDropDown(anchor)
    }

    fun dismiss(animate: Boolean, action: DismissAction = DismissAction.API) {
        if(animate){
            val dismiss = willDismissHandler?.let {
                it(action)
            } ?: true

            if (!dismiss)
                return
            executeDismissAnimation()
        }else{
            super.dismiss()
        }
    }

    //外部调用默认有动画
    override fun dismiss() {
        if(setByThis){
            super.dismiss()
        }else{
            dismiss(true, DismissAction.API)
        }
    }

    //弹窗已消失
    override fun onDismiss() {
        if (onDismissHandlers.size > 0) {
            for (onDismissHandler in onDismissHandlers) {
                onDismissHandler()
            }
        }
        onBackPressedCallback.isEnabled = false
    }

    override fun onPopupDismiss() {
        dismiss(false, DismissAction.API)
    }

    @CallSuper
    override fun onPopupShow() {
        onBackPressedCallback.isEnabled = true
    }

    //获取子视图
    fun <T : View> findViewById(@IdRes id: Int): T? {
        return contentView?.findViewById(id)
    }

    fun <T : View> requireViewById(@IdRes id: Int): T {
        return findViewById(id)
            ?: throw IllegalArgumentException("ID does not reference a View inside this View")
    }

    //获取内容视图
    fun setPopupContentView(view: View) {
        ViewUtils.removeFromParent(_contentView)
        _contentView = view
        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        configLayoutParams(view, params)
        container.addView(_contentView, params)
    }

    //配置内容视图
    abstract fun configLayoutParams(view: View, params: FrameLayout.LayoutParams)
}