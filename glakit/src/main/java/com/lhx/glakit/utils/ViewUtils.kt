package com.lhx.glakit.utils

import android.graphics.drawable.Drawable
import android.os.Build
import android.view.View
import android.view.ViewGroup

/**
 * 视图相关工具类
 */
@Suppress("deprecation")
object ViewUtils {

    /**
     * 设置背景 兼容api level
     * @param drawable 背景
     * @param view 要设置背景的view
     */
    fun setBackground(drawable: Drawable?, view: View?){

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            view?.background = drawable
        }else{
            view?.setBackgroundDrawable(drawable)
        }
    }

    /**
     * 从父视图移除
     * @param view View? 要移除的view
     */
    fun removeFromParent(view: View?){
        val viewParent = view?.parent
        if(viewParent is ViewGroup){
            viewParent.removeView(view)
        }
    }
}