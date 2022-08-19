package com.lhx.glakit.image

import android.app.Activity
import android.content.Context
import android.graphics.BitmapFactory
import com.lhx.glakit.base.widget.ValueCallback
import com.lhx.glakit.utils.AlertUtils
import com.lhx.glakit.utils.StringUtils
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.language.LanguageConfig
import com.luck.picture.lib.listener.OnResultCallbackListener

/**
 * 选择图片回调
 */
typealias ImagePickerCallback = ValueCallback<List<ImageData>>

/**
 * 图片选择器
 */
class ImagePicker(val mode: Int = PictureConfig.MULTIPLE): OnResultCallbackListener<LocalMedia> {

    //回调
    private var callback: ImagePickerCallback? = null

    //配置
    var config: ValueCallback<ImagePickerModel>? = null

    fun pick(context: Context, count: Int, callback: ImagePickerCallback) {
        this.callback = callback
        AlertUtils.actionSheet(
            title = "选择图片",
            cancelButtonTitle = "取消",
            buttonTitles = arrayOf("拍照", "相册"),
            onItemClick = {
                when (it) {
                    0 -> {
                        val model = ImagePickerConfig
                            .create(context as Activity)
                            .openCamera(PictureMimeType.ofImage())
                            .setLanguage(LanguageConfig.CHINESE)
                            .cropImageWideHigh(1080, 0)
                        open(model)
                    }
                    1 -> {
                        val model = ImagePickerConfig
                            .create(context as Activity)
                            .openGallery(PictureMimeType.ofImage())
                            .setLanguage(LanguageConfig.CHINESE)
                            .selectionMode(mode)
                            .maxSelectNum(count)
                            .imageSpanCount(4)
                            .isCamera(false)
                            .cropImageWideHigh(1080, 0)
                        open(model)
                    }
                }
            }
        ).show()
    }

    private fun open(model: ImagePickerModel) {
        model.compressQuality(90)
             .compress(true)

        config?.also {
            it(model)
        }

        if (!model.isCompress) {
            model.isAndroidQTransform(true)
        }
        model.imageEngine(GlideEngine.sharedEngine)
            .forResult(this)
    }

    override fun onResult(result: MutableList<LocalMedia>?) {
        callback?.also {
            if (!result.isNullOrEmpty()) {
                val list = ArrayList<ImageData>()
                for (media in result) {
                    val path = if (media.isCompressed) {
                        media.compressPath
                    } else if (media.isCut) {
                        media.cutPath
                    } else if (!StringUtils.isEmpty(media.androidQToPath)) {
                        media.androidQToPath
                    } else {
                        media.realPath
                    }

                    var width = media.width
                    var height = media.height
                    if (width == 0 || height == 0) {
                        val options = BitmapFactory.Options()
                        options.inJustDecodeBounds = true
                        BitmapFactory.decodeFile(media.path, options)
                        width = options.outWidth
                        height = options.outHeight
                    }

                    list.add(ImageData(path, width, height))

                }
                it(list)
            }
        }
    }

    override fun onCancel() {

    }
}

class ImageData(val path: String, val width: Int, val height: Int)