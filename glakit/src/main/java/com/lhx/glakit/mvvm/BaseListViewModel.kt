package com.lhx.glakit.mvvm

import com.lhx.glakit.GlaKitConfig

/**
 * 列表相关
 */
class BaseListViewModel: BaseViewModel() {

    //页码
    val curPage = GlaKitConfig.HttpFirstPage
}