package com.lhx.glakitDemo.section

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.lhx.glakit.adapter.AbsListViewAdapter
import com.lhx.glakit.base.fragment.ListFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.viewholder.ViewHolder
import com.lhx.glakitDemo.R

class SectionListFragment: ListFragment() {

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
        setBarTitle("ListView")
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
                ViewHolder.get<TextView>(this, R.id.title).text = "Item-$section"
            }
        }

        override fun getSectionFooter(section: Int, convertView: View?, parent: ViewGroup): View {
            var view = convertView
            if(view == null){
                view = LayoutInflater.from(context).inflate(R.layout.section_footer, parent, false)
            }

            return view!!.apply {
                ViewHolder.get<TextView>(this, R.id.title).text = "Item-$section"
            }
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