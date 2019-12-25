package com.lhx.glakit.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lhx.glakit.R

/**
 * 空视图适配器
 */
internal interface EmptyAdapter {

    //空视图
    var emptyView: View?

    //空视图 itemType
    var emptyType: Int

    //空视图下标
    var emptyPosition: Int

    //是否需要显示空视图
    var shouldDisplayEmptyView: Boolean

    //创建空视图如果需要
    fun createEmptyViewIfNeed(parent: ViewGroup){
        if(emptyView == null){
            emptyView = LayoutInflater.from(parent.context).inflate(R.layout.page_empty_view, parent, false)
        }
    }

    //空视图高度 默认和容器一样高 负数
    fun getEmptyViewHeight(): Int {
        return -1
    }

    //空视图已显示
    fun onEmptyViewDisplay(emptyView: View) {

    }
}