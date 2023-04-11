package com.lhx.glakit.image.compress

import android.annotation.SuppressLint
import android.util.Log
import java.util.*

@Suppress("unchecked_cast")
class LruArrayPool(val maxSize: Int = DEFAULT_SIZE): ArrayPool {

    companion object {
        const val DEFAULT_SIZE = 4 * 1024 * 1024

        /**
         * The maximum number of times larger an int array may be to be than a requested size to eligible
         * to be returned from the pool.
         */
        const val MAX_OVER_SIZE_MULTIPLE = 8

        /**
         * Used to calculate the maximum % of the total pool size a single byte array may consume.
         */
         private const val SINGLE_ARRAY_MAX_SIZE_DIVISOR = 2
    }

    private val groupedMap: GroupedLinkedMap<Key, Any> = GroupedLinkedMap()
    private val keyPool = KeyPool()
    private val sortedSizes: HashMap<Class<*>, NavigableMap<Int, Int>> = HashMap()
    private val adapters: HashMap<Class<*>, ArrayAdapterInterface<*>?> =
        HashMap()
    private var currentSize = 0

    @Synchronized
    override fun <T> put(array: T) {
        val arrayClass = array!!::class.java as Class<T>
        val arrayAdapter = getAdapterFromType(arrayClass)
        val size: Int = arrayAdapter.getArrayLength(array)
        val arrayBytes: Int = size * arrayAdapter.getElementSizeInBytes()
        if (!isSmallEnoughForReuse(arrayBytes)) {
            return
        }
        val key = keyPool[size, arrayClass]
        groupedMap.put(key, array)
        val sizes = getSizesForAdapter(arrayClass)
        val current = sizes[key.size]
        sizes[key.size] = if (current == null) 1 else current + 1
        currentSize += arrayBytes
        evict()
    }


    @Synchronized
    override operator fun <T> get(size: Int, arrayClass: Class<T>): T {
        val possibleSize = getSizesForAdapter(arrayClass).ceilingKey(size)
        val key = if (mayFillRequest(size, possibleSize)) {
            keyPool[possibleSize!!, arrayClass]
        } else {
            keyPool[size, arrayClass]
        }
        return getForKey(key, arrayClass)
    }

    private fun <T> getForKey(key: Key, arrayClass: Class<T>): T {
        val arrayAdapter: ArrayAdapterInterface<T> = getAdapterFromType(arrayClass)
        var result: T? = getArrayForKey<T>(key)
        if (result != null) {
            currentSize -= arrayAdapter.getArrayLength(result) * arrayAdapter.getElementSizeInBytes()
            decrementArrayOfSize(arrayAdapter.getArrayLength(result), arrayClass)
        }
        if (result == null) {
            if (Log.isLoggable(arrayAdapter.getTag(), Log.VERBOSE)) {
                Log.v(arrayAdapter.getTag(), "Allocated " + key.size + " bytes")
            }
            result = arrayAdapter.newArray(key.size)
        }
        return result!!
    }

    // Our cast is safe because the Key is based on the type.
    private fun <T> getArrayForKey(key: Key): T? {
        return groupedMap.get(key) as T?
    }

    private fun isSmallEnoughForReuse(byteSize: Int): Boolean {
        return byteSize <= maxSize / SINGLE_ARRAY_MAX_SIZE_DIVISOR
    }

    private fun mayFillRequest(requestedSize: Int, actualSize: Int?): Boolean {
        return actualSize != null
                && (isNoMoreThanHalfFull() || (actualSize <= MAX_OVER_SIZE_MULTIPLE * requestedSize))
    }

    private fun isNoMoreThanHalfFull(): Boolean {
        return currentSize == 0 || (maxSize / currentSize >= 2)
    }

    @Synchronized
    override fun clearMemory() {
        evictToSize(0)
    }


    private fun evict() {
        evictToSize(maxSize)
    }

    @SuppressLint("RestrictedApi")
    private fun evictToSize(size: Int) {
        while (currentSize > size) {
            val evicted = groupedMap.removeLast()
            if (evicted != null) {
                val arrayAdapter = getAdapterFromObject(evicted)
                currentSize -= arrayAdapter.getArrayLength(evicted) * arrayAdapter.getElementSizeInBytes()
                decrementArrayOfSize(arrayAdapter.getArrayLength(evicted), evicted.javaClass)
                if (Log.isLoggable(arrayAdapter.getTag(), Log.VERBOSE)) {
                    Log.v(arrayAdapter.getTag(), "evicted: " + arrayAdapter.getArrayLength(evicted))
                }
            }
        }
    }

    private fun decrementArrayOfSize(size: Int, arrayClass: Class<*>) {
        val sizes = getSizesForAdapter(arrayClass)
        val current = sizes[size]
            ?: throw NullPointerException(
                "Tried to decrement empty size, size: $size, this: $this"
            )
        if (current == 1) {
            sizes.remove(size)
        } else {
            sizes[size] = current - 1
        }
    }

    private fun getSizesForAdapter(arrayClass: Class<*>): NavigableMap<Int, Int> {
        var sizes = sortedSizes[arrayClass]
        if (sizes == null) {
            sizes = TreeMap()
            sortedSizes[arrayClass] = sizes
        }
        return sizes
    }

    private fun <T> getAdapterFromObject(obj: T): ArrayAdapterInterface<T> {
        return getAdapterFromType(obj!!::class.java) as ArrayAdapterInterface<T>
    }

    private fun <T> getAdapterFromType(arrayPoolClass: Class<T>): ArrayAdapterInterface<T> {
        var adapter: ArrayAdapterInterface<*>? = adapters[arrayPoolClass]
        if (adapter == null) {
            adapter = when(arrayPoolClass) {
                IntArray::class.java -> IntegerArrayAdapter()
                ByteArray::class.java -> ByteArrayAdapter()
                else -> throw IllegalArgumentException("No array pool found for: " + arrayPoolClass.simpleName)
            }
            adapters[arrayPoolClass] = adapter
        }
        return adapter as ArrayAdapterInterface<T>
    }

    // VisibleForTesting
    fun getCurrentSize(): Int {
        var currentSize = 0
        for (type in sortedSizes.keys) {
            for (size in sortedSizes[type]!!.keys) {
                val adapter: ArrayAdapterInterface<*> = getAdapterFromType(type)
                currentSize += size * sortedSizes[type]!![size]!! * adapter.getElementSizeInBytes()
            }
        }
        return currentSize
    }

    private class KeyPool: BaseKeyPool<Key>() {
        operator fun get(size: Int, arrayClass: Class<*>?): Key {
            val result = get()
            result.init(size, arrayClass)
            return result
        }

        override fun create(): Key {
            return Key(this)
        }
    }

    private class Key constructor(private val pool: KeyPool): PoolAble {
        var size = 0
        private var arrayClass: Class<*>? = null
        fun init(length: Int, arrayClass: Class<*>?) {
            size = length
            this.arrayClass = arrayClass
        }

        override fun equals(other: Any?): Boolean {
            if (other is Key) {
                return size == other.size && arrayClass == other.arrayClass
            }
            return false
        }

        override fun toString(): String {
            return "Key{" + "size=" + size + "array=" + arrayClass + '}'
        }

        override fun offer() {
            pool.offer(this)
        }

        override fun hashCode(): Int {
            return 31 * size + if (arrayClass != null) arrayClass.hashCode() else 0
        }
    }
}