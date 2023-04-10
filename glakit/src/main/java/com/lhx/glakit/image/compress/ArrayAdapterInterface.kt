package com.lhx.glakit.image.compress

interface ArrayAdapterInterface<T> {

    /**
     * TAG for logging.
     */
    fun getTag(): String

    /**
     * Return the length of the given array.
     */
    fun getArrayLength(array: T): Int

    /**
     * Allocate and return an array of the specified size.
     */
    fun newArray(length: Int): T

    /**
     * Return the size of an element in the array in bytes (e.g. for int return 4).
     */
    fun getElementSizeInBytes(): Int
}