package com.lhx.glakit.api

import com.lhx.glakit.base.widget.ValueCallback
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.Buffer
import okio.BufferedSink
import okio.ForwardingSink
import okio.buffer

//可观察进度的
internal class ObservableMultipartBody internal constructor(
    private val requestBody: RequestBody,
    private val progressCallback: ValueCallback<Double>
) : RequestBody() {

    override fun contentType(): MediaType? = requestBody.contentType()

    override fun contentLength(): Long = requestBody.contentLength()

    override fun writeTo(sink: BufferedSink) {
        val totalBytes = contentLength().toDouble()
        val forwardingSink = object : ForwardingSink(sink) {
            private var bytesWritten = 0.0

            override fun write(source: Buffer, byteCount: Long) {
                bytesWritten += byteCount
                progressCallback(bytesWritten / totalBytes)
                super.write(source, byteCount)
            }
        }

        val bufferedSink = forwardingSink.buffer()
        requestBody.writeTo(bufferedSink)
        bufferedSink.flush()
    }
}