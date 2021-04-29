package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.GridView
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer


/**
 * 网格视图
 */
open class GridFragment : RefreshableFragment() {

    protected lateinit var gridView: GridView

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

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
        gridView = findViewById(R.id.gridView)!!

        setRefreshView(gridView)
        val backToTopButton = getBackToTopButton()
        backToTopButton?.listView = gridView
    }
}