package com.lhx.glakitDemo.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.ItemType
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.fragment.RecyclerFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R

class SectionRecycleViewFragment: RecyclerFragment() {

    override val hasRefresh: Boolean
        get() = true

    private var numberOfSection = 2
    private val adapter: Adapter by lazy { Adapter(recyclerView) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        setBarTitle("RecyclerView")
    }

    override fun onRefresh() {
        recyclerView.postDelayed({
            if(numberOfSection > 0){
                numberOfSection = 0
            }else{
                numberOfSection = 2
            }
            adapter.stopLoadMore(numberOfSection > 0)
            stopRefresh()
        }, 2000)
    }

    private inner class Adapter(recyclerView: RecyclerView): RecyclerViewAdapter(recyclerView) {

        init {
            loadMoreEnable = true
            shouldDisplayEmptyView = true
            stopLoadMore(true)
        }

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return when(viewType){
                ItemType.HEADER.ordinal -> {
                    RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.section_header, parent, false))
                }
                ItemType.FOOTER.ordinal -> {
                    RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.section_footer, parent, false))
                }
                else -> {
                    RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
                }
            }
        }

        override fun numberOfItems(section: Int): Int {
            return 10
        }

        override fun numberOfSections(): Int {
            return numberOfSection
        }

        override fun shouldExistSectionHeader(section: Int): Boolean {
            return true
        }

        override fun shouldExistSectionFooter(section: Int): Boolean {
            return true
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.title).text = "标题-$position"
            viewHolder.getView<TextView>(R.id.subtitle).text = "副标题"
        }

        override fun onBindSectionHeaderViewHolder(viewHolder: RecyclerViewHolder, section: Int) {
            viewHolder.getView<TextView>(R.id.title).text = "Header-$section"
        }

        override fun onBindSectionFooterViewHolder(viewHolder: RecyclerViewHolder, section: Int) {
            viewHolder.getView<TextView>(R.id.title).text = "Footer-$section"
        }

        override fun onLoadMore() {
            recyclerView?.postDelayed({
                numberOfSection ++
                stopLoadMore(true)
            }, 2000)
        }
    }
}