package com.lhx.glakit.image

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import com.lhx.glakit.base.widget.ValueCallback
import com.lhx.glakit.dialog.LoadingDialog
import com.lhx.glakit.image.compress.ImageCompressFileEngine
import com.lhx.glakit.image.crop.ImageFileCropEngine
import com.lhx.glakit.utils.AlertUtils
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnCustomLoadingListener
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.luck.picture.lib.language.LanguageConfig

/**
 * 选择图片回调
 */
typealias ImagePickerCallback = ValueCallback<List<ImageData>>

/**
 * 图片选择器
 */
class ImagePicker(val allowMultiSelection: Boolean): OnResultCallbackListener<LocalMedia>, OnCustomLoadingListener {

    //回调
    private var callback: ImagePickerCallback? = null

    //配置
    var config = ImagePickerConfig()

    fun pick(context: Context, count: Int, callback: ImagePickerCallback) {
        this.callback = callback
        AlertUtils.actionSheet(
            title = "选择图片",
            cancelButtonTitle = "取消",
            buttonTitles = arrayOf("拍照", "相册"),
            onItemClick = {
                when (it) {
                    0 -> openCamera(context)
                    1 -> openGallery(context, count)
                }
            }
        ).show()
    }

    private fun openCamera(context: Context) {
        PictureSelector.create(context)
            .openCamera(SelectMimeType.ofImage())
            .setLanguage(LanguageConfig.CHINESE)
            .setCompressEngine(getImageCompressEngine())
            .setCustomLoadingListener(this)
            .setCropEngine(getCropEngine())
            .forResult(this)
    }

    private fun openGallery(context: Context, count: Int) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.sharedEngine)
            .setLanguage(LanguageConfig.ENGLISH)
            .setCompressEngine(getImageCompressEngine())
            .setCustomLoadingListener(this)
            .setCropEngine(getCropEngine())
            .setSelectionMode(if (allowMultiSelection) SelectModeConfig.MULTIPLE else SelectModeConfig.SINGLE)
            .isDirectReturnSingle(true)
            .setMaxSelectNum(count)
            .setImageSpanCount(4)
            .isDisplayCamera(false)
            .forResult(this)
    }

    private fun getImageCompressEngine(): CompressFileEngine? {
        return if (config.needCompress) ImageCompressFileEngine(config) else null
    }

    private fun getCropEngine(): CropFileEngine? {
        return if (config.enableCrop) ImageFileCropEngine(config) else null
    }

    override fun create(context: Context): Dialog {
        return LoadingDialog(context)
    }

    override fun onResult(result: ArrayList<LocalMedia>?) {
        callback?.also {
            if (!result.isNullOrEmpty()) {
                val list = ArrayList<ImageData>()
                for (media in result) {
                    val path = media.availablePath
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(path, options)
                    list.add(ImageData(path, options.outWidth, options.outHeight))
                }
                it(list)
            }
        }
    }

    override fun onCancel() {

    }
}


class ImageData(val path: String, val width: Int, val height: Int)