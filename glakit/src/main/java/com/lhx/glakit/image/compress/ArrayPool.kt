package com.lhx.glakit.image.compress

interface ArrayPool {
    /**
     * Optionally adds the given array of the given type to the pool.
     *
     *
     * Arrays may be ignored, for example if the array is larger than the maximum size of the pool.
     */
    fun <T> put(array: T)

    /**
     * Returns a non-null array of the given type with a length >= to the given size.
     *
     *
     * If an array of the given size isn't in the pool, a new one will be allocated.
     *
     *
     * This class makes no guarantees about the contents of the returned array.
     *
     * @see .getExact
     */
    operator fun <T> get(size: Int, arrayClass: Class<T>): T?


    /** Clears all arrays from the pool.  */
    fun clearMemory()
}