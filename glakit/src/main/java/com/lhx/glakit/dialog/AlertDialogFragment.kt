package com.lhx.glakit.dialog

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lhx.glakit.R
import com.lhx.glakit.base.widget.OnSingleClickListener
import com.lhx.glakit.drawable.CornerBorderDrawable
import com.lhx.glakit.utils.SizeUtils
import com.lhx.glakit.utils.StringUtils
import kotlinx.android.synthetic.main.action_sheet_dialog.*
import kotlinx.android.synthetic.main.alert_dialog.*


/**
 * 信息弹窗fragment
 */
class AlertDialogFragment: BaseDialogFragment {

    //关闭dialog
    private val dismissDialogWhat = 1
    private val position = "position"

    //弹窗样式
    @AlertStyle.Style
    private var _style = AlertStyle.ALERT

    //弹窗属性
    private var _props: AlertProps? = null
    val props: AlertProps
    get(){
        return _props!!
    }

    //内容视图
    private var _contentView: View? = null

    //标题
    var _title: String? = null

    //副标题
    private var _subtitle: String? = null

    //图标
    private var _icon: Drawable? = null

    //按钮信息
    private var _buttonTitles: Array<String>? = null

    //点击按钮后弹窗是否消失
    var shouldDismissAfterClickItem = true

    //点击事件回调
    var onItemClickListener: OnItemClickListener? = null

    //UI回调
    var alertUIHandler: AlertUIHandler? = null

    //是否需要计算内容高度 当内容或者按钮数量过多时可设置，防止内容显示不完
    var shouldMeasureContentHeight = false

    //用于延迟操作
    private var mHandler: Handler? = null

