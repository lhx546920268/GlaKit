package com.lhx.glakit.dialog

import android.content.DialogInterface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
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


/**
 * 信息弹窗fragment
 */
class AlertDialogFragment: BaseDialogFragment() {

    //关闭dialog
    private val dismissDialogWhat = 1
    private val position = "position"

    //弹窗样式
    @AlertStyle.AlertDialogStyle
    private var _style = 0

    //弹窗属性
    var props: AlertProps? = null

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
    private var _shouldMeasureContentHeight = false

    //用于延迟操作
    private var mHandler: Handler? = null

    constructor(@AlertStyle.AlertDialogStyle style: Int, title: String?, subtitle: String?, icon: Drawable?, buttonTitles: Array<String>){

        _style = style
        _title = title
        _subtitle = subtitle
        _icon = icon
        _buttonTitles = buttonTitles

        _contentView = View.inflate(context, if (_style == AlertStyle.ALERT) R.layout.alert_dialog else R.layout.action_sheet_dialog, null)
    }



    fun setButtonTopBottomPadding(buttonTopBottomPadding: Int) {
        mButtonTopBottomPadding = buttonTopBottomPadding
    }

    fun setButtonLeftRightPadding(buttonLeftRightPadding: Int) {
        mButtonLeftRightPadding = buttonLeftRightPadding
    }

    fun setContentVerticalSpace(contentVerticalSpace: Int) {
        mContentVerticalSpace = contentVerticalSpace
    }

    fun setContentPadding(contentPadding: Int) {
        mContentPadding = contentPadding
    }

    fun setDialogPadding(dialogPadding: Int) {
        mDialogPadding = dialogPadding
    }

    fun setTitleColor(@ColorInt titleColor: Int) {
        mTitleColor = titleColor
    }

    fun setTitleSize(titleSize: Float) {
        mTitleSize = titleSize
    }

    fun setSubtitleColor(subtitleColor: Int) {
        mSubtitleColor = subtitleColor
    }

    fun setSubtitleSize(subtitleSize: Float) {
        mSubtitleSize = subtitleSize
    }

    fun setButtonTextSize(buttonTextSize: Float) {
        mButtonTextSize = buttonTextSize
    }

    fun setButtonTextColor(buttonTextColor: Int) {
        mButtonTextColor = buttonTextColor
    }

    fun setDestructiveButtonTextColor(destructiveButtonTextColor: Int) {
        mDestructiveButtonTextColor = destructiveButtonTextColor
    }

    fun setDisableButtonTextColor(disableButtonTextColor: Int) {
        mDisableButtonTextColor = disableButtonTextColor
    }

    fun setDividerHeight(dividerHeight: Int) {
        mDividerHeight = dividerHeight
    }

    fun setContentMinHeight(contentMinHeight: Int) {
        mContentMinHeight = contentMinHeight
    }

    fun setDestructiveButtonBackgroundColor(destructiveButtonBackgroundColor: Int) {
        mDestructiveButtonBackgroundColor = destructiveButtonBackgroundColor
    }

    fun setDestructiveHighlightedButtonBackgroundColor(
        destructiveHighlightedButtonBackgroundColor: Int
    ) {
        mDestructiveHighlightedButtonBackgroundColor = destructiveHighlightedButtonBackgroundColor
    }

    fun setDestructivePosition(destructivePosition: Int) {
        mDestructivePosition = destructivePosition
    }

    fun setTitle(title: String?) {
        mTitle = title
    }

    fun setSubtitle(subtitle: String?) {
        mSubtitle = subtitle
    }

    fun setIcon(icon: Drawable?) {
        mIcon = icon
    }

    fun setButtonTitles(buttonTitles: Array<String>) {
        mButtonTitles = buttonTitles
    }

    fun setShouldDismissAfterClickItem(shouldDismissAfterClickItem: Boolean) {
        mShouldDismissAfterClickItem = shouldDismissAfterClickItem
    }

    fun setShouldMeasureContentHeight(shouldMeasureContentHeight: Boolean) {
        mShouldMeasureContentHeight = shouldMeasureContentHeight
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        mOnItemClickListener = onItemClickListener
    }

    fun setAlertUIHandler(alertUIHandler: AlertUIHandler?) {
        mAlertUIHandler = alertUIHandler
    }

    fun getContentView(): View? {
        return mContentView
    }

    fun getLogoImageView(): ImageView? {
        return mLogoImageView
    }

    fun getTitleTextView(): TextView? {
        return mTitleTextView
    }

    fun getSubtitleTextView(): TextView? {
        return mSubtitleTextView
    }

    fun getCancelTextView(): TextView? {
        return mCancelTextView
    }

    fun getTopContainer(): LinearLayout? {
        return mTopContainer
    }

    fun getRecyclerView(): RecyclerView? {
        return mRecyclerView
    }

