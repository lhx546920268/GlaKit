package com.lhx.glakit.extension

import android.app.Activity
import android.os.Build
import android.util.TypedValue

//判断activity是否透明
fun Activity.isTranslucentOrFloating(): Boolean {
    val translucentValue = TypedValue()
    val floatingValue = TypedValue()
    var isSwipeToDismiss = false

    //TYPE_INT_BOOLEAN 类型的 data的值可能为-1（true）和0（false）
    theme.resolveAttribute(android.R.attr.windowIsTranslucent, translucentValue, true)
    theme.resolveAttribute(android.R.attr.windowIsFloating, floatingValue, true)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        val swipeToDismissValue = TypedValue()
        @Suppress("deprecation")
        theme.resolveAttribute(android.R.attr.windowSwipeToDismiss, swipeToDismissValue, true)
        isSwipeToDismiss = swipeToDismissValue.type == TypedValue.TYPE_INT_BOOLEAN && swipeToDismissValue.data != 0
    }

    val isTranslucent = translucentValue.type == TypedValue.TYPE_INT_BOOLEAN && translucentValue.data != 0
    val isFloating = floatingValue.type == TypedValue.TYPE_INT_BOOLEAN && floatingValue.data != 0

    return isTranslucent || isFloating || isSwipeToDismiss
}