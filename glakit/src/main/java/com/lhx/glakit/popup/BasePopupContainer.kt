package com.lhx.glakit.popup

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.VoidCallback
import com.lhx.glakit.extension.*
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.utils.ViewUtils

//基础弹窗容器，系统的有各种限制
open class BasePopupContainer: FrameLayout, PopupAnimation {

    //动画方式
    override var animationStyle = AnimationStyle.TRANSLATE
    override val popupBackgroundView: View
        get() = backgroundView
    override val popupContentView: View?
        get() = _contentView
    override val popupContainer: View
        get() = this

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //背景
    private val backgroundView: View by lazy {
        val view = View(context)
        view.setBackgroundColor(ContextCompat.getColor(context, R.color.dialog_background))
        view.setOnSingleListener {
            dismiss(true)
        }
        view.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        view
    }

    //返回键
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            dismiss(true)
        }
    }

    init {
        addView(backgroundView)
        if (context is AppCompatActivity) {
            val activity = context as AppCompatActivity
            activity.onBackPressedDispatcher.addCallback(onBackPressedCallback)
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBackPressedCallback.isEnabled = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        onBackPressedCallback.isEnabled = false
    }

    //弹窗消失回调
    private val onDismissHandlers: HashSet<VoidCallback> by lazy { HashSet() }

    //内容
    private var _contentView: View? = null

    //添加弹窗消失回调
    fun addOnDismissHandler(onDismissHandler: VoidCallback) {
        onDismissHandlers.add(onDismissHandler)
    }

    //移除
    fun removeOnDismissHandler(onDismissHandler: VoidCallback) {
        onDismissHandlers.remove(onDismissHandler)
    }

    //动画显示弹窗，默认有动画
    fun showAsDropDown(anchor: View, animate: Boolean) {
        if (parent != null) return

        val parent = ViewUtils.findSuitableParent(anchor)
        if (parent != null) {
            parent.addView(this)

            val locations = IntArray(2)
            anchor.getLocationOnScreen(locations)
            if (layoutParams is MarginLayoutParams) {
                val params = layoutParams as MarginLayoutParams
                params.topMargin = locations[1] + anchor.height - SizeUtils.getStatusBarHeight(context)
                layoutParams = params
            }

            if (animate) {
                invisible()
                post {
                    executeShowAnimation()
                }
            } else {
                onPopupShow()
            }
        }
    }

    //设置内容视图
    fun setContentView(view: View) {
        _contentView?.removeFromParent()
        _contentView = view
        addView(view, LayoutParams(MATCH_PARENT, WRAP_CONTENT))
    }

    fun dismiss(animate: Boolean) {
        if (animate) {
            executeDismissAnimation()
        } else {
            removeFromParent()
            for (onDismissHandler in onDismissHandlers) {
                onDismissHandler()
            }
        }
    }

    @CallSuper
    override fun onPopupDismiss() {
        dismiss(false)
    }
}