    //初始化视图
    private fun initView() {
        if ((mButtonTitles == null || mButtonTitles!!.size == 0) && mStyle == STYLE_ALERT) {
            mButtonTitles = arrayOf("确定")
        }
        if (mIcon != null) {
            mLogoImageView.setImageDrawable(mIcon)
            mLogoImageView.setPadding(0, mContentVerticalSpace, 0, 0)
        } else {
            mLogoImageView.setVisibility(View.GONE)
        }
        if (mTitle == null) {
            mTitleTextView!!.visibility = View.GONE
        } else {
            mTitleTextView!!.setTextColor(mTitleColor)
            mTitleTextView!!.textSize = mTitleSize
            mTitleTextView!!.text = mTitle
            mTitleTextView!!.setPadding(0, mContentVerticalSpace, 0, 0)
        }
        if (mSubtitle == null) {
            mSubtitleTextView!!.visibility = View.GONE
        } else {
            mSubtitleTextView!!.setTextColor(mSubtitleColor)
            mSubtitleTextView!!.textSize = mSubtitleSize
            mSubtitleTextView!!.text = mSubtitle
            mSubtitleTextView!!.setPadding(0, mContentVerticalSpace, 0, 0)
        }
        //actionSheet 样式不一样
        if (mStyle == STYLE_ACTION_SHEET) {
            mCancelTextView!!.setOnClickListener(this)
            mCancelTextView!!.textSize = mButtonTextSize
            mCancelTextView!!.setTextColor(mButtonTextColor)
            mCancelTextView!!.setPadding(
                mButtonLeftRightPadding, mButtonTopBottomPadding,
                mButtonLeftRightPadding, mButtonTopBottomPadding
            )
            setBackground(mTopContainer)
            setBackgroundSelector(mCancelTextView)
            mTopTransparentView = mContentView.findViewById(R.id.top_tranparent_view)
            mTopTransparentView.setOnClickListener(this)
            val has = hasTopContent()
            //隐藏顶部分割线 没有按钮也隐藏
            if (!has || mButtonTitles!!.size == 0) {
                mContentDivider.setVisibility(View.GONE)
            }
            val top = if (has) mContentPadding - mContentVerticalSpace else 0
            val bottom = if (has) mContentPadding else 0
            mScrollContainer!!.setPadding(0, top, 0, bottom)
        } else {
            mScrollContainer!!.setPadding(
                0,
                mContentPadding - mContentVerticalSpace,
                0,
                mContentPadding
            )
            mContentView.setBackgroundColor(mDialogBackgroundColor)
            setBackground(mContentView)
        }
        if (mShouldMeasureContentHeight || mContentMinHeight > 0) {
            measureContentHeight()
        }
        val spanCount = if (mButtonTitles!!.size != 2 || mStyle == STYLE_ACTION_SHEET) 1 else 2
        val layoutManager = GridLayoutManager(mContext, spanCount)
        layoutManager.orientation = GridLayoutManager.VERTICAL
        mRecyclerView!!.layoutManager = layoutManager
        if (mButtonTitles!!.size > 1) { //添加分割线
            mRecyclerView!!.addItemDecoration(ItemDecoration())
        }
        mRecyclerView!!.adapter = Adapter()
        mDialog = Dialog(mContext, R.style.Theme_dialog_noTitle_noBackground)
        mDialog.setOnDismissListener(this)
        mDialog.setContentView(mContentView)
        //设置弹窗大小
        val window: Window = mDialog.getWindow()
        val layoutParams: WindowManager.LayoutParams = window.getAttributes()
        layoutParams.gravity = if (mStyle == STYLE_ALERT) Gravity.CENTER else Gravity.BOTTOM
        layoutParams.width = getContentViewWidth()
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT
        when (mStyle) {
            STYLE_ACTION_SHEET -> {
                window.getDecorView()
                    .setPadding(mDialogPadding, mDialogPadding, mDialogPadding, mDialogPadding)
                window.setWindowAnimations(R.style.action_sheet_animate)
                mDialog.setCancelable(true)
                mDialog.setCanceledOnTouchOutside(true)
            }
            STYLE_ALERT -> {
                window.getDecorView().setPadding(0, mDialogPadding, 0, mDialogPadding)
                mDialog.setCancelable(false)
                mDialog.setCanceledOnTouchOutside(false)
            }
        }
        window.setAttributes(layoutParams)
    }

