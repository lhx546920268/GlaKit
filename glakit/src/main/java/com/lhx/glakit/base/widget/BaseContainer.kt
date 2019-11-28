package com.lhx.glakit.base.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.RelativeLayout

//基础视图容器
class BaseContainer: RelativeLayout {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //标题栏
    private var mTitleBar: TitleBar? = null

    //头部视图
    private var mTopView: View? = null

    //底部视图
    private var mBottomView: View? = null



}