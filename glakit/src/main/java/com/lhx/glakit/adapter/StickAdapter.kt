package com.lhx.glakit.adapter

//悬浮固定item适配器
interface StickAdapter {

    //是否需要悬浮固定
    fun shouldStickAtPosition(position: Int): Boolean

    //根据当前第一个可见item获取当前可悬浮固定的item
    fun getCurrentStickPosition(firstVisibleItem: Int): Int
}