    constructor(@AlertStyle.Style style: Int, title: String?, subtitle: String?, icon: Drawable?, buttonTitles: Array<String>){

        _style = style
        _title = title
        _subtitle = subtitle
        _icon = icon
        _buttonTitles = buttonTitles
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if(_props == null){
            _props = AlertProps.build(context)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if(_contentView != null){
            _contentView = inflater.inflate(if (_style == AlertStyle.ALERT) R.layout.alert_dialog else R.layout.action_sheet_dialog, container, false)
            initViews()
        }

        return _contentView
    }

    //初始化视图
    private fun initViews() {

        if ((_buttonTitles == null || _buttonTitles!!.isEmpty()) && _style == AlertStyle.ALERT) {
            _buttonTitles = arrayOf("确定")
        }

        if (_icon != null) {
            logoImageView.setImageDrawable(_icon)
            logoImageView.setPadding(0, props.contentVerticalSpace, 0, 0)
        } else {
            logoImageView.visibility = View.GONE
        }

        if (_title == null) {
            titleTextView.visibility = View.GONE
        } else {
            titleTextView.setTextColor(props.titleColor)
            titleTextView.textSize = props.titleSize
            titleTextView.text = _title
            titleTextView.setPadding(0, props.contentVerticalSpace, 0, 0)
        }

        if (_subtitle == null) {
            subtitleTextView.visibility = View.GONE
        } else {
            subtitleTextView.setTextColor(props.subtitleColor)
            subtitleTextView.textSize = props.subtitleSize
            subtitleTextView.text = _subtitle
            subtitleTextView.setPadding(0, props.contentVerticalSpace, 0, 0)
        }

        //actionSheet 样式不一样
        if (_style == AlertStyle.ACTION_SHEET) {
            cancelTextView.setOnClickListener(this)
            cancelTextView.textSize = props.buttonTextSize
            cancelTextView.setTextColor(props.buttonTextColor)
            cancelTextView.setPadding(
                props.buttonLeftRightPadding, props.buttonTopBottomPadding,
                props.buttonLeftRightPadding, props.buttonTopBottomPadding
            )
            setBackground(topContainer)
            setBackgroundSelector(cancelTextView)
            topTransparentView.setOnClickListener(this)
            val has = hasTopContent()

            //隐藏顶部分割线 没有按钮也隐藏
            if (!has || _buttonTitles == null || _buttonTitles!!.isEmpty()) {
                divider.visibility = View.GONE
            }
            val top = if (has) props.contentPadding - props.contentVerticalSpace else 0
            val bottom = if (has) props.contentPadding else 0
            scrollContainer.setPadding(0, top, 0, bottom)
        } else {
            scrollContainer.setPadding(0, props.contentPadding - props.contentVerticalSpace, 0, props.contentPadding)
            _contentView!!.setBackgroundColor(props.backgroundColor)
            setBackground(_contentView!!)
        }

        if (shouldMeasureContentHeight || props.contentMinHeight > 0) {
            measureContentHeight()
        }
        val spanCount = if (_buttonTitles?.size != 2 || _style == AlertStyle.ACTION_SHEET) 1 else 2
        val layoutManager = GridLayoutManager(context, spanCount)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        recyclerView.layoutManager = layoutManager

        if (_buttonTitles != null && _buttonTitles!!.size > 1) { //添加分割线
            recyclerView!!.addItemDecoration(ItemDecoration())
        }

        recyclerView.adapter = Adapter()

        setStyle(STYLE_NORMAL, R.style.Theme_dialog_noTitle_noBackground)

        //设置弹窗大小
        val window = dialog?.window
        if(window != null){
            val layoutParams = window.attributes
            layoutParams.gravity = if (_style == AlertStyle.ALERT) Gravity.CENTER else Gravity.BOTTOM
            layoutParams.width = getContentViewWidth()
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
            when (_style) {
                AlertStyle.ACTION_SHEET -> {
                    window.decorView.setPadding(props.dialogPadding, props.dialogPadding, props.dialogPadding, props.dialogPadding)
                    window.setWindowAnimations(R.style.action_sheet_animate)
                    isCancelable = true
                    dialog?.setCanceledOnTouchOutside(true)
                }
                AlertStyle.ALERT -> {
                    window.decorView.setPadding(0, props.dialogPadding, 0, props.dialogPadding)
                    isCancelable = false
                    dialog?.setCanceledOnTouchOutside(false)
                }
            }
            window.attributes = layoutParams
        }
    }

    //计算内容高度
    private fun measureContentHeight() { //按钮内容高度

        var buttonContentHeight = 0

        //顶部内容高度
        var topContentHeight = 0

        //取消按钮高度 STYLE_ALERT 为 0
        var cancelButtonHeight = 0

        //图标高度
        if (_icon != null) {
            topContentHeight += _icon!!.intrinsicHeight
            topContentHeight += props.contentVerticalSpace
        }

        val contentWidth = getContentViewWidth()
        if (_title != null || _subtitle != null) { //标题高度
            if (_title != null) {
                topContentHeight += props.contentVerticalSpace
                val params = subtitleTextView.layoutParams as LinearLayout.LayoutParams
                val maxWidth = contentWidth - params.leftMargin - params.rightMargin
                topContentHeight += StringUtils.measureTextHeight(_title, titleTextView.paint, maxWidth)
            }

            //副标题高度
            if (_subtitle != null) {
                topContentHeight += props.contentVerticalSpace
                val params = subtitleTextView.layoutParams as LinearLayout.LayoutParams
                val maxWidth = contentWidth - params.leftMargin - params.rightMargin
                topContentHeight += StringUtils.measureTextHeight(_subtitle, subtitleTextView.paint, maxWidth)
            }
        }

        //内容高度不够
        if (_icon != null || _title != null || _subtitle != null) {
            if (topContentHeight < props.contentMinHeight) {
                val res = props.contentMinHeight - topContentHeight
                val top = props.contentPadding - props.contentVerticalSpace + res / 2
                val bottom = props.contentPadding + res / 2
                scrollContainer.setPadding(0, top, 0, bottom)
            }
        }

        val maxWidth = contentWidth - props.buttonLeftRightPadding * 2
        when (_style) {
            AlertStyle.ACTION_SHEET -> {
                if (hasTopContent()) {
                    topContentHeight += props.contentPadding * 2 - props.contentVerticalSpace
                }
                if (_buttonTitles != null && _buttonTitles!!.isNotEmpty()) {

                    val textView = View.inflate(context, R.layout.alert_button_item, null) as TextView
                    textView.textSize = props.buttonTextSize
                    var i = 0
                    while (i < _buttonTitles!!.size) {
                        val title = _buttonTitles!![i]
                        buttonContentHeight += StringUtils.measureTextHeight(title, textView.paint, maxWidth)
                        + props.buttonTopBottomPadding * 2 + props.dividerHeight
                        i++
                    }
                    buttonContentHeight -= props.dividerHeight
                }

                //取消按钮高度
                cancelButtonHeight += StringUtils.measureTextHeight(cancelTextView.text, cancelTextView.paint, maxWidth)
                + props.buttonTopBottomPadding * 2 + props.dialogPadding
            }
            AlertStyle.ALERT -> {
                topContentHeight += props.contentPadding * 2 - props.contentVerticalSpace
                val textView = View.inflate(context, R.layout.alert_button_item, null) as TextView
                textView.textSize = props.buttonTextSize

                if(_buttonTitles != null){
                    var i = 0
                    while (i < _buttonTitles!!.size) {
                        val title = _buttonTitles!![i]
                        buttonContentHeight += StringUtils.measureTextHeight(
                            title, textView.paint,
                            maxWidth
                        ) + props.buttonTopBottomPadding * 2 + props.dividerHeight
                        if (_buttonTitles!!.size <= 2) break
                        i++
                    }
                }
                if (buttonContentHeight > 0) {
                    buttonContentHeight -= props.dividerHeight
                }
            }
        }
        var maxHeight = SizeUtils.getWindowHeight(context!!) - props.dialogPadding * 2 -
                    cancelButtonHeight - scrollContainer.paddingBottom - scrollContainer.paddingTop
        if (divider.visibility == View.VISIBLE) {
            maxHeight -= props.dividerHeight
        }
        if (topContentHeight + buttonContentHeight > maxHeight) { //内容太多了
            val half = maxHeight / 2
            when(half){
                in (buttonContentHeight + 1) until topContentHeight -> {

                    setScrollViewHeight(maxHeight - buttonContentHeight)
                }
                in (topContentHeight + 1) until buttonContentHeight -> {

                    setRecyclerViewHeight(maxHeight - topContentHeight)
                }
                else -> {
                    setRecyclerViewHeight(half)
                    setScrollViewHeight(half)
                }
            }
        }
    }

    //设置scrollview
    private fun setScrollViewHeight(height: Int) {
        val params = scrollView.layoutParams
        params.height = height
        scrollView.layoutParams = params
    }

    //设置recyclerView
    private fun setRecyclerViewHeight(height: Int) {
        val params = recyclerView.layoutParams
        params.height = height
        recyclerView.layoutParams = params
    }

    fun onDismiss(dialog: DialogInterface?) {
        if (mAlertUIHandler != null) {
            mAlertUIHandler!!.onDismiss(this)
        }
    }

    fun onClick(v: View) {
        if (v === mCancelTextView || v === mTopTransparentView) {
            dismiss()
        }
    }

    //获取内容视图宽度
    private fun getContentViewWidth(): Int {
        when (mStyle) {
            STYLE_ALERT -> return SizeUtil.pxFormDip(280, mContext)
            STYLE_ACTION_SHEET -> return mContext.getResources().getDisplayMetrics().widthPixels
        }
        return 0
    }

    //是否有头部内容
    private fun hasTopContent(): Boolean {
        return mTitle != null || mSubtitle != null || mIcon != null
    }

    //设置背景
    private fun setBackground(view: View?) {
        val drawable = CornerBorderDrawable()
        drawable.setCornerRadius(mCornerRadius)
        drawable.backgroundColor = mDialogBackgroundColor
        drawable.attachView(view, false)
    }

    //设置点击效果
    private fun setBackgroundSelector(view: View?): Array<CornerBorderDrawable>? {
        val stateListDrawable =
            StateListDrawable()
        val drawablePressed = CornerBorderDrawable()
        drawablePressed.setCornerRadius(mCornerRadius)
        drawablePressed.backgroundColor = mHighlightBackgroundColor
        stateListDrawable.addState(intArrayOf(R.attr.state_pressed), drawablePressed)
        val drawable = CornerBorderDrawable()
        drawable.setCornerRadius(mCornerRadius)
        drawable.backgroundColor = mDialogBackgroundColor
        stateListDrawable.addState(intArrayOf(), drawable)
        view.setClickable(true)
        ViewUtil.setBackground(stateListDrawable, view)
        return arrayOf(drawablePressed, drawable)
    }

    fun getHandler(): Handler? {
        if (mHandler == null) {
            mHandler = Handler(object : Callback() {
                fun handleMessage(msg: Message): Boolean {
                    when (msg.what) {
                        DISMISS_DIALOG -> {
                            val bundle: Bundle = msg.getData()
                            if (mOnItemClickListener != null) {
                                mOnItemClickListener!!.onItemClick(
                                    this@AlertController,
                                    bundle.getInt(POSITION)
                                )
                            }
                        }
                    }
                    return true
                }
            })
        }
        return mHandler
    }

    //按钮列表适配器
    private class Adapter : RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val holder =
                ViewHolder(View.inflate(mContext, R.layout.alert_button_item, null))
            holder.itemView.setOnClickListener(object : OnSingleClickListener() {
                fun onSingleClick(v: View?) {
                    if (mShouldDismissAfterClickItem) {
                        dismiss()
                        val bundle = Bundle()
                        bundle.putInt(POSITION, holder.adapterPosition)
                        val message: Message = getHandler().obtainMessage()
                        message.what = DISMISS_DIALOG
                        message.setData(bundle)
                        getHandler().sendMessageDelayed(message, 200)
                    } else {
                        if (mOnItemClickListener != null) {
                            mOnItemClickListener.onItemClick(
                                this@AlertController,
                                holder.adapterPosition
                            )
                        }
                    }
                }
            })
            holder.textView.textSize = mButtonTextSize
            holder.textView.setPadding(
                mButtonLeftRightPadding, mButtonTopBottomPadding,
                mButtonLeftRightPadding, mButtonTopBottomPadding
            )
            return holder
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textView.setText(mButtonTitles.get(position))
            var color: Int = mButtonTextColor
            var backgroundColor: Int = mDialogBackgroundColor
            var pressedBackgroundColor: Int = mHighlightBackgroundColor
            var enable = true
            //刷新UI
            if (mAlertUIHandler != null) {
                if (!mAlertUIHandler.shouldEnable(this@AlertController, position)) {
                    color = mDisableButtonTextColor
                    enable = false
                } else if (mAlertUIHandler.shouldDestructive(this@AlertController, position)) {
                    color = mDestructiveButtonTextColor
                    backgroundColor = mDestructiveButtonBackgroundColor
                    pressedBackgroundColor = mDestructiveHighlightedButtonBackgroundColor
                }
            } else if (position == mDestructivePosition) {
                color = mDestructiveButtonTextColor
                backgroundColor = mDestructiveButtonBackgroundColor
                pressedBackgroundColor = mDestructiveHighlightedButtonBackgroundColor
            }
            holder.itemView.isEnabled = enable
            holder.textView.setTextColor(color)
            holder.drawable.backgroundColor = backgroundColor
            holder.drawablePressed.backgroundColor = pressedBackgroundColor
            //设置点击效果
            if (mStyle == STYLE_ACTION_SHEET || mButtonTitles.size != 2) { //垂直
                if (mButtonTitles.size == 1 && mStyle == STYLE_ACTION_SHEET && !hasTopContent()) {
                    holder.drawablePressed.setCornerRadius(mCornerRadius)
                    holder.drawable.setCornerRadius(mCornerRadius)
                } else {
                    if (position == 0 && !hasTopContent() && mStyle == STYLE_ACTION_SHEET) {
                        holder.drawablePressed.setCornerRadius(mCornerRadius, 0, mCornerRadius, 0)
                        holder.drawable.setCornerRadius(mCornerRadius, 0, mCornerRadius, 0)
                    } else if (position == mButtonTitles.size - 1) {
                        holder.drawablePressed.setCornerRadius(0, mCornerRadius, 0, mCornerRadius)
                        holder.drawable.setCornerRadius(0, mCornerRadius, 0, mCornerRadius)
                    } else {
                        holder.drawablePressed.setCornerRadius(0)
                        holder.drawable.setCornerRadius(0)
                    }
                }
            } else { //水平
                if (position == 0) {
                    holder.drawablePressed.setCornerRadius(0, mCornerRadius, 0, 0)
                    holder.drawable.setCornerRadius(0, mCornerRadius, 0, 0)
                } else {
                    holder.drawablePressed.setCornerRadius(0, 0, 0, mCornerRadius)
                    holder.drawable.setCornerRadius(0, 0, 0, mCornerRadius)
                }
            }
        }

