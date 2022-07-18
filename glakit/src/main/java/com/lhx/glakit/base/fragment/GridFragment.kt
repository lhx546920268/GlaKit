package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.BaseAdapter
import android.widget.GridView
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer


/**
 * 网格视图
 */
open class GridFragment : RefreshableFragment() {

    protected val gridView: GridView by lazy { requireViewById(R.id.gridView) }

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {
        var res = getRefreshableContentRes()
        if (res <= 0) {
            if (hasRefresh) {
                res = R.layout.grid_view_refresh_container
            } else {
                res = R.layout.grid_view_container
            }
        }

        setContainerContentView(res)
        backToTopButton?.listView = gridView
    }

    override fun notifyDataSetChanged() {
        (gridView.adapter as BaseAdapter?)?.notifyDataSetChanged()
    }

    override fun startRefresh() {
        if (smartRefreshLayout != null && !refreshing) {
            if (gridView.childCount > 0) {
                gridView.setSelection(0)
            }
            smartRefreshLayout!!.autoRefresh()
        }
    }
}