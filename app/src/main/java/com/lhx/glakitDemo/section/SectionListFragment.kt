package com.lhx.glakitDemo.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lhx.glakit.adapter.AbsListViewAdapter
import com.lhx.glakit.adapter.StickAdapter
import com.lhx.glakit.base.constant.Position
import com.lhx.glakit.base.fragment.ListFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.section.SectionInfo
import com.lhx.glakit.viewholder.ViewHolder
import com.lhx.glakitDemo.R

class SectionListFragment: ListFragment(), StickAdapter {

    override val hasRefresh: Boolean
        get() = true

    private var numberOfSection = 2
    private val adapter = Adapter()

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)
        listView.adapter = adapter
        listView.stickAdapter = this
        setBarTitle("ListView")
    }

    override fun getRefreshableContentRes(): Int {
        return R.layout.section_list_fragment
    }

    override fun shouldStickAtPosition(position: Int): Boolean {
        return adapter.sectionInfoForPosition<SectionInfo>(position)?.sectionBegin == position
    }

    override fun getCurrentStickPosition(firstVisibleItem: Int, stickPosition: Int): Int {
        return adapter.sectionInfoForPosition<SectionInfo>(firstVisibleItem)?.sectionBegin ?: Position.NO_POSITION
    }

    override fun onRefresh() {
        listView.postDelayed({
            if(numberOfSection > 0){
                numberOfSection = 0
            }else{
                numberOfSection = 2
            }
            adapter.stopLoadMore(numberOfSection > 0)
            stopRefresh()
        }, 2000)
    }

    private inner class Adapter: AbsListViewAdapter() {

        init {
            loadMoreEnable = true
            shouldDisplayEmptyView = true
            stopLoadMore(true)
        }

        override fun getView(
            position: Int,
            section: Int,
            convertView: View?,
            parent: ViewGroup
        ): View {
            var view = convertView
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
            }

            return view!!.apply {
                ViewHolder.get<TextView>(this, R.id.title).text = "Item-$position"
            }
        }

        override fun getSectionHeader(section: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.section_header, parent, false)
            }

            return view!!.apply {
                ViewHolder.get<TextView>(this, R.id.title).text = "Header -$section"
            }
        }

        override fun getSectionFooter(section: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.section_footer, parent, false)
            }

            return view!!.apply {
                ViewHolder.get<TextView>(this, R.id.title).text = "Footer -$section"
            }
        }

        override fun shouldExistSectionHeader(section: Int): Boolean {
            return true
        }

        override fun onHeaderClick(section: Int, header: View) {
            println("header click")
        }

        override fun numberOfItems(section: Int): Int {
            return 10
        }

        override fun numberOfSections(): Int {
            return numberOfSection
        }

        override fun onLoadMore() {
            listView.postDelayed({
                numberOfSection ++
                stopLoadMore(true)
            }, 2000)
        }
    }
}