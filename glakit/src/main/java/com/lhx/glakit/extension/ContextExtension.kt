package com.lhx.glakit.extension

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat

//获取颜色 兼容
@ColorInt
fun Context.getColorCompat(@ColorRes res: Int): Int {
    return ContextCompat.getColor(this, res)
}

fun Context.getDrawableCompat(@DrawableRes res: Int): Drawable? {
    return ContextCompat.getDrawable(this, res)
}