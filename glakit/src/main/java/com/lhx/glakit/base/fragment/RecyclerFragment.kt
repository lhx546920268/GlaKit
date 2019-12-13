package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer
import kotlinx.android.synthetic.main.recycler_fragment.*


/**
 * RecyclerView
 */
open class RecyclerFragment : RefreshableFragment() {

    override fun initialize(inflater: LayoutInflater?, container: BaseContainer, saveInstanceState: Bundle?) {
        super.initialize(inflater, container, saveInstanceState)

        var res = getContentRes()
        if (res <= 0) {
            if (hasRefresh()) {
                res = R.layout.recycler_refresh_fragment
            } else {
                res = R.layout.recycler_fragment
            }
        }
        setContainerContentView(res)

        setRefreshView(recyclerView)

        val backToTopButton = getBackToTopButton()
        backToTopButton?.recyclerView = recyclerView
    }
}