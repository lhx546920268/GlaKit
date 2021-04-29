package com.lhx.glakit.base.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ListView
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.BaseContainer

/**
 * 列表 listView
 */
open class ListFragment : RefreshableFragment() {

    protected lateinit var listView: ListView

    override fun initialize(inflater: LayoutInflater, container: BaseContainer, saveInstanceState: Bundle?) {

        super.initialize(inflater, container, saveInstanceState)
        var res: Int = getContentRes()
        if (res <= 0) {
            if (hasRefresh()) {
                res = R.layout.list_refresh_fragment
            } else {
                res = R.layout.list_fragment
            }
        }

        setContainerContentView(res)
        listView = findViewById(R.id.listView)!!

        setRefreshView(listView)
        val backToTopButton = getBackToTopButton()
        backToTopButton?.listView = listView
    }
}