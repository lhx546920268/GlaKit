package com.lhx.glakitDemo.image

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.setOnSingleListener
import com.lhx.glakit.image.ImageData
import com.lhx.glakit.image.ImagePicker
import com.lhx.glakit.image.loadImage
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.ImageScaleFragmentBinding

class ImageScaleFragment: BaseFragment() {

    private val imagePicker by lazy { ImagePicker(false) }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {

        setContainerContentView(R.layout.image_scale_fragment)
        imagePicker.config.apply {
            cropWidth = 640
            cropHeight = 302
        }
        val binding = ImageScaleFragmentBinding.bind(getContainerContentView()!!)
        binding.camera.setOnSingleListener {
            imagePicker.pick(requireActivity(), 1) {
                uploadImage(it.first())
            }
        }

//        var bitmap = BitmapFactory.decodeResource(requireActivity().resources, R.drawable.tupian)
//
//        val matrix = Matrix()
//        matrix.postRotate(180f)
//        matrix.postScale(0.5f, 0.5f)
//        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
//
//        val stream = ByteArrayOutputStream()
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)
//
//        val inputStream = ByteArrayInputStream(stream.toByteArray())
//        bitmap = BitmapFactory.decodeStream(inputStream)
//
//        val imageView = findViewById<ImageView>(R.id.bottom_image)!!
//        imageView.setImageBitmap(bitmap)
    }

    fun finalize() {
        println("${this::class.java.name} finalize")
    }

    private fun uploadImage(data: ImageData) {
        val imageView = findViewById<ImageView>(R.id.bottom_image)!!
        imageView.loadImage(data.path)
//        val task = UploadFileTask(requireContext(), File(data.path), data.width, data.height)
//        task.start()
    }
}