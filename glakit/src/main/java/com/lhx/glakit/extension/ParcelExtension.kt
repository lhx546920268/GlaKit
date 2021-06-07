package com.lhx.glakit.extension

import android.os.Parcel

/**
 * Parcelable 扩展
 */

fun Parcel.getString(): String {
    return readString() ?: ""
}