    //计算内容高度
    private fun measureContentHeight() { //按钮内容高度
        var buttonContentHeight = 0
        //顶部内容高度
        var topContentHeight = 0
        //取消按钮高度 STYLE_ALERT 为 0
        var cancelButtonHeight = 0
        //图标高度
        if (mIcon != null) {
            topContentHeight += mIcon!!.intrinsicHeight
            topContentHeight += mContentVerticalSpace
        }
        val contentWidth = getContentViewWidth()
        if (mTitle != null || mSubtitle != null) { //标题高度
            if (mTitle != null) {
                topContentHeight += mContentVerticalSpace
                val params =
                    mTitleTextView!!.layoutParams as LinearLayout.LayoutParams
                val maxWidth = contentWidth - params.leftMargin - params.rightMargin
                topContentHeight += StringUtil.measureTextHeight(
                    mTitle,
                    mTitleTextView!!.paint,
                    maxWidth
                )
            }
            //副标题高度
            if (mSubtitle != null) {
                topContentHeight += mContentVerticalSpace
                val params =
                    mSubtitleTextView!!.layoutParams as LinearLayout.LayoutParams
                val maxWidth = contentWidth - params.leftMargin - params.rightMargin
                topContentHeight += StringUtil.measureTextHeight(
                    mSubtitle,
                    mSubtitleTextView!!.paint,
                    maxWidth
                )
            }
        }
        //内容高度不够
        if (mIcon != null || mTitle != null || mSubtitle != null) {
            if (topContentHeight < mContentMinHeight) {
                val res = mContentMinHeight - topContentHeight
                val top = mContentPadding - mContentVerticalSpace + res / 2
                val bottom = mContentPadding + res / 2
                mScrollContainer!!.setPadding(0, top, 0, bottom)
            }
        }
        val maxWidth = contentWidth - mButtonLeftRightPadding * 2
        when (mStyle) {
            STYLE_ACTION_SHEET -> {
                if (hasTopContent()) {
                    topContentHeight += mContentPadding * 2 - mContentVerticalSpace
                }
                if (mButtonTitles!!.size > 0) {
                    val textView =
                        View.inflate(mContext, R.layout.alert_button_item, null) as TextView
                    textView.textSize = mButtonTextSize
                    var i = 0
                    while (i < mButtonTitles!!.size) {
                        val title = mButtonTitles!![i]
                        buttonContentHeight += StringUtil.measureTextHeight(
                            title, textView.paint,
                            maxWidth
                        ) + mButtonTopBottomPadding * 2 + mDividerHeight
                        i++
                    }
                    buttonContentHeight -= mDividerHeight
                }
                //取消按钮高度
                cancelButtonHeight += StringUtil.measureTextHeight(
                    mCancelTextView!!.text, mCancelTextView
                        .getPaint(), maxWidth
                ) + mButtonTopBottomPadding * 2 + mDialogPadding
            }
            STYLE_ALERT -> {
                topContentHeight += mContentPadding * 2 - mContentVerticalSpace
                val textView =
                    View.inflate(mContext, R.layout.alert_button_item, null) as TextView
                textView.textSize = mButtonTextSize
                var i = 0
                while (i < mButtonTitles!!.size) {
                    val title = mButtonTitles!![i]
                    buttonContentHeight += StringUtil.measureTextHeight(
                        title, textView.paint,
                        maxWidth
                    ) + mButtonTopBottomPadding * 2 + mDividerHeight
                    if (mButtonTitles!!.size <= 2) break
                    i++
                }
                if (buttonContentHeight > 0) {
                    buttonContentHeight -= mDividerHeight
                }
            }
        }
        var maxHeight: Int =
            mContext.getResources().getDisplayMetrics().heightPixels - mDialogPadding * 2 -
                    cancelButtonHeight - mScrollContainer!!.paddingBottom - mScrollContainer!!.paddingTop
        if (mContentDivider.getVisibility() === View.VISIBLE) {
            maxHeight -= mDividerHeight
        }
        if (topContentHeight + buttonContentHeight > maxHeight) { //内容太多了
            val half = maxHeight / 2
            if (topContentHeight > half && buttonContentHeight < half) {
                setScrollViewHeight(maxHeight - buttonContentHeight)
            } else if (topContentHeight < half && buttonContentHeight > half) {
                setRecyclerViewHeight(maxHeight - topContentHeight)
            } else {
                setRecyclerViewHeight(half)
                setScrollViewHeight(half)
            }
        }
    }

    //设置scrollview
    private fun setScrollViewHeight(height: Int) {
        val params = mScrollView!!.layoutParams
        params.height = height
        mScrollView!!.layoutParams = params
    }

    //设置recyclerView
    private fun setRecyclerViewHeight(height: Int) {
        val params = mRecyclerView!!.layoutParams
        params.height = height
        mRecyclerView!!.layoutParams = params
    }

    //显示弹窗
    fun show() {
        if (mDialog == null) {
            initView()
        }
        if (!mDialog.isShowing()) {
            mDialog.show()
        }
    }

    //隐藏弹窗
    override fun dismiss() {
        if (mDialog.isShowing()) {
            mDialog.dismiss()
        }
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

    //弹窗UI回调
    interface AlertUIHandler {
        //弹窗消失
        fun onDismiss(controller: AlertController?)

        //该按钮是否具有警示意义 从左到右，从上到下
        fun shouldDestructive(controller: AlertController?, index: Int): Boolean

        //该按钮是否可以点击 从左到右，从上到下
        fun shouldEnable(controller: AlertController?, index: Int): Boolean
    }
}