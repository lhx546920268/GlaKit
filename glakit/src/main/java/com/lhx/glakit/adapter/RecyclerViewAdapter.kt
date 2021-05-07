package com.lhx.glakit.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.lhx.glakit.base.constant.Position
import com.lhx.glakit.base.widget.OnSingleClickListener
import com.lhx.glakit.refresh.LoadMoreControl
import com.lhx.glakit.section.SectionInfo
import com.lhx.glakit.viewholder.RecyclerViewHolder
import java.lang.ref.WeakReference


/**
 * Recycler 布局控制器
 */
abstract class RecyclerViewAdapter(recyclerView: RecyclerView) : RecyclerView.Adapter<RecyclerViewHolder>(), ListAdapter, RecyclerViewSectionAdapter {

    companion object{
        const val LOAD_MORE_VIEW_TYPE = 9999 //加载更多视图类型

        const val LOAD_MORE_VIEW_NO_DATA_TYPE = 9998 //加载更多视图没有数据了

        const val EMPTY_VIEW_TYPE = 9997 //空视图类型

        const val HEADER_VIEW_TYPE = 9996 //头部视图类型

        const val FOOTER_VIEW_TYPE = 9995 //底部视图类型
    }

    //关联的
    private val recyclerViewReference = WeakReference(recyclerView)

    protected val recyclerView: RecyclerView?
        get() = recyclerViewReference.get()

    val context: Context?
        get() = recyclerViewReference.get()?.context

    override var totalCount: Int = 0
    override var realCount: Int = 0

    override val sections: ArrayList<SectionInfo> by lazy {
        ArrayList()
    }
    override var shouldReloadData: Boolean = true

    override var loadMoreEnable: Boolean = false
    override var countToTriggerLoadMore: Int = 0

    override var loadMorePosition: Int = Position.NO_POSITION
    override var loadMoreType: Int = LOAD_MORE_VIEW_TYPE
    override var loadMoreNoMoreDataType: Int = LOAD_MORE_VIEW_NO_DATA_TYPE

    override val loadMoreControl: LoadMoreControl by lazy{
        LoadMoreControl()
    }

    override var emptyView: View? = null
    override var emptyType: Int = EMPTY_VIEW_TYPE
    override var emptyPosition: Int = Position.NO_POSITION
    override var shouldDisplayEmptyView: Boolean = true

    override var headerType: Int = HEADER_VIEW_TYPE
    override var footerType: Int = FOOTER_VIEW_TYPE

