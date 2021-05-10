package com.lhx.glakit.base.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer

/**
 * RecyclerView
 */
class RecyclerActivity: RefreshableActivity(){

    val recyclerView: RecyclerView by lazy { findViewById(R.id.recyclerView) }

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

        var res = getRefreshableContentRes()
        if (res <= 0) {
            if (hasRefresh) {
                res = R.layout.recycler_refresh_fragment
            } else {
                res = R.layout.recycler_fragment
            }
        }
        setContainerContentView(res)
        backToTopButton?.recyclerView = recyclerView
    }

    override fun notifyDataSetChanged() {
        recyclerView.adapter?.notifyDataSetChanged()
    }
}