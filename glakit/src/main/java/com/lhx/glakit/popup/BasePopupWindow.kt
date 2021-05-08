package com.lhx.glakit.popup

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.lhx.glakit.R
import com.lhx.glakit.base.interf.VoidCallback

/**
 * 基础弹窗
 */
abstract class BasePopupWindow(val context: Context) : PopupWindow(), PopupWindow.OnDismissListener {

    //动画
    enum class AnimationStyle {
        //平移
        TRANSLATE,

        //高度缩放
        SCALE,

        //自定义，要重写executeCustomAnimation
        CUSTOM,
    }

    //动画方式
    var animationStyle = AnimationStyle.TRANSLATE

    //点击弹窗以外的位置是否关闭弹窗
    var cancelable = true

    //弹窗消失回调
    private val onDismissHandlers: HashSet<VoidCallback> by lazy { HashSet() }

    //是否是自己设置消失回调
    private var setByThis = false

    //容器
    protected val container: BasePopupWindowContainer by lazy {BasePopupWindowContainer(context)}

    //内容
    protected lateinit var _contentView: View

    //背景
    protected val backgroundView: View by lazy {
        val view = View(context)
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.dialog_background))
        view.setOnClickListener {
            dismiss(true)
        }
        view.layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
        view
    }

    //是否要动画
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

        isFocusable = true
        isTouchable = true
        isOutsideTouchable = true

        setTouchInterceptor { _, event ->
            if (event.action == MotionEvent.ACTION_OUTSIDE && !this@BasePopupWindow.isFocusable) {
                //如果焦点不在popupWindow上，且点击了外面，不再往下dispatch事件：
                if(cancelable){
                    dismiss(true)
                }
                cancelable
            } else false
            //否则default，往下dispatch事件:关掉popupWindow，
        }

        setAnimationStyle(R.anim.anim_no_duration)
        container.addView(backgroundView)

        _contentView = popupContentView

        val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        configLayoutParams(_contentView, params)
        container.addView(_contentView, params)

        contentView = container

        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        if (shouldAnimate) {
            if (container.isLaidOut) {
                executeShowAnimation()
            } else {
                container.visibility = View.INVISIBLE
                container.onLayoutHandler = {
                    executeShowAnimation()
                }
            }
        }else{
            onShow()
        }
    }

    //动画显示弹窗，默认有动画
    fun showAsDropDown(anchor: View, animate: Boolean) {
        shouldAnimate = animate
        showAsDropDown(anchor)
    }

    override fun showAsDropDown(anchor: View) {
        initialize()
        //安卓7.0 以上 会占全屏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val rect = Rect()
            anchor.getGlobalVisibleRect(rect)
            val h: Int = anchor.resources.displayMetrics.heightPixels - rect.bottom
            height = h
        }
        super.showAsDropDown(anchor)
    }

    //显示动画
    private fun executeShowAnimation(){
        container.onLayoutHandler = null

        container.post {
            container.visibility = View.VISIBLE
            when(animationStyle){
                AnimationStyle.SCALE -> {
                    val params = _contentView.layoutParams
                    val animation = ValueAnimator.ofInt(0, _contentView.measuredHeight)
                    animation.duration = 250
                    animation.addUpdateListener {
                        params.height = it.animatedValue as Int
                        _contentView.requestLayout()
                    }
                    animation.start()
                }
                AnimationStyle.TRANSLATE -> {
                    val animation = TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1f,
                        Animation.RELATIVE_TO_SELF, 0f)
                    animation.duration = 250
                    _contentView.startAnimation(animation)
                }
                AnimationStyle.CUSTOM -> executeCustomAnimation(true)
            }

            val alphaAnimation = AlphaAnimation(0f, 1.0f)
            alphaAnimation.duration = 250
            alphaAnimation.setAnimationListener(object: Animation.AnimationListener{
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    onShow()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            backgroundView.startAnimation(alphaAnimation)
        }
    }

    fun dismiss(animate: Boolean) {
        if(animate){
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
            dismiss(true)
        }
    }

    //消失动画
    private fun executeDismissAnimation(){
        when(animationStyle){
            AnimationStyle.SCALE -> {
                val params = _contentView.layoutParams
                val animation = ValueAnimator.ofInt(_contentView.measuredHeight, 0)
                animation.duration = 250
                animation.addUpdateListener {
                    params.height = it.animatedValue as Int
                    _contentView.requestLayout()
                }
                animation.start()
            }
            AnimationStyle.TRANSLATE -> {
                val animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, -1f)
                animation.duration = 250
                _contentView.startAnimation(animation)
            }
            AnimationStyle.CUSTOM -> executeCustomAnimation(false)
        }

        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = 250
        alphaAnimation.setAnimationListener(object: Animation.AnimationListener{
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                dismiss(false)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        backgroundView.startAnimation(alphaAnimation)
    }

    //弹窗已消失
    override fun onDismiss() {
        if (onDismissHandlers.size > 0) {
            for (onDismissHandler in onDismissHandlers) {
                onDismissHandler()
            }
        }
    }

    //弹窗已显示，动画完成后调用
    protected fun onShow() {

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
    abstract val popupContentView: View

    //配置内容视图
    abstract fun configLayoutParams(view: View, params: FrameLayout.LayoutParams)

    //自定义动画
    open fun executeCustomAnimation(isShow: Boolean){

    }

    //弹窗容器
    protected class BasePopupWindowContainer: FrameLayout {

        //布局完成回调，用来执行显示动画的
        var onLayoutHandler: (() -> Unit)? = null

        constructor(context: Context) : super(context)
        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
        constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
            context,
            attrs,
            defStyleAttr
        )


        override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
            super.onLayout(changed, left, top, right, bottom)

            if(onLayoutHandler != null){
                onLayoutHandler!!()
            }
        }
    }
}