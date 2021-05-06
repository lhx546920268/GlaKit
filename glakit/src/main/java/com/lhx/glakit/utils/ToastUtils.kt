package com.lhx.glakit.utils

import android.view.Gravity
import android.view.View
import androidx.annotation.DrawableRes
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.toast.ToastManager

/**
 * Toast 工具类
 */
object ToastUtils {

    fun showToast(view: View, text: CharSequence, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM) {
        ToastManager.sharedManager.show(text, view)
    }

    fun showToast(container: ToastContainer, text: CharSequence, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM) {
        showToast(container.toastContainer, text, icon, gravity)
    }
}