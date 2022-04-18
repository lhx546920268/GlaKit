package com.lhx.glakit

import android.content.Context
import com.lhx.glakit.loading.LoadingView
import com.lhx.glakit.loading.PageLoadingView
import com.lhx.glakit.refresh.RefreshHeader

//该库 初始化器
object GlaKitConfig {

    //http分页请求第一页
    var HttpFirstPage = 1

    //页面加载类 相关类要忽略混淆
    var pageLoadingViewCreator: ((Context) -> PageLoadingView)? = null
    var loadViewCreator: ((Context) -> LoadingView)? = null

    //自定义下拉刷新头部 要实现 RefreshHeader
    var refreshHeaderCreator: ((Context) -> RefreshHeader)? = null
}

