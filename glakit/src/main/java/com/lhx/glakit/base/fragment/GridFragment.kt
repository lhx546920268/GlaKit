package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer
import kotlinx.android.synthetic.main.grid_fragment.*


/**
 * 网格视图
 */
open class GridFragment : RefreshableFragment() {

    override fun initialize(inflater: LayoutInflater?, container: BaseContainer, saveInstanceState: Bundle?) {

        super.initialize(inflater, container, saveInstanceState)
        var res = getContentRes()
        if (res <= 0) {
            if (hasRefresh()) {
                res = R.layout.grid_refresh_fragment
            } else {
                res = R.layout.grid_fragment
            }
        }

        setContainerContentView(res)

        setRefreshView(gridView)
        val backToTopButton = getBackToTopButton()
        backToTopButton?.listView = gridView
    }
}