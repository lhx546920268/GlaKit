package com.lhx.glakitDemo.pager

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.pager.CyclePageAdapter2
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R

class PagerFragment: BaseFragment() {

    val viewPager: ViewPager2 by lazy { findViewById(R.id.view_pager)!! }
    val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView)!! }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        setContainerContentView(R.layout.pager_fragment)
        val adapter = Adapter(viewPager)
        adapter.shouldAutoPlay = true
        viewPager.adapter = adapter
        viewPager.setPageTransformer(MarginPageTransformer(pxFromDip(10f)))

        PagerSnapHelper().attachToRecyclerView(recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = RAdapter(recyclerView)
    }

    private inner class Adapter(viewPager2: ViewPager2): CyclePageAdapter2(viewPager2) {

        val colors = arrayOf(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.GRAY)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
            return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pager_item, parent, false))
        }

        override fun onBindItem(holder: RecyclerViewHolder, position: Int) {
            holder.getView<TextView>(R.id.text).text = "${position + 1}"
            holder.itemView.setBackgroundColor(colors[position])
        }

        override val realCount: Int
            get() = colors.size

    }

    private inner class RAdapter(recyclerView: RecyclerView) : RecyclerViewAdapter(recyclerView) {

        val colors = arrayOf(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.GRAY)
        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return RecyclerViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.pager_item_2, parent, false))
        }

        override fun numberOfItems(section: Int): Int {
            return colors.size
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.text).text = "${position + 1}"
            viewHolder.itemView.setBackgroundColor(colors[position])
        }
    }
}