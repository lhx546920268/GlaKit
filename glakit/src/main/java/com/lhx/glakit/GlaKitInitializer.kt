package com.lhx.glakit

import android.view.View
import com.lhx.glakit.loading.LoadingView

//该库 初始化器
object GlaKitInitializer {

    //页面加载类
    var pageLoadingViewClass: Class<out View>? = null
    var loadViewClass: Class<out LoadingView?>? = null
}