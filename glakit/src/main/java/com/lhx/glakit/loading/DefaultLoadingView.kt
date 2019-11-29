package com.lhx.glakit.loading

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.lhx.glakit.drawable.DrawableUtils
import com.lhx.glakit.drawable.LoadingDrawable
import com.lhx.glakit.utils.SizeUtils
import kotlinx.android.synthetic.main.default_loading_view.view.*

/**
 * 默认的loading
 */
class DefaultLoadingView: LoadingView {

    //菊花
    protected var loadingDrawable: LoadingDrawable;

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    override fun onFinishInflate() {
        super.onFinishInflate()

        DrawableUtils.setDrawable(container, SizeUtils.pxFormDip(8f, context), Color.parseColor("#4c4c4c"))
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mImageView = findViewById(R.id.loading);
        mLoadingDrawable = new LoadingDrawable(getContext());
        mImageView.setImageDrawable(mLoadingDrawable);

        mTextView = findViewById(R.id.text_view);
        mContainer = findViewById(R.id.container);

        CornerBorderDrawable.setDrawable(mContainer, 15, Color.parseColor("#4c4c4c"));
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mLoadingDrawable.start();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mLoadingDrawable.stop();
    }

    public TextView getTextView() {
        return mTextView;
    }

    @NonNull
    @Override
    public View getContentView() {
        return mContainer;
    }
}

}