package com.lhx.glakit.extension

import android.os.Build
import android.os.Parcel

/**
 * Parcelable 扩展
 */

fun Parcel.getString(): String {
    return readString() ?: ""
}

@Suppress("deprecation")
fun <T> Parcel.readParcelableCompat(clazz: Class<T>): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        readParcelable(clazz.classLoader, clazz)
    } else {
        readParcelable(clazz.classLoader)
    }
}