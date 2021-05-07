package com.lhx.glakitDemo.home

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
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.dialog.DialogFragment
import com.lhx.glakitDemo.drawable.CornerDrawableFragment
import com.lhx.glakitDemo.section.SectionListFragment
import com.lhx.glakitDemo.section.SectionRecycleViewFragment

class HomeFragment: RecyclerFragment() {

    val items = arrayOf("Drawable", "RecyclerView", "ListView", "Dialog")

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)

        setBarTitle("首页")
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = Adapter(recyclerView)
    }

    private inner class Adapter(recyclerView: RecyclerView): RecyclerViewAdapter(
        recyclerView
    ), ToastContainer {

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            return RecyclerViewHolder(
                LayoutInflater.from(context!!).inflate(
                    R.layout.layout_item,
                    parent,
                    false
                )
            )
        }

        override fun numberOfItems(section: Int): Int {
            return items.size
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.textView).text = items[position]
        }

        override fun onItemClick(positionInSection: Int, section: Int, item: View) {
            when(positionInSection) {
                0 -> {
                    startActivity(CornerDrawableFragment::class.java)
                }
                1-> {
                    startActivity(SectionRecycleViewFragment::class.java)
                }
                2-> {
                    startActivity(SectionListFragment::class.java)
                }
                3-> {
                    startActivity(DialogFragment::class.java)
                }
            }
        }

        override val toastContainer: View
            get() = recyclerView!!
    }
}