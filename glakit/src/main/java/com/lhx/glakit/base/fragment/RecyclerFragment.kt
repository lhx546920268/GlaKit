package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.widget.StickRecyclerView

/**
 * RecyclerView
 */
open class RecyclerFragment : RefreshableFragment() {

    protected val recyclerView: StickRecyclerView by lazy { requireViewById(R.id.recyclerView) }

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

        var res = getRefreshableContentRes()
        if (res <= 0) {
            if (hasRefresh) {
                res = R.layout.recycler_view_refresh_container
            } else {
                res = R.layout.recycler_view_container
            }
        }
        setContainerContentView(res)
        backToTopButton?.recyclerView = recyclerView
    }

    override fun notifyDataSetChanged() {
        recyclerView.adapter?.notifyDataSetChanged()
    }

    override fun startRefresh() {
        if (smartRefreshLayout != null && !refreshing) {
            if (recyclerView.childCount > 0) {
                recyclerView.scrollToPosition(0)
            }
            smartRefreshLayout!!.autoRefresh()
        }
    }
}