        override fun getItemCount(): Int {
            return if (mButtonTitles != null) mButtonTitles.size else 0
        }
    }

    //弹窗按钮
    private class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var textView: TextView
        var drawable: CornerBorderDrawable
        var drawablePressed: CornerBorderDrawable

        init {
            textView = itemView
            val drawables: Array<CornerBorderDrawable> = setBackgroundSelector(itemView)
            drawablePressed = drawables[0]
            drawablePressed.setCornerRadius(0)
            drawable = drawables[1]
            drawable.setCornerRadius(0)
        }
    }

    //按钮分割线
    private class ItemDecoration internal constructor() : RecyclerView.ItemDecoration() {
        ///分割线
        var mDivider: Drawable

        fun onDraw(c: Canvas?, parent: RecyclerView, state: RecyclerView.State?) {
            super.onDraw(c, parent, state!!)
            //绘制按钮分割线
            val count = parent.childCount
            for (i in 0 until count) {
                val child: View = parent.getChildAt(i)
                val position = parent.getChildAdapterPosition(child)
                if (position < mButtonTitles.size - 1) { //垂直排列
                    if (mStyle == STYLE_ACTION_SHEET || mButtonTitles.size != 2) {
                        mDivider.setBounds(
                            0,
                            child.getBottom(),
                            child.getRight(),
                            child.getBottom() + mDividerHeight
                        )
                    } else { //水平排列
                        mDivider.setBounds(
                            child.getRight(),
                            0,
                            child.getRight() + mDividerHeight,
                            child.getBottom()
                        )
                    }
                    mDivider.draw(c)
                }
            }
        }

        fun getItemOffsets(
            outRect: Rect,
            view: View?,
            parent: RecyclerView,
            state: RecyclerView.State?
        ) { //设置item的偏移量 大小为item+分割线
            val position = parent.getChildAdapterPosition(view)
            if (position < mButtonTitles.size - 1) {
                if (mStyle == STYLE_ACTION_SHEET || mButtonTitles.size != 2) { //垂直排列
                    outRect.bottom = mDividerHeight
                } else { //水平
                    outRect.right = mDividerHeight
                }
            }
        }

        ///构造方法
        init {
            val drawable = ColorDrawable()
            drawable.color = ContextCompat.getColor(mContext, R.color.divider_color)
            mDivider = drawable
        }
    }

    //弹窗按钮点击回调
    interface OnItemClickListener {
        //点击某个按钮 从左到右，从上到下
        fun onItemClick(controller: AlertController?, index: Int)
    }

    //弹窗适配器
    interface AlertDialogAdapter {

        //弹窗消失
        fun onDismiss(fragment: AlertDialogFragment){}

        //该按钮是否具有警示意义 从左到右，从上到下
        fun shouldDestructive(fragment: AlertDialogFragment, position: Int): Boolean{
            return false
        }

        //该按钮是否可以点击 从左到右，从上到下
        fun shouldEnable(fragment: AlertDialogFragment, position: Int): Boolean
    }
}