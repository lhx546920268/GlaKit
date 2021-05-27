package com.lhx.glakit.extension

/**
 * 列表扩展
 */

/**
 * 安全获取，防止越界
 */
fun <T> List<T>.getSafely(index: Int): T? {
    return if (index < size) this[index] else null
}

fun <T> List<T>.lastSafely(): T? {
    return if (isNotEmpty()) last() else null
}