package com.lhx.glakit.adapter

import android.view.ViewGroup
import com.lhx.glakit.viewholder.RecyclerViewHolder


/**
 * recycler section 适配器
 */
internal interface RecyclerViewSectionAdapter : SectionAdapter {

    /**
     * 创建 header viewHolder
     */
    fun onCreateHeaderViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder{
        throw Exception("${javaClass.name} must impl onCreateHeaderViewHolder")
    }

    /**
     * 给头部绑定数据
     */
    fun onBindHeaderViewHolder(viewHolder: RecyclerViewHolder){}

    /**
     * 创建 footer viewHolder
     */
    fun onCreateFooterViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder{
        throw Exception("${javaClass.name} must impl onCreateFooterViewHolder")
    }

    /**
     * 给底部绑定数据
     */
    fun onBindFooterViewHolder(viewHolder: RecyclerViewHolder){}

    /**
     * 创建 item、header、footer 根据类型来判断 viewHolder
     */
    fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder

    /**
     * 给某个item绑定数据
     */
    fun onBindItemViewHolder(viewHolder: RecyclerViewHolder, position: Int, section: Int)

    /**
     * 给某个section的头部绑定数据
     */
    fun onBindSectionHeaderViewHolder(viewHolder: RecyclerViewHolder, section: Int){}

    /**
     * 给某个section的底部绑定数据
     */
    fun onBindSectionFooterViewHolder(viewHolder: RecyclerViewHolder, section: Int){}
}