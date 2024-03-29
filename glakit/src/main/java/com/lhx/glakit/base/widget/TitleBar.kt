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
import androidx.core.view.isGone
import com.lhx.glakit.R
import com.lhx.glakit.extension.getColorCompat
import com.lhx.glakit.extension.getDrawableCompat
import com.lhx.glakit.extension.getTintDrawable
import kotlin.math.max
import kotlin.math.min


//标题栏
class TitleBar: ViewGroup {

    //标题
    private var titleTextView: TextView? = null

    //标题颜色
    var titleColor = context.getColorCompat(R.color.title_bar_title_color)
        set(value) {
            if (value != field) {
                field = value
                titleTextView?.setTextColor(titleColor)
            }
        }

    //阴影分割线
    private var shadow: View

    //左边按钮
    private var leftItem: View? = null

    //右边按钮
    private var rightItem: View? = null

    //标题视图
    private var titleView: View? = null
    private var titleViewFill = false

    //标题栏离屏幕的间距
    var titleBarMargin = resources.getDimensionPixelSize(R.dimen.title_bar_margin)
        set(value) {
            if(value != field) {
                field = value
                requestLayout()
            }
        }

    //着色
    var tintColor = context.getColorCompat(R.color.title_bar_tint_color)
        set(value) {
            if (value != field) {
                field = value
                var textView = leftItem
                if (textView is TextView) {
                    val drawables = textView.compoundDrawables
                    val drawable = drawables[0]
                    if (drawable != null) {
                        textView.setCompoundDrawables(drawable.getTintDrawable(field), null, null, null)
                    }
                    textView.setTextColor(field)
                }

                textView = rightItem
                if (textView is TextView) {
                    val drawables = textView.compoundDrawables
                    val drawable = drawables[2]
                    if (drawable != null) {
                        textView.setCompoundDrawables(null, null, drawable.getTintDrawable(field), null)
                    }
                    textView.setTextColor(field)
                }
            }
        }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr) {

        setBackgroundColor(context.getColorCompat(R.color.title_bar_background_color))
        shadow = View(context)
        shadow.setBackgroundColor(context.getColorCompat(R.color.title_bar_shadow_color))
        addView(shadow)
    }

    //设置标题
    fun setTitle(title: CharSequence?) {
        titleViewFill = false
        if(titleTextView == null){
            val textView = TextView(this.context)
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.title_bar_text_size))
            textView.paint.isFakeBoldText = context.resources.getBoolean(R.bool.title_bar_text_style_bold)
            textView.setTextColor(titleColor)
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
    fun setTitleView(titleView: View?, fill: Boolean = false) {

        if(this.titleView != null)
            removeView(this.titleView)

        this.titleView = titleView
        if (titleView != null) {
            titleViewFill = fill
            titleTextView?.visibility = View.INVISIBLE
            addView(titleView, 0)
        } else {
            titleViewFill = false
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
                drawable = context.getDrawableCompat(icon)
                drawable = drawable?.getTintDrawable(tintColor)
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

    private var rightItemWarp = false
    //设置右边视图
    fun setRightItem(item: View?, wrap: Boolean = false) {
        if (item !== rightItem) {
            if (rightItem != null)
                removeView(rightItem)

            rightItem = item
            if (rightItem != null) {
                rightItemWarp = wrap
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
     * @param icon 图标,显示在标题上面
     * @param isLeft 位置, 是否在左边
     * @return 新创建的按钮
     */
    fun setItem(title: String?, icon: Drawable?, isLeft: Boolean): TextView {

        var drawable = icon
        val textView = TextView(this.context)

        if (!TextUtils.isEmpty(title)) {
            textView.text = title
        }

        textView.setPadding(titleBarMargin,0, titleBarMargin, 0)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.title_bar_item_text_size))
        textView.setBackgroundColor(Color.TRANSPARENT)
        textView.setTextColor(tintColor)
        textView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT)

        if (drawable != null) {
            drawable = drawable.getTintDrawable(tintColor)
        }
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

        var leftWidth = 0
        var rightWidth = 0

        if (leftItem != null && !leftItem!!.isGone) {
            leftWidth = leftItem!!.measuredWidth
            measureChild(leftItem!!, widthMeasureSpec, heightMeasureSpec)
        }

        if (rightItem != null && !rightItem!!.isGone) {
            rightWidth = rightItem!!.measuredWidth
            measureChild(rightItem!!, widthMeasureSpec, heightMeasureSpec)
        }

        if(titleView != null){
            val params = titleView!!.layoutParams
            val padding = max(leftWidth, titleBarMargin) + max(rightWidth, titleBarMargin)
            val childWidthMeasureSpec = getChildMeasureSpec(widthMeasureSpec, padding, params.width)
            val childHeightMeasureSpec = getChildMeasureSpec(heightMeasureSpec, 0, params.height)
            measureChild(titleView!!, childWidthMeasureSpec, childHeightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {

        val left = paddingLeft
        val top = paddingTop
        val width = r - paddingRight - left
        val height = b - paddingBottom - top

        var leftWidth = titleBarMargin
        if(leftItem != null){
            leftWidth = leftItem!!.measuredWidth
            leftItem!!.layout(left, top,left + leftWidth, top + height)
        }

        var rightWidth = titleBarMargin
        if(rightItem != null){
            rightWidth = rightItem!!.measuredWidth
            var rightItemTop = top
            var rightItemBottom = top + height
            if (rightItemWarp) {
                rightItemTop = max(top, top + (height - rightItem!!.measuredHeight) / 2)
                rightItemBottom = rightItemTop + min(height, rightItem!!.measuredHeight)
            }

            rightItem!!.layout(left + width - rightItem!!.measuredWidth, rightItemTop, left + width, rightItemBottom)
        }

        if(titleView != null){
            val maxWidth = width - leftWidth - rightWidth
            val titleWidth = if (titleViewFill) maxWidth else min(titleView!!.measuredWidth, maxWidth)
            val titleHeight = min(height, titleView!!.measuredHeight)
            val titleTop = top + (height - titleHeight) / 2
            var titleLeft = leftWidth

            if (titleWidth < maxWidth) {
                val avg = (width - titleWidth) / 2
                if (avg >= leftWidth && avg + titleWidth <= width - rightWidth) {
                    titleLeft = avg
                } else if (avg + titleWidth > width - rightWidth) {
                    //左移
                    titleLeft = width - rightWidth - titleWidth
                }
            }

            titleView?.layout(titleLeft, titleTop, titleLeft + titleWidth, titleTop + titleHeight)
        }

        val dividerHeight = resources.getDimensionPixelSize(R.dimen.divider_height)
        shadow.layout(left, height - dividerHeight, left + width, height)
    }
}