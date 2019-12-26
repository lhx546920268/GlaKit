package com.lhx.glakit.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.DrawableRes
import com.lhx.glakit.R

/**
 * Toast 工具类
 */
object ToastUtils {

    //当前显示的toast
    private var toast: CustomToast? = null


    //如果需要 创建toast
    @Synchronized
    fun createToastIfNeeded(context: Context) {
        if(toast == null){
            toast = CustomToast(context)
        }
    }


    //关闭上一个toast
    fun hideToast() {
        if (toast != null) {
            toast!!.cancel()
            toast = null
        }
    }

    fun showToast(context: Context, text: CharSequence?, @DrawableRes icon: Int = 0, gravity: Int = Gravity.BOTTOM) {

        createToastIfNeeded(context.applicationContext)
        toast?.apply {
            textView.text = text
            imageView.setImageResource(icon)
            setGravity(gravity, 0, 0)
            show()
        }
    }

    //自定义toast
    private class CustomToast (context: Context): Toast(context){

        val textView: TextView
        get() {
           return view.findViewById(R.id.textView)
        }

        val imageView: ImageView
        get() {
            return view.findViewById(R.id.imageView)
        }

        init {
            view = View.inflate(context, R.layout.custom_toast, null)
            duration = LENGTH_SHORT
        }
    }
}