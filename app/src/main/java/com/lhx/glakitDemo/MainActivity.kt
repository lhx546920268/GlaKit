package com.lhx.glakitDemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.*
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.drawable.CornerDrawableFragment
import java.lang.ref.WeakReference

@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    val items = arrayOf("Drawable")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
//        titleBar.setTitle("这是一个标题")
//        titleBar.setLeftItem("返回", null)
//        titleBar.setRightItem("完成", null)
//
//
//        recyclerView.layoutManager = TetrisLayoutManager()
//        recyclerView.adapter = Adapter(WeakReference(recyclerView))
    }

    private inner class Adapter(recyclerViewReference: WeakReference<RecyclerView>): RecyclerViewAdapter(
        recyclerViewReference
    ) {

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
//            viewHolder.itemView.textView.text = items[position]
        }

        override fun onItemClick(positionInSection: Int, section: Int, item: View) {
            when(positionInSection) {
                0 -> {
                    startActivity(BaseActivity.getIntentWithFragment(context!!, CornerDrawableFragment::class.java))
                }
            }
        }
    }
}