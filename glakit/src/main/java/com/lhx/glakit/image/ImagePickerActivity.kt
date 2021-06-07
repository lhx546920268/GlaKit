package com.lhx.glakit.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.text.TextUtils
import android.util.Log
import androidx.exifinterface.media.ExifInterface
import com.lhx.glakit.loading.LoadingHelper
import com.lhx.glakit.loading.LoadingView
import com.luck.picture.lib.PictureSelectorActivity
import com.luck.picture.lib.compress.InputStreamAdapter
import com.luck.picture.lib.compress.InputStreamProvider
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.thread.PictureThreadUtils
import com.luck.picture.lib.tools.AndroidQTransformUtils
import com.luck.picture.lib.tools.DateUtils
import com.luck.picture.lib.tools.SdkVersionUtils
import com.luck.picture.lib.tools.StringUtils
import java.io.*

/**
 * 图片选择，主要是为了修改loading样式
 */
class ImagePickerActivity: PictureSelectorActivity(), LoadingHelper {

    override var loadingView: LoadingView? = null
    override var loading = false

    override fun showPleaseDialog() {
        showLoading(container, 0)
    }

    override fun dismissDialog() {
        hideLoading()
    }

    override fun onDestroy() {
        super.onDestroy()
        hideLoading()
    }
    override fun compressImage(result: MutableList<LocalMedia>?) {
        if (result == null) {
            onResult(result)
            return
        }
        PictureThreadUtils.executeByIo(object : PictureThreadUtils.SimpleTask<List<File?>>() {
            @Throws(Exception::class)
            override fun doInBackground(): List<File?> {
                val files = ArrayList<File>()
                for (media in result) {
                    val provider = streamProviderFromMedia(media)
                    files.add(compress(provider, result.size))
                }
                return files
            }

            override fun onSuccess(files: List<File?>) {
                if (files.size == result.size) {
                    handleCompressCallBack(result, files)
                } else {
                    onResult(result)
                }
            }
        })
    }

    private fun handleCompressCallBack(images: List<LocalMedia>?, files: List<File?>?) {
        if (images == null || files == null) {
            exit()
            return
        }
        val isAndroidQ = SdkVersionUtils.checkedAndroid_Q()
        val size = images.size
        if (files.size == size) {
            var i = 0
            while (i < size) {
                val file = files[i]
                if (file == null) {
                    i++
                    continue
                }
                val path = file.absolutePath
                val image = images[i]
                val http = PictureMimeType.isHasHttp(path)
                val flag = !TextUtils.isEmpty(path) && http
                val isHasVideo = PictureMimeType.isHasVideo(image.mimeType)
                image.isCompressed = !isHasVideo && !flag
                image.compressPath = if (isHasVideo || flag) null else path
                if (isAndroidQ) {
                    image.androidQToPath = image.compressPath
                }
                i++
            }
        }
        onResult(images)
    }

    private fun streamProviderFromMedia(media: LocalMedia): InputStreamAdapter {
        return object : InputStreamAdapter() {
            @Throws(IOException::class)
            override fun openInternal(): InputStream? {
                return if (PictureMimeType.isContent(media.path) && !media.isCut) {
                    if (!TextUtils.isEmpty(media.androidQToPath)) {
                        FileInputStream(media.androidQToPath)
                    } else context.contentResolver.openInputStream(Uri.parse(media.path))!!
                } else {
                    if (PictureMimeType.isHasHttp(media.path)) null else FileInputStream(if (media.isCut) media.cutPath else media.path)
                }
            }

            override fun getPath(): String {
                return if (media.isCut) {
                    media.cutPath
                } else {
                    if (TextUtils.isEmpty(media.androidQToPath)) media.path else media.androidQToPath
                }
            }

            override fun getMedia(): LocalMedia {
                return media
            }
        }
    }

    private fun compress(provider: InputStreamProvider, totalCount: Int): File {
        val media = provider.media
        val hasVideo = PictureMimeType.isHasVideo(media.mimeType)
        if (!hasVideo) {
            val inputStream = provider.open()
            if (inputStream != null) {
                inputStream.use {
                    val newPath =
                        if (media.isCut && !TextUtils.isEmpty(media.cutPath)) media.cutPath else media.realPath
                    val suffix = extSuffix(media.mimeType)
                    var outFile = getImageCacheFile(
                        context,
                        provider,
                        if (TextUtils.isEmpty(suffix)) extSuffix(provider) else suffix
                    )
                    var filename = ""
                    val newFileName = config.renameCompressFileName
                    if (!TextUtils.isEmpty(newFileName)) {
                        filename =
                            if (config.isCamera || totalCount == 1) newFileName else StringUtils.rename(
                                newFileName
                            )
                        outFile = getImageCustomFile(context, filename)
                    }

                    // 如果文件存在直接返回不处理
                    if (outFile.exists()) {
                        Log.d("CompressFile", "file = ${outFile.absolutePath}")
                        return outFile
                    }

                    return if (extSuffix(provider).startsWith(".gif")) {
                        // GIF without compression
                        copyIfNeeded(provider, newPath, filename)
                    } else {
                        compress(provider, outFile) ?: copyIfNeeded(provider, newPath, filename)
                    }
                }
            }
        }

        return File(provider.media.path)
    }

    fun copyIfNeeded(provider: InputStreamProvider, newPath: String, filename: String): File {
        val media = provider.media
        // 这种情况判断一下，如果是小于设置的图片压缩阀值，再Android 10以上做下拷贝的处理
        return if (SdkVersionUtils.checkedAndroid_Q()) {
            val newFilePath =
                if (media.isCut) media.cutPath else AndroidQTransformUtils.copyPathToAndroidQ(
                    context,
                    provider.path,
                    media.width,
                    media.height,
                    media.mimeType,
                    filename
                )
            File(if (TextUtils.isEmpty(newFilePath)) newPath else newFilePath)
        } else {
            File(newPath)
        }
    }

