package com.lhx.glakitDemo.nest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.ItemType
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.activity.RecyclerActivity
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R

//嵌套滑动
class NestedScrollActivity: RecyclerActivity() {

    override val hasRefresh: Boolean
        get() = true

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)
        setBarTitle("嵌套滑动")
        val layoutManager = ParentLayoutManager(this)
        layoutManager.callback = {
            childContainer?.getChildRecyclerView()
        }
        (recyclerView as ParentRecyclerView).callback = {
            childContainer?.getChildRecyclerView()
        }
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = Adapter(recyclerView)
    }

    override fun onRefresh() {
        baseContainer?.postDelayed({
            stopRefresh()
        }, 1000)
    }

    private var childContainer: NestedScrollChildContainer? = null

    override fun getRefreshableContentRes(): Int {
        return R.layout.nested_scroll_activity
    }

    inner class Adapter(recyclerView: RecyclerView) : RecyclerViewAdapter(recyclerView) {

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return RecyclerViewHolder(LayoutInflater.from(context).inflate(viewType, parent, false))
        }

        override fun getItemViewType(position: Int, section: Int, type: ItemType): Int {
            return if (position == 9) R.layout.nested_scroll_item else R.layout.layout_item
        }

        override fun numberOfItems(section: Int): Int {
            return 10
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            if (position != 9) {
                viewHolder.getView<TextView>(R.id.textView).text = "Item $position"
            } else {
                val item = viewHolder.itemView as NestedScrollItem
                if (childContainer == null) {
                    childContainer = LayoutInflater.from(context).inflate(R.layout.nested_scroll_child_container, null) as NestedScrollChildContainer
                }
                item.setView(childContainer!!)
            }
        }

        override fun onItemClick(position: Int, section: Int, item: View) {
            println("click parent")
        }
    }
}