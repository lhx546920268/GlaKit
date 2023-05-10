package com.lhx.glakit.pager

import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.lhx.glakit.timer.CountDownTimer
import com.lhx.glakit.viewholder.RecyclerViewHolder

//设置当前位置，类似viewPager的
fun ViewPager2.setCurrentItem(
    item: Int,
    duration: Long,
    interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
    pagePxWidth: Int = width // Default value taken from getWidth() from ViewPager2 view
) {
    val pxToDrag: Int = pagePxWidth * (item - currentItem)
    val animator = ValueAnimator.ofInt(0, pxToDrag)
    var previousValue = 0
    animator.addUpdateListener { valueAnimator ->
        val currentValue = valueAnimator.animatedValue as Int
        val currentPxToDrag = (currentValue - previousValue).toFloat()
        fakeDragBy(-currentPxToDrag)
        previousValue = currentValue
    }

    animator.addListener (onStart = {
        beginFakeDrag()
    }, onEnd = {
        endFakeDrag()
    })

    animator.interpolator = interpolator
    animator.duration = duration
    animator.start()
}

/**
 * ViewPager2 的无限循环轮播
 */
@Suppress("unused_parameter")
abstract class CyclePageAdapter2(val viewPager2: ViewPager2): RecyclerView.Adapter<RecyclerViewHolder>() {

    init {
        this.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                if (shouldAutoPlay && realCount <= 1) {
                    stopAutoPlayTimer()
                }
            }
        })

        viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                if (shouldCycle) {
                    if (realCount > 1) {
                        if (position == 0) {
                            _targetPosition = realCount
                        } else if (position == realCount + 1) {
                            _targetPosition = 1
                        }
                    }
                } else {
                    _targetPosition = position
                }
            }

            override fun onPageScrollStateChanged(state: Int) {

                //当不是动画中改变当前显示view的位置
                if (state != ViewPager.SCROLL_STATE_SETTLING && _targetPosition != -1) {
                    viewPager2.setCurrentItem(_targetPosition, false)
                    _targetPosition = -1
                }

                //用户滑动时关闭自动轮播
                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    stopAutoPlayTimer()
                } else {
                    startAutoPlayTimer()
                }
            }
        })

        viewPager2.addOnAttachStateChangeListener(object: View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                startAutoPlayTimer()
            }

            override fun onViewDetachedFromWindow(v: View) {
                stopAutoPlayTimer()
            }
        })
    }

    final override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        onBindItem(holder, getRealPosition(position))
    }

    final override fun getItemId(position: Int): Long {
        return fetchItemId(getRealPosition(position))
    }

    final override fun getItemViewType(position: Int): Int {
        return fetchItemViewType(getRealPosition(position))
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        scrollToFirstPosition()
    }

    override fun getItemCount(): Int {
        //当只有一个view时，不需要循环
        return if (realCount > 1 && shouldCycle) realCount + 2 else realCount
    }

    //需要移动到的位置
    private var _targetPosition = -1

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
    private var _countDownTimer: CountDownTimer? = null

    //是否需要循环
    var shouldCycle = true
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            if(value != field){
                field = value
                notifyDataSetChanged()
            }
        }

    //移动到第一个位置
    fun scrollToFirstPosition() {
        if (realCount > 1) {
            viewPager2.setCurrentItem(if (shouldCycle) 1 else 0, false)
            _targetPosition = -1
            startAutoPlayTimer()
        }
    }

    //移动到某个位置
    fun scrollToPosition(position: Int, smooth: Boolean) {
        if (realCount > 1 && position >= 0 && position < realCount) {
            if (shouldCycle) {
                viewPager2.setCurrentItem(position + 1, smooth)
            } else {
                viewPager2.setCurrentItem(position, smooth)
            }
        }
    }

    //通过viewPager 位置获取真实的数据源位置
    fun getRealPosition(position: Int): Int {
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

    //通过数据源位置获取布局位置
    fun getAdapterPosition(position: Int): Int {
        return if (realCount <= 1 || !shouldCycle) {
            position
        } else {
            position + 1
        }
    }

    //跑到下一页
    private fun nextPage() {
        var position: Int = viewPager2.currentItem
        position++
        if (position >= itemCount) {
            position = if (realCount != itemCount) 1 else 0
        }
        viewPager2.setCurrentItem(position, 300)
    }

    //开始自动轮播计时器
    private fun startAutoPlayTimer() {
        if (!shouldAutoPlay || realCount <= 1) return
        if (_countDownTimer == null) {
            _countDownTimer = object : CountDownTimer(
                COUNT_DOWN_INFINITE,
                autoPlayInterval.toLong()
            ) {
                override fun onFinish() {}
                override fun onTick(millisLeft: Long) {
                    nextPage()
                }
            }
        }
        if (_countDownTimer!!.isExecuting) return
        _countDownTimer!!.start()
    }

    //停止自动轮播计时器
    private fun stopAutoPlayTimer() {
        if (_countDownTimer != null) {
            _countDownTimer!!.stop()
            _countDownTimer = null
        }
    }

    //获取真实的数量
    abstract val realCount: Int

    //和recyclerView一样
    abstract fun onBindItem(holder: RecyclerViewHolder, position: Int)
    fun fetchItemId(position: Int): Long {
        return position.toLong()
    }

    fun fetchItemViewType(position: Int): Int {
        return 0
    }
}