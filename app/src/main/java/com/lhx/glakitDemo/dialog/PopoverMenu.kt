package com.lhx.glakitDemo.dialog

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.popover.PopoverContainer
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.R


class PopoverMenu: PopoverContainer {

    private val recyclerView: RecyclerView by lazy {
        val view = RecyclerView(context)
        view.layoutManager = LinearLayoutManager(context)
        view
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){

        val layout = popoverLayout
        layout.popoverColor = Color.BLACK
        layout.cornerRadius = SizeUtils.pxFormDip(10.0f, context)

        val padding: Int = SizeUtils.pxFormDip(20.0f, context)
        setPadding(padding, SizeUtils.pxFormDip(2.0f, context), padding, padding)
        recyclerView.adapter = Adapter(recyclerView)

        setContentView(recyclerView)
    }

    private inner class Adapter(recyclerView: RecyclerView) :
        RecyclerViewAdapter(recyclerView) {

        val items = arrayOf("首页", "分类", "购物车", "我的")

        override fun onCreateViewHolder(viewType: Int, parent: ViewGroup): RecyclerViewHolder {
            val view = LayoutInflater.from(context).inflate(R.layout.section_header, parent, false)
            view.layoutParams.width = LayoutParams.WRAP_CONTENT
            view.setBackgroundColor(Color.TRANSPARENT)
            return RecyclerViewHolder(view)
        }

        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.getView<TextView>(R.id.title).apply {
                text = items[position]
                setTextColor(Color.WHITE)
            }
        }

        override fun numberOfItems(section: Int): Int {
            return items.count()
        }

        override fun onItemClick(position: Int, section: Int, item: View) {
            dismiss(true)
        }
    }
}