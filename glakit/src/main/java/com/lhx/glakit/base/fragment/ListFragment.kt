package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.BaseAdapter
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.widget.StickListView

/**
 * 列表 listView
 */
open class ListFragment : RefreshableFragment() {

    protected val listView: StickListView by lazy { requireViewById(R.id.listView) }

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

        var res: Int = getRefreshableContentRes()
        if (res <= 0) {
            if (hasRefresh) {
                res = R.layout.list_view_refresh_container
            } else {
                res = R.layout.list_view_container
            }
        }

        setContainerContentView(res)
        backToTopButton?.listView = listView
    }

    override fun notifyDataSetChanged() {
        (listView.adapter as BaseAdapter?)?.notifyDataSetChanged()
    }

    override fun startRefresh() {
        if (smartRefreshLayout != null && !refreshing) {
            if (listView.childCount > 0) {
                listView.setSelection(0)
            }
            smartRefreshLayout!!.autoRefresh()
        }
    }
}