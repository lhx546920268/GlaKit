package com.lhx.glakit.scan.core

import android.content.pm.PackageManager
import com.lhx.glakit.utils.Size
import kotlin.math.abs

@Suppress("deprecation")
internal class ScanCamera1(cameraManager: CameraManager,
                           callBack: CallBack
)
    : Camera(cameraManager, callBack)
{

    //相机
    private var camera: android.hardware.Camera? = null


    //获取当前预览大小
    override fun getPreviewSize(): Size? {
        return if (camera != null) {
            val size = camera!!.parameters.previewSize
            Size(size.width, size.height)
        } else null
    }

    //获取预览格式
    override fun getPreviewFormat(): Int? {
        return if (camera != null) {
            camera!!.parameters.previewFormat
        } else null
    }

    //获取预览格式字符串
    override fun getPreviewFormatString(): String? {
        return if (camera != null) {
            camera!!.parameters.get("preview-format")
        } else null
    }

    override fun open() {
        if (camera == null) {
            camera = android.hardware.Camera.open()
        }
        if (camera == null) {
            callBack.onCameraOpen(false)
        } else {
            try {
                camera?.apply {
                    setPreviewTexture(surface)
                    setOptimalPreviewSize(surfaceWidth, surfaceHeight)
                    setDisplayOrientation(90)
                    setPreviewCallback(previewCallback)
                    startPreview()
                }
                callBack.onCameraOpen(true)
            } catch (e: Exception) {
                callBack.onCameraOpen(false)
            }
        }
    }

    override fun start() {
        camera?.startPreview()
    }

    override fun stop() {
        camera?.stopPreview()
    }

    override fun setOpenLamp(open: Boolean): Boolean {
        if (camera != null) {
            //判断设备是否支持闪光灯
            var support = false
            val featureInfos = context.packageManager.systemAvailableFeatures
            for (info in featureInfos) {
                if (PackageManager.FEATURE_CAMERA_FLASH == info.name) {
                    support = true
                    break
                }
            }

            if (support) {
                val parameters = camera!!.parameters
                parameters.flashMode = if (open) android.hardware.Camera.Parameters.FLASH_MODE_TORCH
                else android.hardware.Camera.Parameters.FLASH_MODE_AUTO
                camera!!.parameters = parameters
                return true
            }
        }
        return false
    }

    override fun setOptimalPreviewSize(width: Int, height: Int) {
        if (camera != null) {
            val parameters = camera!!.parameters
            val sizes = parameters.supportedPreviewSizes
            var landScapeWidth = width
            var landScapeHeight = height

            if (isPortrait()) {
                landScapeWidth = height
                landScapeHeight = width
            }

            var optimalSize: android.hardware.Camera.Size? = null

            //如果存在相等的尺寸 直接设置
            for (size in sizes) {
                if (size.width == landScapeWidth && size.height == landScapeHeight) {
                    optimalSize = size
                    break
                }
            }
            if (optimalSize == null) {

                //使用宽高比例最接近的
                val ratio = landScapeWidth.toFloat() / landScapeHeight.toFloat()
                var differ = Float.MAX_VALUE
                for (size in sizes) {
                    val r = size.width.toFloat() / size.height.toFloat()
                    val d = abs(ratio - r)
                    if (d < differ) {
                        differ = d
                        optimalSize = size
                    }
                }
            }
            if (optimalSize != null) {
                parameters.setPreviewSize(optimalSize.width, optimalSize.height)
                camera!!.parameters = parameters
            }
        }
    }
}