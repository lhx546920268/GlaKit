package com.lhx.glakit.popup

import android.animation.ValueAnimator
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import com.lhx.glakit.extension.visible

/**
 * 弹窗动画
 */
interface PopupAnimation {

    //动画方式
    var animationStyle: AnimationStyle

    //背景
    val popupBackgroundView: View

    //内容
    val popupContentView: View?

    //容器
    val popupContainer: View

    //显示动画
    fun executeShowAnimation() {
        popupContainer.post {
            popupContainer.visible()
            when(animationStyle){
                AnimationStyle.SCALE -> {
                    popupContentView?.apply {
                        val params = layoutParams
                        val animation = ValueAnimator.ofInt(0, measuredHeight)
                        animation.duration = 250
                        animation.addUpdateListener {
                            params.height = it.animatedValue as Int
                            requestLayout()
                        }
                        animation.start()
                    }
                }
                AnimationStyle.TRANSLATE -> {
                    val animation = TranslateAnimation(
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f,
                        Animation.RELATIVE_TO_SELF, -1f,
                        Animation.RELATIVE_TO_SELF, 0f)
                    animation.duration = 250
                    popupContentView?.startAnimation(animation)
                }
                AnimationStyle.CUSTOM -> executeCustomAnimation(true)
            }

            val alphaAnimation = AlphaAnimation(0f, 1.0f)
            alphaAnimation.duration = 250
            alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                }

                override fun onAnimationEnd(animation: Animation?) {
                    onPopupShow()
                }

                override fun onAnimationRepeat(animation: Animation?) {
                }
            })
            popupBackgroundView.startAnimation(alphaAnimation)
        }
    }

    //消失动画
    fun executeDismissAnimation() {
        when(animationStyle){
            AnimationStyle.SCALE -> {
                popupContentView?.apply {
                    val params = layoutParams
                    val animation = ValueAnimator.ofInt(measuredHeight, 0)
                    animation.duration = 250
                    animation.addUpdateListener {
                        params.height = it.animatedValue as Int
                        requestLayout()
                    }
                    animation.start()
                }
            }
            AnimationStyle.TRANSLATE -> {
                val animation = TranslateAnimation(
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0f,
                    Animation.RELATIVE_TO_SELF, -1f)
                animation.duration = 250
                popupContentView?.startAnimation(animation)
            }
            AnimationStyle.CUSTOM -> executeCustomAnimation(false)
        }

        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = 250
        alphaAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                onPopupDismiss()
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
        popupBackgroundView.startAnimation(alphaAnimation)
    }

    //自定义动画
    fun executeCustomAnimation(isShow: Boolean){

    }

    //弹窗显示了
    fun onPopupShow() {}

    //弹窗消失了
    fun onPopupDismiss() {}
}