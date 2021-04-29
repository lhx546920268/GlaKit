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

/**
 * Toast 工具类
 */
object ToastUtils {

    fun showToast(container: ToastContainer, text: CharSequence, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM) {

        Snackbar.make(container.toastContainer, text, Snackbar.LENGTH_SHORT)
//        toast?.apply {
//            textView.text = text
//            imageView.setImageResource(icon)
//            setGravity(gravity, 0, 0)
//            show()
//        }
    }

    //自定义toast
//    private class CustomToast (context: Context): Toast(context){
//
//        val textView: TextView
//            get() => cont
//        get() {
//           return view.findViewById(R.id.textView)
//        }
//
//        val imageView: ImageView
//        get() {
//            return view.findViewById(R.id.imageView)
//        }
//
//        init {
//            view = View.inflate(context, R.layout.custom_toast, null)
//            duration = LENGTH_SHORT
//        }
//    }
}