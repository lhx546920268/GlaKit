package com.lhx.glakit.adapter

import com.lhx.glakit.base.constant.Position
import com.lhx.glakit.refresh.LoadMoreStatus
import com.lhx.glakit.section.SectionInfo

/**
 * 列表适配器
 */
@Suppress("unchecked_cast")
internal interface ListAdapter: LoadMoreAdapter, EmptyAdapter, SectionAdapter {

    /**
     * item总数
     */
    var totalCount: Int

    /**
     * 数据源item的数量
     */
    var realCount: Int

    /**
     * section信息数组
     */
    val sections: ArrayList<out SectionInfo>

    /**
     * 是否需要刷新数据
     */
    var shouldReloadData: Boolean

    /**
     * 列表头部 itemType
     */
    var headerType: Int

    /**
     * 列表底部 itemType
     */
    var footerType: Int

    //创建section 如果需要
    fun createSectionsIfNeeded() {
        if (shouldReloadData) {
            shouldReloadData = false
            sections.clear()

            //计算列表行数量
            val numberOfSection = numberOfSections()
            var count = 0

            if(shouldExistHeader()){
                count ++
            }

            for (section in 0 until numberOfSection) {
                val numberOfItems = numberOfItems(section)

                //保存section信息
                val sectionInfo = createSectionInfo(section, numberOfItems, count)
                sections + sectionInfo

                count += numberOfItems
                if (sectionInfo.isExistHeader) count++
                if (sectionInfo.isExistFooter) count++
            }

            if(shouldExistFooter()){
                count ++
            }

            realCount = count
            if (realCount == 0 && shouldDisplayEmptyView && displayEmptyViewEnable()) {
                emptyPosition = count
                count++
            } else {
                emptyPosition = Position.NO_POSITION
            }
            if (loadMoreEnableForData(realCount) && loadMoreEnable && shouldDisplay()) {
                loadMorePosition = count
                count++
            } else {
                loadMorePosition = Position.NO_POSITION
            }
            totalCount = count
        }
    }

    //创建sectionInfo
    fun createSectionInfo(section: Int, numberOfItems: Int, position: Int): SectionInfo{
        val sectionInfo = SectionInfo()
        sectionInfo.section = section
        sectionInfo.numberItems = numberOfItems
        sectionInfo.isExistHeader = shouldExistSectionHeader(section)
        sectionInfo.isExistFooter = shouldExistSectionFooter(section)
        sectionInfo.sectionBegin = position

        return sectionInfo
    }

    //通过position获取对应的sectionInfo
    fun <T : SectionInfo> sectionInfoForPosition(position: Int): T? {
        if (sections.size == 0)
            return null

        var info = sections[0]
        for (i in 1 until sections.size) {
            val sectionInfo = sections[i]
            info = if (sectionInfo.sectionBegin > position) {
                break
            } else {
                sectionInfo
            }
        }
        return info as T
    }

    //如果可能 触发加载更多
    fun triggerLoadMoreIfNeeded(position: Int) {
        if (loadMoreEnable && loadMoreControl.loadingStatus == LoadMoreStatus.HAS_MORE) {
            if (realCount - position - 1 <= countToTriggerLoadMore) {
                startLoadMore()
            }
        }
    }

    //是否是加载更多的 item
    fun isLoadMoreItem(position: Int): Boolean {
        return loadMoreEnable && loadMoreEnableForData(realCount) && position == loadMorePosition && shouldDisplay()
    }

    //是否是空视图 item
    fun isEmptyItem(position: Int): Boolean {
        return shouldDisplayEmptyView && realCount == 0 && emptyPosition == position
    }

    //getItemId 的重写方法
    fun getListItemId(position: Int): Long {
        val sectionInfo: SectionInfo? = sectionInfoForPosition(position)
        return when {
            isEmptyItem(position) || isLoadMoreItem(position) -> {
                position.toLong()
            }
            sectionInfo!!.isHeaderForPosition(position) -> {
                //存在头部
                getItemId(0, sectionInfo.section, ItemType.HEADER)
            }
            sectionInfo.isFooterForPosition(position) -> {
                //存在底部
                getItemId(0, sectionInfo.section, ItemType.FOOTER)
            }
            else -> {
                getItemId(sectionInfo.getItemPosition(position), sectionInfo.section, ItemType.VIEW)
            }
        }
    }

    //getItemViewType 的重写方法
    fun getListItemViewType(position: Int): Int {
        val sectionInfo: SectionInfo? = sectionInfoForPosition(position)
        return when{
            isHeader(position) -> {
                headerType
            }
            isFooter(position) -> {
                footerType
            }
            isEmptyItem(position) -> {
                emptyType
            }
            isLoadMoreItem(position) -> {
                if (hasMore()) loadMoreType else loadMoreNoMoreDataType
            }
            sectionInfo!!.isHeaderForPosition(position) -> {
                //存在头部
                getItemViewType(0, sectionInfo.section, ItemType.HEADER)
            }
            sectionInfo.isFooterForPosition(position) -> {
                //存在底部
                getItemViewType(0, sectionInfo.section, ItemType.FOOTER)
            }
            else -> {
                getItemViewType(sectionInfo.getItemPosition(position), sectionInfo.section, ItemType.VIEW)
            }
        }
    }

    //是否存在列表头部
    fun shouldExistHeader(): Boolean {
        return false
    }

    //是否是头部
    fun isHeader(position: Int): Boolean {
        return shouldExistHeader() && position == 0
    }

    //是否存在列表底部
    fun shouldExistFooter(): Boolean {
        return false
    }

    //是否是底部
    fun isFooter(position: Int): Boolean{
        return shouldExistFooter() && position == totalCount - 1
    }
}