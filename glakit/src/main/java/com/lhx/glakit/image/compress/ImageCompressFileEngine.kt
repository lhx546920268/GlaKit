package com.lhx.glakit.image.compress

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.text.TextUtils
import android.util.Log
import com.lhx.glakit.base.widget.ValueCallback
import com.lhx.glakit.image.ImageFormat
import com.lhx.glakit.image.ImagePickerConfig
import com.lhx.glakit.image.ImageUtils
import com.lhx.glakit.utils.Size
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnKeyValueResultCallbackListener
import com.luck.picture.lib.utils.BitmapUtils
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.min

/**
 * 压缩回调
 */
typealias ImageCompressEngineCallback = ValueCallback<List<LocalMedia>>

/**
 * 图片压缩
 */
class ImageCompressFileEngine(val config: ImagePickerConfig): CompressFileEngine {

    private lateinit var context: Context
    private var cacheDir: String? = null
    companion object {
        private const val CACHE_DISK_DIR = "compressedDir"
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onStartCompress(
        context: Context,
        source: ArrayList<Uri>,
        call: OnKeyValueResultCallbackListener?
    ) {

        this.context = context
        GlobalScope.launch(Dispatchers.IO) {
            val files = HashMap<String, File?>()
            for (uri in source) {
                val provider = streamProviderFromMedia(uri)
                val file: File? = try {
                    compress(provider)
                }finally {
                    provider.close()
                }
                files[provider.getPath()] = file
            }

            withContext(Dispatchers.Main) {
                for (entry in files) {
                    call?.onCallback(entry.key, entry.value?.absolutePath)
                }
            }
        }
    }

    private fun streamProviderFromMedia(uri: Uri): InputStreamAdapter {
        return object : InputStreamAdapter() {

            override fun openInternal(): InputStream? {
                return ArrayPoolProvide.sharedProvider.openInputStream(context.contentResolver, uri)
            }

            override fun getPath(): String {
                val string = uri.toString()
                return if (PictureMimeType.isContent(string)) string else (uri.path ?: "")
            }
        }
    }

    private fun compress(provider: InputStreamProvider): File {
        val mimeType = getMineType(provider)
        val outFile = getImageCacheFile(context, extSuffix(mimeType))
        // 如果文件存在直接返回不处理
        if (outFile.exists()) {
            Log.d("CompressFile", "file = ${outFile.absolutePath}")
            return outFile
        }

        val source = sourceFromProvider(provider)
        return if (!PictureMimeType.isUrlHasImage(source)
            || PictureMimeType.isHasHttp(source)
            || PictureMimeType.isUrlHasGif(source)) {
            File(source)
        } else {
            compress(provider, outFile, mimeType) ?: File(source)
        }
    }

    private fun sourceFromProvider(provider: InputStreamProvider): String {
        val path = provider.getPath()
        return if (PictureMimeType.isContent(path)) PhotoUtils.getPath(context, Uri.parse(path)) else path
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context A context.
     */
    private fun getImageCacheFile(context: Context, suffix: String): File {
        if (TextUtils.isEmpty(cacheDir)) {
            cacheDir = getImageCacheDir(context)!!.absolutePath
        }

        val ext = if (TextUtils.isEmpty(suffix)) ".jpg" else suffix
        val random = "${System.currentTimeMillis()}${(Math.random() * 1000).toInt()}"
        val cacheBuilder = "$cacheDir${File.separator}$random$ext"
        return File(cacheBuilder)
    }

    private fun getImageCustomFile(context: Context, filename: String): File {
        if (TextUtils.isEmpty(cacheDir)) {
            cacheDir = getImageCacheDir(context)!!.absolutePath
        }
        val cacheBuilder = "$cacheDir${File.separator}$filename"
        return File(cacheBuilder)
    }

    /**
     * Returns a directory with a default name in the private cache directory of the application to
     * use to store retrieved audio.
     */
    private fun getImageCacheDir(context: Context): File? {
        val cacheDir = context.externalCacheDir
        if (cacheDir != null) {
            val result = File(cacheDir, CACHE_DISK_DIR)
            return if (!result.mkdirs() && (!result.exists() || !result.isDirectory)) {
                // File wasn't able to create a directory, or the result exists but not a directory
                null
            } else result
        }
        return null
    }

    private fun extSuffix(mimeType: String): String {
        if (TextUtils.isEmpty(mimeType)) {
            return ".jpg"
        }
        return try {
            if (mimeType.startsWith("video"))
                mimeType.replace("video/", ".")
            else
                mimeType.replace("image/", ".")
        } catch (e: Exception) {
            ".jpg"
        }
    }

    private fun getMineType(provider: InputStreamProvider): String {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(provider.open(), null, options)
            return options.outMimeType
        } catch (e: Exception) {
            "image/jpg"
        }
    }

    private fun needCompressToLocalMedia(leastCompressSize: Int, file: File): Boolean {
        if (leastCompressSize > 0) {
            return file.exists() && file.length() > leastCompressSize shl 10
        }
        return true
    }

    //判断格式是否支持
    private fun supportFormat(stream: InputStream?): Boolean {
        stream ?: false
        return when(ImageUtils.getFormat(stream!!)) {
            ImageFormat.JPEG, ImageFormat.PNG, ImageFormat.WEBP -> true
            else -> false
        }
    }

    private fun rotatingAndScaleIfNeeded(bitmap: Bitmap, provider: InputStreamProvider, mimeType: String): MatrixResult {

        var size: Size? = null
        val width = bitmap.width
        val height = bitmap.height

        if (!config.enableCrop) {
            val maxWidth = config.maxWidth
            val maxHeight = config.maxHeight
            if (maxWidth > 0 || maxHeight > 0) {
                val result = ImageUtils.fitSize(width, height, maxWidth, maxHeight)
                if (result.width < width || result.height < height) {
                    //图片比裁剪大小大 才裁剪
                    size = result
                }
            }
        }

        var rotate = false
        var orientation = 0
        if (isJPG(mimeType)) {
            val degree = BitmapUtils.readPictureDegree(context, sourceFromProvider(provider))
            if (degree > 0) {
                orientation = degree
                rotate = true
            }
        }

        if (size != null || rotate) {
            val matrix = Matrix()
            if (rotate) {
                matrix.postRotate(orientation.toFloat())
            }

            if (size != null) {
                matrix.postScale(size.width / width.toFloat(),
                    size.height / height.toFloat())
            }

            val result = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            return MatrixResult(
                change = result != null,
                rotating = rotate,
                result ?: bitmap
            )
        }
        return MatrixResult(change = false, rotating = false, bitmap)
    }

    private fun compress(provider: InputStreamProvider, outFile: File, mimeType: String): File? {
        var opts: BitmapFactory.Options? = null
        //防止图片过大导致 OOM
        if (!config.enableCrop) {
            val maxWidth = config.maxWidth
            val maxHeight = config.maxHeight
            if (maxWidth > 0 || maxHeight > 0) {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeStream(provider.open(), null, options)
                val width = options.outWidth
                val height = options.outHeight
                val result = ImageUtils.fitSize(width, height, maxWidth, maxHeight)
                if (result.width < width || result.height < height) {
                    //图片比裁剪大小大 才裁剪
                    val size = min(width / result.width, height / result.height)
                    if (size >= 2) {
                        opts = BitmapFactory.Options()
                        opts.inSampleSize = size
                    }
                }
            }
        }

        var bitmap = BitmapFactory.decodeStream(provider.open(), null, opts)
        bitmap ?: return null

        val stream = ByteArrayOutputStream()
        val rotateResult = rotatingAndScaleIfNeeded(bitmap, provider, mimeType)
        bitmap = rotateResult.bitmap
        val source = sourceFromProvider(provider)
        val file = File(source)

        val isCompress = needCompressToLocalMedia(config.minimumCompressSize, file)
        val isSupport = supportFormat(provider.open())
        var useEnabled = false

        if (isCompress || rotateResult.change || !isSupport) {
            useEnabled = true
            var compressQuality = config.compressQuality
            if (compressQuality <= 0 || compressQuality > 100) {
                compressQuality = 90
            }
            bitmap.compress(
                if (bitmap.hasAlpha()) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                compressQuality,
                stream
            )

            if (stream.size() > file.length() && isSupport && !rotateResult.rotating) {
                //压缩后变大了并且是支持的格式，用原图
                useEnabled = false
            }
        }
        bitmap.recycle()

        if (useEnabled) {
            val fos = FileOutputStream(outFile)
            try {
                fos.write(stream.toByteArray())
                fos.flush()
            } finally {
                fos.close()
                stream.close()
            }
            return outFile
        }
        return null
    }

    private fun isJPG(mimeType: String): Boolean {
        return if (TextUtils.isEmpty(mimeType)) {
            false
        } else mimeType.startsWith("image/heic")
                || mimeType.startsWith("image/jpeg")
                || mimeType.startsWith("image/jpg")
    }
}

//缩放 旋转结果
private class MatrixResult(val change: Boolean, val rotating: Boolean, val bitmap: Bitmap)