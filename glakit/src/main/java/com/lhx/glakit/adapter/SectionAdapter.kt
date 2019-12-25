package com.lhx.glakit.adapter

import android.view.View
import androidx.annotation.IntDef
import com.lhx.glakit.base.constant.Position

//itemView类型 行
const val ITEM_TYPE_VIEW = 0

//itemView类型 header
const val ITEM_TYPE_HEADER = 1

//itemView类型 footer
const val ITEM_TYPE_FOOTER = 2

@IntDef(ITEM_TYPE_VIEW, ITEM_TYPE_HEADER, ITEM_TYPE_FOOTER)
@Retention(AnnotationRetention.SOURCE)
annotation class ItemType

/**
 * section 适配器
 */
internal interface SectionAdapter {

    //section数量
    fun numberOfSections(): Int{
        return 1
    }

    //每个section中的item数量
    fun numberOfItems(section: Int): Int

    //是否需要section的头部
    fun shouldExistSectionHeader(section: Int): Boolean{
        return false
    }

    //是否需要section的底部
    fun shouldExistSectionFooter(section: Int): Boolean{
        return false
    }

    //getItemId 的重写方法
    fun getItemId(positionInSection: Int, section: Int, @ItemType type: Int): Long{
        return type.toLong()
    }

    //getItemViewType 的重写方法
    fun getItemViewType(positionInSection: Int, section: Int, @ItemType type: Int): Int{
        return type
    }

    //点击item
    fun onItemClick(positionInSection: Int, section: Int, item: View) {}

    //点击头部
    fun onHeaderClick(section: Int, header: View) {}

    //点击底部
    fun onFooterClick(section: Int, footer: View) {}
}