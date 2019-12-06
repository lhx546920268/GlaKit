package com.lhx.glakit.pager

import android.os.CountDownTimer
import android.view.View.OnAttachStateChangeListener
import android.widget.Scroller
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener


/**
 * 循环轮播器 view可复用
 */
abstract class CyclePagerAdapter(viewPager: ViewPager) : ReusablePagerAdapter(viewPager), OnPageChangeListener {

    //真实的数量
    private var realCount = 0

    //需要移动到的位置
    private var targetPosition = -1

    //是否要自动轮播
    var shouldAutoPlay = false
    set(value) {
        if(value != field){
            field = value
            if (value) {
                startAutoPlayTimer()
            } else {
                stopAutoPlayTimer()
            }
        }
    }

    //自动轮播间隔 毫秒
    var autoPlayInterval = 5000

    //自动轮播计时器
    private var mCountDownTimer: CountDownTimer? = null

    //是否需要循环
    var shouldCycle = true
    set(value) {
        if(value != field){
            field = value
            notifyDataSetChanged()
        }
    }
    private var detachedFromWindow = false

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (shouldCycle) {
            if (realCount > 1) {
                if (position == 0) {
                    targetPosition = realCount
                } else if (position == realCount + 1) {
                    targetPosition = 1
                }
            }
        } else {
            targetPosition = position
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

        //当不是动画中改变当前显示view的位置
        if (state != ViewPager.SCROLL_STATE_SETTLING && targetPosition != -1) {
            viewPager.setCurrentItem(targetPosition, false)
            targetPosition = -1
        }

        //用户滑动时关闭自动轮播
        if (state == ViewPager.SCROLL_STATE_DRAGGING) {
            stopAutoPlayTimer()
        } else {
            startAutoPlayTimer()
        }
    }

    override fun getCount(): Int {

        //当只有一个view时，不需要循环
        return if (realCount > 1 && shouldCycle) realCount + 2 else realCount
    }


    //移动到第一个位置
    override fun scrollToFirstPosition() {
        if (realCount > 1) {
            viewPager.setCurrentItem(if (shouldCycle) 1 else 0, false)
            targetPosition = -1
            startAutoPlayTimer()
        }
    }

    //移动到某个位置
    fun scrollToPosition(position: Int, smooth: Boolean) {
        if (realCount > 1 && position >= 0 && position < realCount) {
            if (shouldCycle) {
                viewPager.setCurrentItem(position + 1, smooth)
            } else {
                viewPager.setCurrentItem(position, smooth)
            }
        }
    }

    /**
     * 通过viewPager 位置获取真实的数据源位置
     * @param position viewPager 位置
     * @return 真实的数据源位置
     */
    override fun getRealPosition(position: Int): Int {
        return if (realCount <= 1 || !shouldCycle) {
            position
        } else {
            when(position){
                0 -> {
                    realCount - 1
                }
                realCount + 1 -> {
                    0
                }
                else -> {
                    position - 1
                }
            }
        }
    }

    /**
     * 通过数据源位置获取布局位置
     * @param position 数据源位置
     * @return 布局位置
     */
    fun getAdapterPosition(position: Int): Int {
        return if (realCount <= 1 || !shouldCycle) {
            position
        } else {
            position + 1
        }
    }

    //跑到下一页
    private fun nextPage() {
        var position: Int = viewPager.currentItem
        position++
        if (position >= count) {
            position = if (realCount != count) 1 else 0
        }
        viewPager.setCurrentItem(position, true)
    }


    //开始自动轮播计时器
    private fun startAutoPlayTimer() {
        if (!shouldAutoPlay || realCount <= 1) return
        if (mCountDownTimer == null) {
            mCountDownTimer =
                object : CountDownTimer(
                    CountDownTimer.COUNT_DOWN_UNLIMITED,
                    mAutoPlayInterval.toLong()
                ) {
                    override fun onFinish() {}
                    override fun onTick(millisLeft: Long) {
                        nextPage()
                    }
                }
        }
        if (mCountDownTimer.isExecuting()) return
        mCountDownTimer!!.start()
    }

    //停止自动轮播计时器
    private fun stopAutoPlayTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.stop()
            mCountDownTimer = null
        }
    }

    fun notifyDataSetChanged() {
        super.notifyDataSetChanged()
        if (mShouldAutoPlay && mRealCount <= 1) {
            stopAutoPlayTimer()
        }
    }

    /**
     * 获取真实的数量
     * @return 真实的数量
     */
    abstract val realCount: Int

    //设置scroller
    private fun setScroller() {
        try {
            var field: Field? = null
            field = ViewPager::class.java.getDeclaredField("mScroller")
            field.setAccessible(true)
            val scroller =
                PagerScroller(mViewPager.getContext())
            field.set(mViewPager, scroller)
        } catch (e: NoSuchFieldException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    inner class PagerScroller(context: Context?) : Scroller(context) {
        private var mDuration = 1000
        fun setDuration(mDuration: Int) {
            this.mDuration = mDuration
        }

        override fun startScroll(
            startX: Int,
            startY: Int,
            dx: Int,
            dy: Int,
            duration: Int
        ) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }

        override fun startScroll(startX: Int, startY: Int, dx: Int, dy: Int) {
            super.startScroll(startX, startY, dx, dy, mDuration)
        }
    }

    companion object {
        const val TAG = "CyclePagerAdapter"
    }

    init {
        viewPager.addOnPageChangeListener(this)
        viewPager.addOnAttachStateChangeListener(object : OnAttachStateChangeListener() {
            override fun onViewAttachedToWindow(v: View?) {
                startAutoPlayTimer()
                if (mDetachedFromWindow) { //ViewPager 会在 AttachedToWindow 需要重新布局，会导致第一次smoothScroll没有动画
                    mViewPager.requestLayout()
                    mDetachedFromWindow = false
                }
            }

            override fun onViewDetachedFromWindow(v: View?) {
                mDetachedFromWindow = true
                stopAutoPlayTimer()
            }
        })
        setScroller()
    }
}