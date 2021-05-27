package com.lhx.glakit.loading

import androidx.annotation.DrawableRes

/**
 * 交互回调
 */
interface InteractionCallback {

    //显示loading
    fun showLoading(delay: Long = 0, text: CharSequence? = null)

    //隐藏loading
    fun hideLoading()

    //提示文字信息
    fun showToast(text: CharSequence)
}