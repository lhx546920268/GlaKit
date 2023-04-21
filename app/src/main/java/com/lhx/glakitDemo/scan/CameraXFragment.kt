package com.lhx.glakitDemo.scan

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Size
import android.view.LayoutInflater
import android.view.Surface
import androidx.annotation.RequiresApi
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.scan.core.ScanDecoder
import com.lhx.glakitDemo.R
import java.util.concurrent.Executors
import com.google.zxing.Result
import com.lhx.glakit.base.widget.ValueCallback

@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class CameraXFragment: BaseFragment() {

    private val previewView: PreviewView by lazy { findViewById(R.id.preview_view)!! }

    private val preview by lazy {
        val preview = Preview.Builder().build()
        preview.setSurfaceProvider(previewView.surfaceProvider)
        preview.targetRotation = Surface.ROTATION_0
        preview
    }

    private val analyzer by lazy {
        ImageAnalyzer({
            preview.resolutionInfo?.resolution
        }, {
            activity?.runOnUiThread {
                if (running) {
                    stopCamera()
                    showToast(it.text)

                    previewView.postDelayed( {
                        openCamera()
                    }, 2000)
                }
            }
        })
    }

    private val analysis by lazy {
        val analysis = ImageAnalysis.Builder()
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)
            .setTargetAspectRatio(AspectRatio.RATIO_16_9)
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
        analysis.setAnalyzer(Executors.newSingleThreadExecutor(), analyzer)
        analysis
    }

    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {

        setContainerContentView(R.layout.camera_x_fragment)
        val provider = ProcessCameraProvider.getInstance(requireContext())
        provider.addListener({
            cameraProvider?.unbindAll()
            cameraProvider = provider.get()
            openCamera()
        }, ContextCompat.getMainExecutor(requireContext()))

    }

    override fun onDestroy() {
        stopCamera()
        super.onDestroy()
    }

    private var pausing = false
    private var running = true

    private fun pausePreview() {
        if (running) {
            pausing = true
            running = false
            cameraProvider?.unbindAll()
            camera = null
        }
    }

    private fun openCamera() {
        if (!running) return
        pausing = false
        running = true
        camera = cameraProvider?.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analysis)
    }

    private fun stopCamera() {
        cameraProvider?.unbindAll()
        running = false
        camera = null
    }

    private class ImageAnalyzer(val resolution: (() -> Size?), val callback: ValueCallback<Result>): ImageAnalysis.Analyzer {

        private val scanDecoder by lazy { ScanDecoder() }

        @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
        override fun analyze(imageProxy: ImageProxy) {
            val image = imageProxy.image
            println("analyzer image width = ${imageProxy.width}, ${imageProxy.height}")
            if (image != null) {
                val buffer = image.planes[0].buffer
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes)
                val result = scanDecoder.decode(bytes, Rect(), imageProxy.width, imageProxy.height)
                if (result != null) {
                    callback(result)
                }
            }
            imageProxy.close()
        }

        override fun getDefaultTargetResolution(): Size? {
            return resolution()
        }
    }
}