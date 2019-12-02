package com.lhx.glakit.base.widget

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import com.lhx.glakit.R
import com.lhx.glakit.drawable.DrawableUtils
import kotlin.math.max
import kotlin.math.min


//标题栏
class TitleBar: ViewGroup {

    //标题
    private var titleTextView: TextView? = null

    //阴影分割线
    private var shadow: View

    //左边按钮
    private var leftItem: View? = null

    //右边按钮
    private var rightItem: View? = null

    ///标题视图
    private var titleView: View? = null

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {

        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.title_bar_background_color))
        shadow = View(context)
        shadow.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.title_bar_shadow_color))
        addView(shadow)
    }

    //设置标题
    fun setTitle(title: CharSequence?) {

        if(titleTextView == null){
            val textView = TextView(this.context)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.title_bar_text_size))
            textView.setTextColor(ContextCompat.getColor(context, R.color.title_bar_title_color))
            textView.setLines(1)
            textView.setBackgroundColor(Color.TRANSPARENT)
            textView.gravity = Gravity.CENTER
            textView.ellipsize = TextUtils.TruncateAt.END

            addView(textView, 0)
            titleTextView = textView

            this.titleView = titleTextView
        }

        titleTextView!!.text = title
    }

    fun getTitle(): CharSequence? {
        if(titleTextView != null){
            return titleTextView!!.text
        }

        return null
    }


    //设置阴影颜色
    fun setShadowColor(@ColorInt shadowColor: Int) {

        shadow.setBackgroundColor(shadowColor)
    }

    //设置标题视图
    fun setTitleView(titleView: View?) {

        if(this.titleView != null)
            removeView(this.titleView)

        this.titleView = titleView
        if (titleView != null) {

            titleTextView?.visibility = View.INVISIBLE
            addView(titleView, 0)
        } else {

            titleTextView?.visibility = View.VISIBLE
            this.titleView = titleTextView
        }
    }

    //显示返回按钮
    fun setShowBackButton(show: Boolean): TextView? {
        if (show) {
            var drawable: Drawable? = null
            val icon: Int = R.drawable.back_icon
            var title: String? = null
            if (icon != 0) {
                drawable = ContextCompat.getDrawable(context, icon)
                drawable = DrawableUtils.getTintDrawable(drawable!!, ContextCompat.getColor(context, R.color.title_bar_tint_color))
            }
            if (drawable == null) {
                title = context.getString(R.string.title_bar_back_title)
            }
            val textView: TextView = setItem(title, null, true)
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
                textView.setCompoundDrawables(drawable, null, null, null)
            }
            return textView
        } else {
            setLeftItem(null)
        }
        return null
    }


    //设置左边视图
    fun setLeftItem(item: View?) {

        if (item != leftItem) {
            if (leftItem != null)
                removeView(leftItem)

            leftItem = item

            if (leftItem != null) {
                addView(leftItem, 0)
            }
        }
    }

    ///设置右边视图
    fun setRightItem(item: View?) {

        if (item !== rightItem) {
            if (rightItem != null)
                removeView(rightItem)

            rightItem = item
            if (rightItem != null) {

                addView(rightItem, 0)
            }
        }
    }


    fun setLeftItem(title: String?, drawable: Drawable?): TextView {
        return setItem(title, drawable, true)
    }

    fun setRightItem(title: String?, drawable: Drawable?): TextView {
        return setItem(title, drawable, false)
    }

    /**
     * 设置标题栏按钮
     *
     * @param title 标题
     * @param drawable 图标,显示在标题上面
     * @param isLeft 位置, 是否在左边
     * @return 新创建的按钮
     */
    fun setItem(title: String?, drawable: Drawable?, isLeft: Boolean): TextView {

        val textView = TextView(this.context)

        if (!TextUtils.isEmpty(title)) {
            textView.text = title
        }

        val margin = resources.getDimensionPixelSize(R.dimen.title_bar_margin)
        textView.setPadding(margin,0, margin, 0)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.title_bar_item_text_size))
        textView.setBackgroundColor(Color.TRANSPARENT)
        textView.setTextColor(ContextCompat.getColor(context, R.color.title_bar_tint_color))
        textView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)
        drawable?.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)

        if(isLeft){
            textView.gravity = Gravity.START or Gravity.CENTER_VERTICAL
            if (drawable != null) {
                textView.setCompoundDrawables(drawable, null, null, null)
            }
            setLeftItem(textView)
        }else{
            textView.gravity = Gravity.END or Gravity.CENTER_VERTICAL
            if (drawable != null) {
                textView.setCompoundDrawables(null, null, drawable, null)
            }
            setRightItem(textView)
        }

        return textView
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        //测量子视图大小
        measureChildren(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val left = paddingLeft
        val top = paddingTop
        val width = r - paddingRight - left
        val height = b - paddingBottom - top

        var leftWidth = 0

        if(leftItem != null){
            leftWidth = leftItem!!.measuredWidth
            leftItem!!.layout(left, top,left + leftWidth, top + height)
        }

        var rightWidth = 0

        if(rightItem != null){
            rightWidth = rightItem!!.measuredWidth
            rightItem!!.layout(left + width - rightWidth, top, left + width, top + height)
        }

        if(titleView != null){
            var titleWidth = titleView!!.measuredWidth
            val titleHeight = min(height, titleView!!.measuredHeight)

            val margin = max(max(leftWidth, rightWidth), resources.getDimensionPixelSize(R.dimen.title_bar_margin))
            if(titleWidth > width - margin * 2){
                titleWidth = max(0, width - margin * 2)
            }

            val titleLeft = left + (width - titleWidth) / 2
            val titleTop = top + (height - titleHeight) / 2
            titleView?.layout(titleLeft, titleTop, titleLeft + titleWidth, titleTop + titleHeight)

            println("width = $width, height = $height")
        }

        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
        shadow.layout(left, height - dividerHeight, left + width, height)
    }
}