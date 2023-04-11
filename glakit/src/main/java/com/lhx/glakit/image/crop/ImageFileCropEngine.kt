package com.lhx.glakit.image.crop

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lhx.glakit.image.ImagePickerConfig
import com.luck.picture.lib.engine.CropFileEngine
import com.luck.picture.lib.utils.ActivityCompatHelper
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.UCropImageEngine

/**
 * 图片裁剪
 */
class ImageFileCropEngine(val config: ImagePickerConfig): CropFileEngine {

    /**
     * 配制UCrop，可根据需求自我扩展
     *
     * @return
     */
    private fun buildOptions(): UCrop.Options {
        val options = UCrop.Options()
        options.setShowCropFrame(true)
        options.setShowCropGrid(false)
        options.setHideBottomControls(true)
        options.setCompressionQuality(config.compressQuality)
        options.withAspectRatio(config.cropWidth.toFloat(), config.cropHeight.toFloat())
        options.withMaxResultSize(config.cropWidth, config.cropHeight)

        return options
    }

    override fun onStartCrop(
        fragment: Fragment,
        srcUri: Uri?,
        destinationUri: Uri?,
        dataSource: java.util.ArrayList<String?>?,
        requestCode: Int
    ) {
        val options: UCrop.Options = buildOptions()
        val uCrop = UCrop.of(srcUri!!, destinationUri!!, dataSource)
        uCrop.withOptions(options)
        uCrop.setImageEngine(object : UCropImageEngine {
            override fun loadImage(context: Context, url: String, imageView: ImageView) {
                if (!ActivityCompatHelper.assertValidRequest(context)) {
                    return
                }
                Glide.with(context).load(url).override(180, 180).into(imageView)
            }

            override fun loadImage(
                context: Context,
                url: Uri,
                maxWidth: Int,
                maxHeight: Int,
                call: UCropImageEngine.OnCallbackListener<Bitmap>
            ) {
                Glide.with(context).asBitmap().load(url).override(maxWidth, maxHeight)
                    .into(object : CustomTarget<Bitmap?>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap?>?
                        ) {
                            call.onCall(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            call.onCall(null)
                        }
                    })
            }
        })
        uCrop.start(fragment.requireActivity(), fragment, requestCode)
    }
}