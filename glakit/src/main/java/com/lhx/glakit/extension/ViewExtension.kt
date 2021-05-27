package com.lhx.glakit.extension

import android.view.View
import com.lhx.glakit.base.widget.OnSingleClickListener


/**
 * 视图扩展
 */

/**
 * 防止多次点击
 */
fun View.setOnSingleListener(callback: (v: View) -> Unit) {

    setOnClickListener(object : OnSingleClickListener() {
        override fun onSingleClick(v: View) {
            callback(v)
        }
    })
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.gone() {
    visibility = View.GONE
}