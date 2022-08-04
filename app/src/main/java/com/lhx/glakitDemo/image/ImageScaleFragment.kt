package com.lhx.glakitDemo.image

import android.os.Bundle
import android.view.LayoutInflater
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.setOnSingleListener
import com.lhx.glakit.image.GlideEngine
import com.lhx.glakit.image.ImagePicker
import com.lhx.glakit.image.ImagePickerConfig
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.ImageScaleFragmentBinding
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.listener.OnResultCallbackListener

class ImageScaleFragment: BaseFragment() {

    private val imagePicker by lazy { ImagePicker() }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        
        setContainerContentView(R.layout.image_scale_fragment)
        val binding = ImageScaleFragmentBinding.bind(getContainerContentView()!!)
        binding.camera.setOnSingleListener {
            imagePicker.pick(requireActivity(), 9) {

            }
        }
    }
}