    /**
     * Returns a file with a cache image name in the private cache directory.
     *
     * @param context A context.
     */
    private fun getImageCacheFile(
        context: Context,
        provider: InputStreamProvider,
        suffix: String
    ): File {
        var path = config.compressSavePath ?: ""
        if (TextUtils.isEmpty(path)) {
            val imageCacheDir = getImageCacheDir(context)
            if (imageCacheDir != null) {
                path = imageCacheDir.absolutePath
            }
        }
        var cacheBuilder = ""
        try {
            val media = provider.media
            val encryptionValue =
                StringUtils.getEncryptionValue(media.path, media.width, media.height)
            cacheBuilder = if (!TextUtils.isEmpty(encryptionValue) && !media.isCut) {
                "$path/IMG_CMP_$encryptionValue${if (TextUtils.isEmpty(suffix)) ".jpg" else suffix}"
            } else {
                "$path/${DateUtils.getCreateFileName("IMG_CMP_")}${if (TextUtils.isEmpty(suffix)) ".jpg" else suffix}"
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return File(cacheBuilder)
    }

    private fun getImageCustomFile(context: Context, filename: String): File {

        var path = config.compressSavePath ?: ""
        if (TextUtils.isEmpty(path)) {
            path = getImageCacheDir(context)!!.absolutePath
        }
        return File("$path/$filename")
    }

    /**
     * Returns a directory with the given name in the private cache directory of the application to
     * use to store retrieved media and thumbnails.
     *
     * @param context A context.
     * @see .getImageCacheDir
     */
    private fun getImageCacheDir(context: Context): File? {
        val cacheDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (cacheDir != null) {
            return if (!cacheDir.mkdirs() && (!cacheDir.exists() || !cacheDir.isDirectory)) {
                // File wasn't able to create a directory, or the result exists but not a directory
                null
            } else cacheDir
        }
        return null
    }

    fun extSuffix(mimeType: String): String {
        if (TextUtils.isEmpty(mimeType)) {
            return ".jpg"
        }
        return try {
            if (mimeType.startsWith("video")) mimeType.replace("video/", ".") else mimeType.replace(
                "image/",
                "."
            )
        } catch (e: Exception) {
            ".jpg"
        }
    }

    fun extSuffix(provider: InputStreamProvider): String {
        return try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(provider.open(), null, options)
            options.outMimeType.replace("image/", ".")
        } catch (e: Exception) {
            ".jpg"
        }
    }

    fun needCompressToLocalMedia(leastCompressSize: Int, path: String): Boolean {
        if (leastCompressSize > 0 && !TextUtils.isEmpty(path)) {
            val source = File(path)
            return source.exists() && source.length() > leastCompressSize shl 10
        }
        return true
    }

    private fun rotatingAndScaleIfNeeded(bitmap: Bitmap, provider: InputStreamProvider): MatrixResult {

        var size: ImageUtils.Size? = null
        val width = bitmap.width
        val height = bitmap.height

        if (!config.enableCrop) {
            val maxWidth = config.cropWidth
            val maxHeight = config.cropHeight
            if (maxWidth > 0 || maxHeight > 0) {
                val result = ImageUtils.fitSize(width, height, maxWidth, maxHeight)
                if (!(result.width >= width || result.height >= height)) {
                    //图片比裁剪大小大 才裁剪
                    size = result
                }
            }
        }

        var rotate = false
        val media = provider.media
        var orientation = 0
        if (media != null && media.isCut) {
            if (isJPG(media.mimeType)) {
                orientation = media.orientation
                if (orientation > 0) {
                    var result = true
                    when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> orientation = 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> orientation = 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> orientation = 270
                        else -> result = false
                    }
                    rotate = result
                }
            }
        }

        if (size != null || rotate) {
            val matrix = Matrix()
            if (rotate) {
                matrix.postRotate(orientation.toFloat())
            }

            if (size != null) {
                matrix.setScale(size.width / width.toFloat(),
                    size.height / height.toFloat())
            }

            return MatrixResult(
                true,
                Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true)
            )
        }
        return MatrixResult(false, bitmap)
    }

    fun compress(provider: InputStreamProvider, outFile: File): File? {
        var bitmap = BitmapFactory.decodeStream(provider.open(), null, null)
        bitmap ?: return null

        val stream = ByteArrayOutputStream()
        val rotateResult = rotatingAndScaleIfNeeded(bitmap, provider)
        bitmap = rotateResult.bitmap
        val media = provider.media
        val path =
            if (media.isCut && !TextUtils.isEmpty(media.cutPath)) media.cutPath else media.realPath
        val isCompress = needCompressToLocalMedia(config.minimumCompressSize, path)
        if (isCompress || rotateResult.change) {
            var compressQuality = config.compressQuality
            if (rotateResult.change) {
                compressQuality = 100
            } else if (compressQuality <= 0 || compressQuality > 100) {
                compressQuality = 80
            }
            bitmap.compress(
                if (config.focusAlpha) Bitmap.CompressFormat.PNG else Bitmap.CompressFormat.JPEG,
                compressQuality,
                stream
            )
        }
        bitmap.recycle()

        if (isCompress || rotateResult.change) {
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

    fun isJPG(mimeType: String): Boolean {
        return if (TextUtils.isEmpty(mimeType)) {
            false
        } else mimeType.startsWith("image/heic")
                || mimeType.startsWith("image/jpeg")
                || mimeType.startsWith("image/jpg")
    }
}

//缩放 旋转结果
private class MatrixResult(val change: Boolean, val bitmap: Bitmap)