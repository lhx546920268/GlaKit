package com.lhx.glakit.utils

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import com.lhx.glakit.R
import com.lhx.glakit.base.activity.ActivityLifeCycleManager
import com.lhx.glakit.base.interf.VoidCallback
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.toast.ToastManager

/**
 * Toast 工具类
 */
object ToastUtils {

    private val context: Context
        get() = ActivityLifeCycleManager.currentContext

    fun showToast(view: View, text: CharSequence) {
        ToastManager.show(text, view)
    }

    fun showToast(container: ToastContainer, text: CharSequence) {
        showToast(container.toastContainer, text)
    }

    fun showToast(@StringRes text: Int, mask: Boolean = false, dismissCallback: VoidCallback? = null) {
        val context = ActivityLifeCycleManager.currentContext
        if (context is Activity) {
            try {
                val view = context.window.decorView.findViewById<FrameLayout>(android.R.id.content)
                ToastManager.show(context.getString(text), view, mask, dismissCallback)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun showToast(text: CharSequence, mask: Boolean = false, dismissCallback: VoidCallback? = null) {
        val context = ActivityLifeCycleManager.currentContext
        if (context is Activity) {
            try {
                val view = context.window.decorView.findViewById<FrameLayout>(android.R.id.content)
                ToastManager.show(text, view, mask, dismissCallback)
            }catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    //当前显示的toast
    private var currentToast: Toast? = null

    //获取toast
    private fun getToast(context: Context): Toast {
        close()
        currentToast = Toast(context)
        return currentToast!!
    }


    //关闭上一个toast
    private fun close() {
        if (currentToast != null) {
            currentToast!!.cancel()
            currentToast = null
        }
    }

    @Suppress("deprecation")
    fun show(text: CharSequence) {
        currentToast = getToast(context)
        val view = View.inflate(context, R.layout.toast_layout, null)
        val textView = view.findViewById<View>(R.id.text) as TextView
        textView.text = text
        currentToast?.apply {
            setGravity(Gravity.CENTER, 0, 0)
            setView(view)
            duration = Toast.LENGTH_SHORT
            show()
        }
    }
}