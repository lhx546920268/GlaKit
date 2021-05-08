package com.lhx.glakitDemo.dialog

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.popup.BasePopupWindow
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R

class ListPopupWindow(context: Context): BasePopupWindow(context) {

    private val recyclerView: RecyclerView by lazy {
        val view = RecyclerView(context)
        view.layoutManager = LinearLayoutManager(context)
        view.setBackgroundColor(Color.RED)
        view.adapter = Adapter(view)
        view
    }

    init {
        animationStyle = AnimationStyle.TRANSLATE
    }

    override val popupContentView: View
        get() = recyclerView

    override fun configLayoutParams(view: View, params: FrameLayout.LayoutParams) {

    }

    private inner class Adapter(recyclerView: RecyclerView) :
        RecyclerViewAdapter(recyclerView) {

        val items = arrayOf("首页", "分类", "购物车", "我的")

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.section_header, parent, false)
            view.layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
            return RecyclerViewHolder(view)
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.title).apply {
                text = items[position]
            }
        }

        override fun numberOfItems(section: Int): Int {
            return items.count()
        }

        override fun onItemClick(positionInSection: Int, section: Int, item: View) {
            dismiss()
        }
    }
}