    init {

        //关闭默认动画
        recyclerView.apply {
            val animator = itemAnimator
            if (animator is DefaultItemAnimator) {
                animator.supportsChangeAnimations = false
            }
        }

        this.setHasStableIds(true)

        //添加数据改变监听
        this.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onChanged() {
                //数据改变，刷新数据
                shouldReloadData = true
            }
        })
    }

    override fun stopLoadMore(hasMore: Boolean) {
        super.stopLoadMore(hasMore)
        notifyDataSetChanged()
    }

    /**
     * 获取item
     */
    fun getItem(positionInSection: Int, section: Int): View? {
        if(recyclerView != null && recyclerView!!.layoutManager != null){
            val sectionInfo = sections[section]
            val position = sectionInfo.getItemStartPosition() + positionInSection
            return recyclerView!!.layoutManager!!.findViewByPosition(position)
        }
        return null
    }

    /**
     * 获取section header
     */
    fun getSectionHeader(section: Int): View? {
        if(recyclerView != null && recyclerView!!.layoutManager != null) {
            val sectionInfo = sections[section]
            val position = sectionInfo.getHeaderPosition()
            return recyclerView!!.layoutManager!!.findViewByPosition(position)
        }
        return null
    }

    /**
     * 获取section footer
     */
    fun getSectionFooter(section: Int): View? {
        if(recyclerView != null && recyclerView!!.layoutManager != null) {
            val sectionInfo = sections[section]
            val position = sectionInfo.getFooterPosition()
            return recyclerView!!.layoutManager!!.findViewByPosition(position)
        }
        return null
    }

    /**
     * 获取item viewHolder
     */
    fun getItemViewHolder(positionInSection: Int, section: Int): RecyclerViewHolder? {
        val item = getItem(positionInSection, section)
        if(item != null){
            return recyclerView!!.getChildViewHolder(item) as RecyclerViewHolder
        }
        return null
    }

    /**
     * 获取header viewHolder
     */
    fun getSectionHeaderViewHolder(section: Int): RecyclerViewHolder? {
        val header = getSectionHeader(section)
        if(header != null){
            return recyclerView!!.getChildViewHolder(header) as RecyclerViewHolder
        }
        return null
    }

    /**
     * 获取footer viewHolder
     */
    fun getSectionFooterViewHolder(section: Int): RecyclerViewHolder? {
        val footer = getSectionFooter(section)
        if(footer != null){
            return recyclerView!!.getChildViewHolder(footer) as RecyclerViewHolder
        }
        return null
    }


    final override fun getItemCount(): Int {
        createSectionsIfNeeded()
        return totalCount
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    final override fun getItemViewType(position: Int): Int {
        return getListItemViewType(position)
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        return when (viewType) {
            LOAD_MORE_VIEW_TYPE, LOAD_MORE_VIEW_NO_DATA_TYPE -> {
                RecyclerViewHolder(getLoadMoreContentView(null, parent))
            }
            EMPTY_VIEW_TYPE -> {
                createEmptyViewIfNeed(parent)
                RecyclerViewHolder(emptyView!!)
            }
            HEADER_VIEW_TYPE -> {
                onCreateHeaderViewHolder(viewType, parent)!!
            }
            FOOTER_VIEW_TYPE -> {
                onCreateFooterViewHolder(viewType, parent)!!
            }
            else -> {
                val holder = onCreateViewHolder(viewType, parent)
                holder.itemView.setOnClickListener(object : OnSingleClickListener() {

                    override fun onSingleClick(v: View) {

                        //添加点击事件
                        val p = holder.bindingAdapterPosition
                        val info: SectionInfo = sectionInfoForPosition(p)!!

                        when{
                            info.isHeaderForPosition(p) -> {
                                onHeaderClick(info.section, v)
                            }
                            info.isFooterForPosition(p) -> {
                                onFooterClick(info.section, v)
                            }
                            else -> {
                                onItemClick(info.getItemPosition(p), info.section, v)
                            }
                        }
                    }
                })
                holder
            }
        }
    }

    final override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        triggerLoadMoreIfNeeded(position)

        when (holder.itemViewType) {
            LOAD_MORE_VIEW_TYPE, LOAD_MORE_VIEW_NO_DATA_TYPE -> {

                if (holder.itemView.layoutParams !is RecyclerView.LayoutParams) {
                    holder.itemView.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                }
            }
            EMPTY_VIEW_TYPE -> {
                val params = if (holder.itemView.layoutParams is RecyclerView.LayoutParams) {
                    holder.itemView.layoutParams
                } else {
                    RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT)
                }

                var height = getEmptyViewHeight()
                if (height < 0) {
                    height = recyclerView!!.height
                }
                params.height = height
                holder.itemView.layoutParams = params
                onEmptyViewDisplay(holder.itemView)
            }
            HEADER_VIEW_TYPE -> {
                onBindHeaderViewHolder(holder)
            }
            FOOTER_VIEW_TYPE -> {
                onBindFooterViewHolder(holder)
            }
            else -> {
                val sectionInfo: SectionInfo = sectionInfoForPosition(position)!!
                when{
                    sectionInfo.isHeaderForPosition(position) -> {
                        onBindSectionHeaderViewHolder(holder, sectionInfo.section)
                    }
                    sectionInfo.isFooterForPosition(position) -> {
                        onBindSectionFooterViewHolder(holder, sectionInfo.section)
                    }
                    else -> {
                        onBindItemViewHolder(holder, sectionInfo.getItemPosition(position), sectionInfo.section)
                    }
                }
            }
        }
    }

    //滚到对应的位置
    fun scrollTo(section: Int) {
        scrollTo(section, false)
    }

    fun scrollTo(section: Int, smooth: Boolean) {
        scrollTo(section, -1, smooth)
    }

    fun scrollTo(section: Int, positionInSection: Int, smooth: Boolean) {
        if (recyclerView != null && section < sections.size) {
            val info = sections[section]
            var position = info.getHeaderPosition()
            if (positionInSection >= 0) {
                position = info.getItemStartPosition() + positionInSection
            }
            if (smooth) {
                recyclerView!!.smoothScrollToPosition(position)
            } else {
                recyclerView!!.scrollToPosition(position)
            }
        }
    }

    //移动到对应位置，如果能置顶则置顶
    fun scrollToWithOffset(positionInSection: Int, offset: Int) {
        scrollToWithOffset(0, positionInSection, offset)
    }

    fun scrollToWithOffset(section: Int, positionInSection: Int, offset: Int) {

        if (recyclerView!!.layoutManager is LinearLayoutManager) {
            val layoutManager =
                recyclerView!!.layoutManager as LinearLayoutManager?
            val info = sections[section]
            var position = info.getHeaderPosition()
            if (positionInSection >= 0) {
                position = info.getItemStartPosition() + positionInSection
            }
            layoutManager!!.scrollToPositionWithOffset(position, offset)
        } else {
            throw RuntimeException("scrollToWithOffset 仅仅支持 LinearLayoutManager")
        }
    }
}