package com.lhx.glakit.scan.core

import android.graphics.Rect
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import java.util.*

/**
 * 扫描图片解码
 */
class ScanDecoder {

    //解码器
    private val _reader = MultiFormatReader()

    init {
        //解码格式
        val formats = Vector<BarcodeFormat>()

        //条形码
        formats.add(BarcodeFormat.UPC_A)
        formats.add(BarcodeFormat.UPC_E)
        formats.add(BarcodeFormat.EAN_13)
        formats.add(BarcodeFormat.EAN_8)
        formats.add(BarcodeFormat.RSS_14)
        formats.add(BarcodeFormat.CODE_39)
        formats.add(BarcodeFormat.CODE_93)
        formats.add(BarcodeFormat.CODE_128)

        //二维码
        formats.add(BarcodeFormat.QR_CODE)
        setBarcodeFormat(formats)
    }

    fun setBarcodeFormat(formats: Vector<BarcodeFormat>) {
        val hints = HashMap<DecodeHintType, Any>()
        hints[DecodeHintType.POSSIBLE_FORMATS] = formats
        _reader.setHints(hints)
    }

    //解码
    @Suppress("NAME_SHADOWING")
    fun decode(data: ByteArray?, rect: Rect, width: Int, height: Int): Result? {
        data ?: return null
        var width = width
        var height = height

        //将相机获取的图片数据转化为binaryBitmap格式
        val rotatedData = ByteArray(data.size)
        for (y in 0 until height) {
            for (x in 0 until width)
                rotatedData[x * height + height - y - 1] = data[x + y * width]
        }
        val tmp = width
        width = height
        height = tmp

        val source = YUVLuminanceSource(data, width, height,rect.left, rect.top, rect.width(), rect.height())
        val bitmap = BinaryBitmap(HybridBinarizer(source))

        var rawResult: Result? = null
        try {
            //解析转化后的图片，得到结果
            rawResult = _reader.decodeWithState(bitmap)
        } catch (e: ReaderException) {
            // continue
        } finally {
            _reader.reset()
        }

        return rawResult
    }
}