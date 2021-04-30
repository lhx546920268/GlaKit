package com.lhx.glakitDemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.*
import com.lhx.glakit.adapter.RecyclerViewAdapter
import com.lhx.glakit.base.activity.BaseActivity
import com.lhx.glakit.toast.ToastContainer
import com.lhx.glakit.utils.ToastUtils
import com.lhx.glakit.viewholder.RecyclerViewHolder
import com.lhx.glakitDemo.databinding.MainActivityBinding
import com.lhx.glakitDemo.drawable.CornerDrawableFragment
import java.lang.ref.WeakReference

@SuppressLint("SetTextI18n")
class MainActivity : BaseActivity() {

    val viewBinding: MainActivityBinding by lazy { MainActivityBinding.bind(currentContentView!!)}

    val items = arrayOf("Drawable", "Dialog")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding.apply {
            titleBar.setTitle("这是一个标题")
            titleBar.setLeftItem("返回", null)
            titleBar.setRightItem("完成", null)

            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            recyclerView.adapter = Adapter(WeakReference(recyclerView))
        }
    }

    override fun getContentViewRes(): Int {
        return R.layout.main_activity
    }

    private inner class Adapter(recyclerViewReference: WeakReference<RecyclerView>): RecyclerViewAdapter(
        recyclerViewReference
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
                    startActivity(getIntentWithFragment(context!!, CornerDrawableFragment::class.java))
                }
                1-> {
                    ToastUtils.showToast(item, "I am a Toast")
                }
            }
        }

        override val toastContainer: View
            get() = recyclerView!!
    }
}