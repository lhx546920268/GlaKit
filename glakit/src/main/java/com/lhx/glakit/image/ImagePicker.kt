package com.lhx.glakit.image

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import com.lhx.glakit.base.widget.ValueCallback
import com.lhx.glakit.utils.AlertUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.utils.PictureFileUtils

/**
 * 选择图片回调
 */
typealias ImagePickerCallback = ValueCallback<List<ImageData>>

/**
 * 图片选择器
 */
class ImagePicker(val mode: Int = SelectModeConfig.MULTIPLE): OnResultCallbackListener<LocalMedia> {

    //回调
    private var callback: ImagePickerCallback? = null

    //配置
//    var config: ValueCallback<ImagePickerModel>? = null

    fun pick(context: Context, count: Int, callback: ImagePickerCallback) {
        this.callback = callback
        AlertUtils.actionSheet(
            title = "选择图片",
            cancelButtonTitle = "取消",
            buttonTitles = arrayOf("拍照", "相册"),
            onItemClick = {
                when (it) {
                    0 -> {
//                        val model = ImagePickerConfig
//                            .create(context as Activity)
//                            .openCamera(PictureMimeType.ofImage())
//                            .setLanguage(LanguageConfig.CHINESE)
//                            .cropImageWideHigh(1080, 0)
//                        open(model)
                        PictureSelector.create(context)
                            .openCamera(SelectMimeType.ofImage())
                            .setLanguage(LanguageConfig.CHINESE)
                            .forResult(this)
                    }
                    1 -> {
//                        val model = ImagePickerConfig
//                            .create(context as Activity)
//                            .openGallery(PictureMimeType.ofImage())
//                            .setLanguage(LanguageConfig.CHINESE)
//                            .selectionMode(mode)
//                            .maxSelectNum(count)
//                            .imageSpanCount(4)
//                            .isCamera(false)
//                            .cropImageWideHigh(1080, 0)
//                        open(model)
                        PictureSelector.create(context)
                            .openGallery(SelectMimeType.ofImage())
                            .setLanguage(LanguageConfig.CHINESE)
                            .setSelectionMode(mode)
                            .setMaxSelectNum(count)
                            .setImageSpanCount(4)
                            .isDisplayCamera(false)
                            .forResult(this)
                    }
                }
            }
        ).show()
    }

//    private fun open(model: ImagePickerModel) {
//        model.compressQuality(90)
//             .compress(true)
//
//        config?.also {
//            it(model)
//        }
//
//        if (!model.isCompress) {
//            model.isAndroidQTransform(true)
//        }
//        model.imageEngine(GlideEngine.sharedEngine)
//            .forResult(this)
//    }


    override fun onResult(result: ArrayList<LocalMedia>?) {
        val tag = "ImagePicker"
        callback?.also {
            if (!result.isNullOrEmpty()) {
                val list = ArrayList<ImageData>()
                for (media in result) {
                    val path = if (media.isCompressed) {
                        media.compressPath
                    } else if (media.isCut) {
                        media.cutPath
                    }  else {
                        media.realPath
                    }

                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(media.path, options)

                    list.add(ImageData(path, options.outWidth, options.outHeight))


                    Log.i(tag, "文件名: " + media.fileName)
                    Log.i(tag, "是否压缩:" + media.isCompressed)
                    Log.i(tag, "压缩:" + media.compressPath)
                    Log.i(tag, "初始路径:" + media.path)
                    Log.i(tag, "绝对路径:" + media.realPath)
                    Log.i(tag, "是否裁剪:" + media.isCut)
                    Log.i(tag, "裁剪路径:" + media.cutPath)
                    Log.i(tag, "是否开启原图:" + media.isOriginal)
                    Log.i(tag, "原图路径:" + media.originalPath)
                    Log.i(tag, "沙盒路径:" + media.sandboxPath)
                    Log.i(tag, "水印路径:" + media.watermarkPath)
                    Log.i(tag, "视频缩略图:" + media.videoThumbnailPath)
                    Log.i(tag, "原始宽高: " + media.width + "x" + media.height)
                    Log.i(
                        tag,
                        "裁剪宽高: " + media.cropImageWidth + "x" + media.cropImageHeight
                    )
                    Log.i(
                        tag,
                        "文件大小: " + PictureFileUtils.formatAccurateUnitFileSize(media.size)
                    )
                    Log.i(tag, "文件时长: " + media.duration)
                }
                it(list)
            }
        }
    }

    override fun onCancel() {

    }
}

class ImageData(val path: String, val width: Int, val height: Int)