package com.lhx.glakit.web

import com.lhx.glakit.utils.StringUtils

/**
 * 网页配置回调
 */
interface WebAdapter {

    //标题改变了
    fun onTitleChanged(title: String)

    //是否添加移动设备头部，用于缩放内容
    fun shouldAddMobileMeta(): Boolean {
        return true
    }

    //加载完成
    fun onPageFinish(url: String?) {}

    //当前url是否可以打开
    fun shouldOpenURL(url: String): Boolean {

        return if (!StringUtils.isEmpty(url)) {
            url.startsWith("http://") || url.startsWith("https://")
        } else true
    }

    //返回需要设置的自定义 userAgent 会拼在系统的userAgent后面
    fun getCustomUserAgent(): String? {
        return null
    }
}