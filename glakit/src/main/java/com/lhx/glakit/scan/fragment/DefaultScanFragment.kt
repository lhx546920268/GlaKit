package com.lhx.glakit.scan.fragment

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.lhx.glakit.scan.widget.DefaultScanBackgroundView

/**
 * 默认的二维码扫描
 */
abstract class DefaultScanFragment: ScanFragment() {

    private val backgroundView by lazy { DefaultScanBackgroundView(requireContext()) }

    override fun getScanRect(width: Int, height: Int): Rect {
        return backgroundView.scanRect
    }

    override fun onCameraStart() {
        backgroundView.startScanAnimate()
    }

    override fun onCameraStop() {
        backgroundView.stopScanAnimate()
    }

    override fun getContentView(inflater: LayoutInflater, container: FrameLayout): View {
        return backgroundView
    }
}