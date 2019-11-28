package com.lhx.glakit.base.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Property
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import kotlin.reflect.KProperty

//标题栏
class TitleBar: RelativeLayout {

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    //标题
    private var titleTextView: TextView? = null

    //左边按钮
    private var letView: View? = null

    //右边按钮
    private var rightView: View? = null

    //设置标题
    fun setTitle(title: String?) {

        if(titleTextView == null){
            val textView = TextView(this.context)
            textView.textSize = 18.0f
            textView.setTextColor(Color.BLACK)
        }

        titleTextView!!.text = title
    }
}