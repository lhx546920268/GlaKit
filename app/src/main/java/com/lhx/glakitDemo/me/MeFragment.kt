package com.lhx.glakitDemo.me

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.base.fragment.RecyclerFragment
import com.lhx.glakit.base.widget.BaseContainer

class MeFragment: RecyclerFragment() {

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        super.initialize(inflater, container, saveInstanceState)
        setBarTitle("我的")
    }
}