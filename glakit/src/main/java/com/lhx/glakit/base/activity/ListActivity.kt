package com.lhx.glakit.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.widget.ListView
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer

/**
 * 列表 listView
 */
class ListActivity: RefreshableActivity() {

    val listView: ListView by lazy { findViewById(R.id.listView) }

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

        var res: Int = getRefreshableContentRes()
        if (res <= 0) {
            if (hasRefresh) {
                res = R.layout.list_refresh_fragment
            } else {
                res = R.layout.list_fragment
            }
        }

        setContainerContentView(res)
        backToTopButton?.listView = listView
    }

    override fun notifyDataSetChanged() {
        (listView.adapter as BaseAdapter?)?.notifyDataSetChanged()
    }
}