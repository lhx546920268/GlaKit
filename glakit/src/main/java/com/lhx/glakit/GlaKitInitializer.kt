package com.lhx.glakit

import android.view.View
import com.lhx.glakit.loading.LoadingView
import com.lhx.glakit.refresh.RefreshHeader

//该库 初始化器
object GlaKitInitializer {

    //http分页请求第一页
    var HttpFirstPage = 1

    //页面加载类
    var pageLoadingViewClass: Class<out View>? = null
    var loadViewClass: Class<out LoadingView?>? = null

    //自定义下拉刷新头部 要实现 RefreshHeader
    var refreshHeaderClass: Class<out RefreshHeader>? = null
}

