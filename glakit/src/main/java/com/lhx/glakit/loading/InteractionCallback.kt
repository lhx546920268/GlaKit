package com.lhx.glakit.loading

/**
 * 交互回调
 */
interface InteractionCallback {

    //显示loading
    fun showLoading(delay: Int = 0)

    //隐藏loading
    fun hideLoading()

    //提示文字信息
    fun showText(text: CharSequence)
}