package com.lhx.glakit.refresh

/**
 * 加载更多状态
 */
object LoadMoreStatus {

    const val NORMAL = 0 //什么都没

    const val HAS_MORE = 1 //可以加载更多数据

    const val LOADING = 2 //加载中

    const val FAIL = 3 //加载失败 点击可加载

    const val NO_MORE_DATA = 4 //没有数据了
}