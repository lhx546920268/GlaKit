package com.lhx.glakit.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.lhx.glakit.R


/**
 * 带删除按钮输入框
 */
@Suppress("ClickableViewAccessibility")
class ClearableEditText : AppCompatTextView, TextWatcher {

    ///清空按钮图标
    var clearDrawable: Drawable? = null
    set(value) {
        if(value != field){
            field = value
            if(field != null){
                field!!.setBounds(0, 0, field!!.intrinsicWidth, field!!.intrinsicHeight)
            }
        }
    }

    ///是否聚焦
    private var _hasFocus = false

    constructor(context: Context?) : this(context, null)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ){
        if (attrs != null) {
            val array = context!!.obtainStyledAttributes(attrs, R.styleable.ClearableEditText)
            val res = array.getResourceId(R.styleable.ClearableEditText_clear_icon, 0)
            if (res > 0) {
                clearDrawable = ContextCompat.getDrawable(context, res)
            }
            array.recycle()
        }

        addTextChangedListener(this)

        //焦点改变
        setOnFocusChangeListener{ _, hasFocus ->
            _hasFocus = hasFocus
            showClearBtn()
        }

        setHintTextColor(ContextCompat.getColor(context!!, R.color.hint_color))
    }

    //显示删除按钮
    private fun showClearBtn() {
        if (length() > 0 && _hasFocus)
            setCompoundDrawablesWithIntrinsicBounds(null, null, clearDrawable, null)
        else
            setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
    }


    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
    }

    override fun afterTextChanged(s: Editable?) {
        showClearBtn()
    }

    // 处理删除事件
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_UP) {

            if (clearDrawable != null) {
                val x = event.x.toInt()
                val y = event.y.toInt()

                val rect = clearDrawable!!.bounds
                val height: Int = rect.height()
                val distance = (getHeight() - height) / 2

                val isInnerWidth = x > width - totalPaddingRight && x < width - paddingRight
                val isInnerHeight = y > distance && y < distance + height

                if (isInnerWidth && isInnerHeight) {
                    this.text = ""
                } else {
                    performClick()
                }
            } else {
                performClick()
            }
        }
        return super.onTouchEvent(event)
    }
}