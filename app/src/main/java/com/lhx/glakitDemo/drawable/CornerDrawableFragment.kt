package com.lhx.glakitDemo.drawable

import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.drawable.LoadingDrawable
import com.lhx.glakitDemo.R

@Suppress("deprecation")
class CornerDrawableFragment: BaseFragment() {

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        container.setContentView(R.layout.corner_drawable_fragment)
        setBarTitle("Drawable")


//        val height = corner_text.paint.fontMetrics.descent - corner_text.paint.fontMetrics.ascent
//        val html = "<img src='http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg'/> 圆角圆角圆角圆角圆角圆角圆角圆角"
//        corner_text.text = Html.fromHtml(html,
//            {
//                val drawable = CornerBorderDrawable()
//                drawable.borderColor = Color.RED
//                drawable.borderWidth = pxFromDip(10.0f)
//                drawable.shouldAbsoluteCircle = true
//                drawable.backgroundColor = Color.BLUE
//                drawable.intrinsicWidth = height.toInt()
//                drawable.intrinsicHeight = height.toInt()
//                drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//
//                drawable
//            }, null)
//
//        val drawable = CornerBorderDrawable()
//        drawable.borderColor = Color.RED
//        drawable.borderWidth = pxFromDip(10.0f)
//        drawable.shouldAbsoluteCircle = true
//        drawable.backgroundColor = Color.BLUE
//        drawable.intrinsicWidth = 30
//        drawable.intrinsicHeight = 20
//        drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
//        drawable.attachView(corner_text1)
//
//        val loadingDrawable = LoadingDrawable()
//        loadingDrawable.intrinsicWidth = pxFromDip(37.0f)
//        loadingDrawable.intrinsicHeight = pxFromDip(37.0f)
//
//        img_square.setImageDrawable(loadingDrawable)
//        loadingDrawable.start()
    }
}

