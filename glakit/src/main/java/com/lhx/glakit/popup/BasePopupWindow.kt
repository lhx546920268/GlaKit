package com.lhx.glakit.popup

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.annotation.IdRes
import androidx.core.content.ContextCompat
import com.lhx.glakit.R


/**
 * 基础弹窗
 */
abstract class BasePopupWindow(val context: Context) : PopupWindow(), PopupWindow.OnDismissListener {

    //点击弹窗以外的位置是否关闭弹窗
    var cancelable = true

    //弹窗消失回调
    private val onDismissHandlers: HashSet<OnDismissHandler> by lazy { HashSet() }

    //是否是自己设置消失回调
    private var setByThis = false

    //添加弹窗消失回调
    fun addOnDismissHandler(onDismissHandler: OnDismissHandler) {
        onDismissHandlers.add(onDismissHandler)
    }

    //移除
    fun removeOnDismissHandler(onDismissHandler: OnDismissHandler) {
        onDismissHandlers.remove(onDismissHandler)
    }

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
                    dismiss()
                }
                cancelable
            } else false
            //否则default，往下dispatch事件:关掉popupWindow，
        }

        animationStyle = R.anim.anim_no
        contentView = popupContentView

        width = ViewGroup.LayoutParams.MATCH_PARENT
        height = ViewGroup.LayoutParams.MATCH_PARENT
        setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(context, R.color.dialog_background)))
    }

    override fun onDismiss() {
        if (onDismissHandlers.size > 0) {
            for (onDismissHandler in onDismissHandlers) {
                onDismissHandler.onDismiss()
            }
        }
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

    ///获取子视图
    fun <T : View> findViewById(@IdRes id: Int): T? {
        return contentView?.findViewById(id)
    }

    fun <T : View> requireViewById(@IdRes id: Int): T {
        return findViewById(id)
            ?: throw IllegalArgumentException("ID does not reference a View inside this View")
    }

    ///获取内容视图
    abstract val popupContentView: View

    //弹窗消失回调
    interface OnDismissHandler {
        //弹窗消失
        fun onDismiss()
    }
}