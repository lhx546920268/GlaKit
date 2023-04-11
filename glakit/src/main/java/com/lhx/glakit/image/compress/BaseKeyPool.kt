package com.lhx.glakit.image.compress

import java.util.*


abstract class BaseKeyPool<T: PoolAble> {

    companion object {
        private const val MAX_SIZE = 20

        fun <T> createQueue(size: Int): Queue<T> {
            return java.util.ArrayDeque(size)
        }
    }

    private val keyPool = createQueue<T>(MAX_SIZE)

    open fun get(): T {
        var result = keyPool.poll()
        if (result == null) {
            result = create()
        }
        return result
    }

    open fun offer(key: T) {
        if (keyPool.size < MAX_SIZE) {
            keyPool.offer(key)
        }
    }

    abstract fun create(): T
}