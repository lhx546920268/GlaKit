package com.lhx.glakit.section

/**
 * 上下左右偏移量
 */
data class EdgeInsets(var left: Int, var top: Int, var right: Int, var bottom: Int) {
    companion object {
        fun zero(): EdgeInsets = EdgeInsets(0, 0, 0, 0)
    }
}