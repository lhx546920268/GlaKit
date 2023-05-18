package com.lhx.glakit.extension

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat


/**
 * 获取着色的 drawable
 * @param tintColor 对应颜色
 * @return 着色后的drawable
 */
fun Drawable.getTintDrawable(@ColorInt tintColor: Int) : Drawable {
    val wrapDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(wrapDrawable, tintColor)
    return wrapDrawable
}

fun Drawable.getTintListDrawable(color: ColorStateList) : Drawable {
    val wrapDrawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTintList(wrapDrawable, color)
    return wrapDrawable
}