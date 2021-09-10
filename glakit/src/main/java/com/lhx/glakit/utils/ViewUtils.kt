package com.lhx.glakit.utils

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout

/**
 * 视图相关工具类
 */
@Suppress("deprecation")
object ViewUtils {

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

    /**
     * 寻找合适的父视图，返回activity 根视图，一般用来用来显示loading toast的
     */
    fun findSuitableParent(target: View): ViewGroup? {
        var view: View? = target
        var fallback: ViewGroup? = null
        do {
            if (view is FrameLayout) {
                fallback = if (view.getId() == android.R.id.content) {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return view
                } else {
                    // It's not the content view but we'll use it as our fallback
                    view
                }
            }
            if (view != null) {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                val parent = view.parent
                view = if (parent is View) parent else null
            }
        } while (view != null)

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback
    }
}