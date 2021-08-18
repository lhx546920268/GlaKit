package com.lhx.glakitDemo.nest

import android.content.Context
import android.hardware.SensorManager
import android.view.ViewConfiguration
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.ln

/**
 * 快速滑动帮助类 相关算法可以在OverScroller找到
 */
class FlingHelper(context: Context) {

    companion object {
        // Fling friction
        private val FLING_FRICTION = ViewConfiguration.getScrollFriction()

        // 减速率
        private val DECELERATION_RATE = (ln(0.78) / ln(0.9)).toFloat()
    }

    // A context-specific coefficient adjusted to physical values.
    private val physicalCoeff: Float

    init {
        val ppi = context.resources.displayMetrics.density * 160.0f
        physicalCoeff = (SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.37f // inch/meter
                * ppi
                * 0.84f) // look and feel tuning
    }

    //获取快速滑动的减速时间
    private fun getSplineDeceleration(velocity: Int): Double {
        return ln((0.35f * abs(velocity) / (FLING_FRICTION * physicalCoeff)).toDouble())
    }

    private fun getSplineDeceleration(distance: Double): Double {
        val decelMinusOne: Double = DECELERATION_RATE - 1.0
        return decelMinusOne * ln(distance / (FLING_FRICTION * physicalCoeff )) / DECELERATION_RATE
    }

    //获取快速滑动的距离
    fun getSplineFlingDistance(velocity: Int): Double {
        val l = getSplineDeceleration(velocity)
        val decelMinusOne: Double = DECELERATION_RATE - 1.0
        return FLING_FRICTION * physicalCoeff * exp(DECELERATION_RATE / decelMinusOne * l)
    }

    fun getSplineFlingVelocity(distance: Double): Int {
        val l = getSplineDeceleration(distance)
        return abs(exp(l) * FLING_FRICTION * physicalCoeff / 0.35).toInt()
    }

    //获取快速滑动的时间
    fun getSplineFlingDuration(velocity: Int): Int {
        val l = getSplineDeceleration(velocity)
        val decelMinusOne: Double = DECELERATION_RATE - 1.0
        return (1000.0 * exp(l / decelMinusOne)).toInt()
    }
}