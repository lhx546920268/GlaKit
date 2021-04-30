package com.lhx.glakit.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.google.android.material.snackbar.Snackbar
import com.lhx.glakit.R
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.widget.ToastManager

/**
 * Toast 工具类
 */
object ToastUtils {

    fun showToast(context: Context, text: CharSequence, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM){

    }

    fun showToast(view: View, text: CharSequence, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM) {
        ToastManager.sharedManager.show(text, view)
    }

    fun showToast(container: ToastContainer, text: CharSequence, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM) {
        showToast(container.toastContainer, text, icon, gravity)
    }
}