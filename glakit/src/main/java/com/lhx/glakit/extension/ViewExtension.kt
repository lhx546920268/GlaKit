package com.lhx.glakit.extension

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.annotation.ColorInt
import com.lhx.glakit.base.widget.OnSingleClickListener
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.utils.ViewUtils


/**
 * 视图扩展
 */

const val MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT
const val WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT

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

fun View.isVisible(): Boolean {
    return visibility == View.VISIBLE
}

fun View.removeFromParent() {
    ViewUtils.removeFromParent(this)
}

//设置圆角边框
fun View.setDrawable(cornerRadius: Int,
                     @ColorInt backgroundColor: Int,
                     borderWidth: Int = 0,
                     @ColorInt borderColor: Int = 0): CornerBorderDrawable {
    val drawable = CornerBorderDrawable()
    drawable.setCornerRadius(cornerRadius)
    drawable.backgroundColor = backgroundColor
    drawable.borderWidth = borderWidth
    drawable.borderColor = borderColor
    drawable.attachView(this)
    return drawable
}

fun View.setCircleDrawable(@ColorInt backgroundColor: Int,
                      borderWidth: Int = 0, @ColorInt borderColor: Int = Color.TRANSPARENT): CornerBorderDrawable {
    val drawable = CornerBorderDrawable()
    drawable.shouldAbsoluteCircle = true
    drawable.backgroundColor = backgroundColor
    drawable.borderWidth = borderWidth
    drawable.borderColor = borderColor
    drawable.attachView(this)

    return drawable
}