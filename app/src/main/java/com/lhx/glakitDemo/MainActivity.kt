package com.lhx.glakitDemo

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.layout.TetrisLayoutManager
import com.lhx.glakit.viewholder.RecyclerViewHolder
import kotlinx.android.synthetic.main.layout_item.view.*
import kotlinx.android.synthetic.main.main_activity.*
import java.lang.ref.WeakReference


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        titleBar.setTitle("这是一个标题")
        titleBar.setLeftItem("返回", null)
        titleBar.setRightItem("完成", null)


        recyclerView.layoutManager = TetrisLayoutManager()
        recyclerView.adapter = Adapter(WeakReference(recyclerView))
    }

    private inner class Adapter(recyclerViewReference: WeakReference<RecyclerView>): RecyclerViewAdapter(
        recyclerViewReference
    ) {

        var selectedPosition: Int = 0
        set(value) {
            if(field != value){
                field = value
                notifyDataSetChanged()
                Handler().postDelayed({
                    recyclerView?.smoothScrollToPosition(selectedPosition)
                }, 100)
            }
        }

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
            return 1
        }

        @SuppressLint("SetTextI18n")
        override fun onBindItemViewHolder(
            viewHolder: RecyclerViewHolder,
            position: Int,
            section: Int
        ) {
            viewHolder.itemView.textView.text = "index $position"
            viewHolder.itemView.textView.setTextColor(if (position == selectedPosition) Color.RED else Color.BLACK)
        }

        override fun onItemClick(positionInSection: Int, section: Int, item: View) {
            selectedPosition = positionInSection
        }
    }
}