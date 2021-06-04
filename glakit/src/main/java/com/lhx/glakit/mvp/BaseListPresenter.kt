package com.lhx.glakit.mvp

import com.lhx.glakit.GlaKitInitializer

/**
 * 列表相关
 */
class BaseListPresenter<T>(owner: T): BasePresenter<T>(owner) {

    //页码
    val curPage = GlaKitInitializer.HttpFirstPage
}