package com.lhx.glakit.dialog

import androidx.annotation.IntDef

//信息弹窗样式
object AlertStyle {

    const val ALERT = 0 //在中间显示的

    const val ACTION_SHEET = 1 //在底部弹出的

    //弹窗样式
    @IntDef(ALERT, ACTION_SHEET)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Style
}