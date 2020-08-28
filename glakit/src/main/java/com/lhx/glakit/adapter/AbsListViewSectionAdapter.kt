package com.lhx.glakit.adapter

import android.view.View
import android.view.ViewGroup


/**
 * 列表 section 适配器
 */
internal interface AbsListViewSectionAdapter {

    /**
     * 获取某个section中的头部
     */
    fun getSectionHeader(section: Int, convertView: View?, parent: ViewGroup): View?{
        return null
    }

    /**
     * 获取某个section中的底部
     */
    fun getSectionFooter(section: Int, convertView: View?, parent: ViewGroup): View?{
        return null
    }

    /**
     * 获取某个section中的行视图
     */
    fun getView(position: Int, section: Int, convertView: View?, parent: ViewGroup): View?

    /**
     * getItem 的重写方法
     */
    fun getItem(position: Int, section: Int, type: ItemType): Any?{
        return null
    }

    /**
     * 获取列表item类型的数量,包括sectionHeader 和 footer
     */
    fun numberOfViewTypes(): Int{
        return 1
    }
}