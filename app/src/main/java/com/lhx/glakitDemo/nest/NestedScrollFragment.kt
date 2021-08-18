package com.lhx.glakitDemo.nest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.fragment.RecyclerFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R

class NestedScrollFragment: RecyclerFragment() {

    override fun getRefreshableContentRes(): Int {
        return R.layout.nested_scroll_fragment
    }

    val childRecyclerView: ChildRecyclerView
        get() = recyclerView as ChildRecyclerView

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = Adapter(recyclerView)
    }

    override fun showTitleBar(): Boolean {
        return false
    }

    inner class Adapter(recyclerView: RecyclerView) : RecyclerViewAdapter(recyclerView) {

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return RecyclerViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_item, parent, false))
        }

        override fun numberOfItems(section: Int): Int {
            return 100
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.textView).text = "Child Item $position"
        }
    }
}