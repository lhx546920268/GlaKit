package com.lhx.glakitDemo.image

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.image.GlideEngine
import com.lhx.glakit.image.ImagePicker
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.ImageScaleFragmentBinding
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

class ImageScaleFragment: BaseFragment() {

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        setContainerContentView(R.layout.image_scale_fragment)
        val binding = ImageScaleFragmentBinding.bind(getContainerContentView()!!)

        binding.album.setOnClickListener {
            ImagePicker
                .create(this)
                .openGallery(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.sharedEngine)
                .maxSelectNum(9)
                .imageSpanCount(4)
                .selectionMode(PictureConfig.MULTIPLE)
                .isCamera(false)
                .isEnableCrop(false)
                .isCompress(true)
                .forResult(object: OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>) {

                    }

                    override fun onCancel() {
                    }
                })
        }

        binding.camera.setOnClickListener {
            ImagePicker
                .create(this)
                .openCamera(PictureMimeType.ofImage())
                .imageEngine(GlideEngine.sharedEngine)
                .isCompress(true)
                .forResult(object: OnResultCallbackListener<LocalMedia> {
                    override fun onResult(result: MutableList<LocalMedia>) {

                    }

                    override fun onCancel() {
                    }
                })
        }
    }
}