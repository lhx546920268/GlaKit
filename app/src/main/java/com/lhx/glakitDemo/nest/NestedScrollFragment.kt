package com.lhx.glakitDemo.nest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.fragment.RecyclerFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.MATCH_PARENT
import com.lhx.glakit.nested.NestedChildRecyclerView
import com.lhx.glakit.nested.NestedScrollHelper
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R

class NestedScrollFragment: RecyclerFragment() {

    override fun getRefreshableContentRes(): Int {
        return R.layout.nested_scroll_fragment
    }

    var nestedScrollHelper: NestedScrollHelper? = null
    val filterView by lazy { findViewById<View>(R.id.filter)!! }

    var offset: Float = 0f
        set(value) {
            if (value != field) {
                field = value
                filterView.translationY = value
            }
        }

    var onScrollListener: RecyclerView.OnScrollListener? = null
        set(value) {
            field = value
            if (value != null && isInit) {
                recyclerView.addOnScrollListener(value)
            }
        }

    val childRecyclerView: NestedChildRecyclerView
        get() = recyclerView as NestedChildRecyclerView

    var totalDy = 0

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)

        childRecyclerView.nestedScrollHelper = nestedScrollHelper
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = Adapter(recyclerView)

        if (onScrollListener != null) {
            recyclerView.addOnScrollListener(onScrollListener!!)
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                totalDy += dy
            }
        })
    }

    override fun showTitleBar(): Boolean {
        return false
    }

    inner class Adapter(recyclerView: RecyclerView) : RecyclerViewAdapter(recyclerView) {

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false))
        }

        override fun onCreateHeaderViewHolder(
            viewType: Int,
            parent: ViewGroup
        ): RecyclerViewHolder {
            val view = View(context)
            view.layoutParams = RecyclerView.LayoutParams(MATCH_PARENT, pxFromDip(40f))
            return RecyclerViewHolder(view)
        }

        override fun numberOfItems(section: Int): Int {
            return 100
        }

        override fun shouldExistHeader(): Boolean {
            return true
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.textView).text = "Child Item $position"
        }

        override fun onItemClick(position: Int, section: Int, item: View) {
            println("click child")
        }
    }
}