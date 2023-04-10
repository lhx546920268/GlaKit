package com.lhx.glakit.image.compress

class ByteArrayAdapter: ArrayAdapterInterface<ByteArray> {

    companion object {
        private const val TAG = "ByteArrayPool"
    }

    override fun getTag(): String {
        return TAG
    }

    override fun getArrayLength(array: ByteArray): Int {
        return array.size
    }

    override fun newArray(length: Int): ByteArray {
        return ByteArray(length)
    }

    override fun getElementSizeInBytes(): Int {
        return 1
    }
}