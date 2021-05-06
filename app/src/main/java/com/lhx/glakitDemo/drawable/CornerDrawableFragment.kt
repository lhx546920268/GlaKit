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
import com.lhx.glakitDemo.databinding.CornerDrawableFragmentBinding

@Suppress("deprecation")
class CornerDrawableFragment: BaseFragment() {

    val viewBinding by lazy { CornerDrawableFragmentBinding.bind(getContainerContentView()!!) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        container.setContentView(R.layout.corner_drawable_fragment)
        setBarTitle("Drawable")

        viewBinding.apply {
            val height = cornerText.paint.fontMetrics.descent - cornerText.paint.fontMetrics.ascent
            val html = "<img src='http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg'/> 圆角圆角圆角圆角圆角圆角圆角圆角"
            cornerText.text = Html.fromHtml(html,
                {
                    val drawable = CornerBorderDrawable()
                    drawable.borderColor = Color.RED
                    drawable.borderWidth = pxFromDip(10.0f)
                    drawable.shouldAbsoluteCircle = true
                    drawable.backgroundColor = Color.BLUE
                    drawable.intrinsicWidth = height.toInt()
                    drawable.intrinsicHeight = height.toInt()
                    drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

                    drawable
                }, null)

            val drawable = CornerBorderDrawable()
            drawable.borderColor = Color.RED
            drawable.borderWidth = pxFromDip(10.0f)
            drawable.shouldAbsoluteCircle = true
            drawable.backgroundColor = Color.BLUE

            drawable.attachView(cornerText1, true)

            val loadingDrawable = LoadingDrawable()
            loadingDrawable.intrinsicWidth = pxFromDip(37.0f)
            loadingDrawable.intrinsicHeight = pxFromDip(37.0f)

            imgSquare.setImageDrawable(loadingDrawable)
            loadingDrawable.start()

            drawable.borderColor = 0
            drawable.borderWidth = 0
            drawable.shouldAbsoluteCircle = false
            drawable.backgroundColor = Color.RED
            drawable.setCornerRadius(pxFromDip(10.0f))
            drawable.attachView(imgRect, true)
        }
    }
}

