package com.lhx.glakitDemo.scan

import com.google.zxing.Result
import com.lhx.glakit.scan.fragment.DefaultScanFragment

class QRCodeScanFragment: DefaultScanFragment() {

    override fun onViewInitialize() {
        setBarTitle("扫一扫")
    }

    override fun onScanSuccess(result: Result) {
        showToast(result.text)
    }
}