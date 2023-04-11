
package com.lhx.glakit.image

class ImagePickerConfig {

    //裁剪尺寸
    var cropWidth = 0
    var cropHeight = 0

    val enableCrop: Boolean
        get() = cropWidth > 0 && cropHeight > 0

    //是否允许压缩
    var enableCompress = true

    //压缩时用来缩放的最大尺寸，0表示不缩放
    //比如宽度有值时，只有当图片宽度大于这个值，才缩放，高度根据宽度缩放的比例来缩放
    var maxWidth = 1024
    var maxHeight = 0

    //压缩比例
    var compressQuality = 90

    //小于这个值不压缩100kb
    var minimumCompressSize = 100

    val needCompress: Boolean
        get() = enableCompress && (maxWidth > 0 || maxHeight > 0 || compressQuality < 100)
}
