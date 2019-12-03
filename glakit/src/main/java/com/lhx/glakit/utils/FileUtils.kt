package com.lhx.glakit.utils

import android.content.ContentResolver
import android.content.Context
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File


/**
 * 文件工具类
 */
object FileUtils {

    //app目录
    private var appFolder: String? = null

    //app缓存文件夹
    private var cacheFolder: String? = null

    //app缓存图片文件夹
    private var imageCacheFolder: String? = null

    fun getAppFolder(context: Context): String {
        if (appFolder == null) {
            //当sd卡可用并且不可被移除时 使用sd卡
            val cacheFolder = if (MEDIA_MOUNTED == Environment.getExternalStorageState() || !Environment.isExternalStorageRemovable()) {
                    context.externalCacheDir?.path
                } else {
                    context.cacheDir?.path
                }
            appFolder = "${cacheFolder}${File.separator}${AppUtils.getAppPackageName(context)}${File.separator}"
            val file = File(appFolder!!)
            if (!file.exists()) {
                file.mkdirs()
            }
        }
        return appFolder!!
    }

    fun getCacheFolder(context: Context): String? {
        if (cacheFolder == null) {
            cacheFolder = "${getAppFolder(context)}.cache${File.separator}"
            val file = File(cacheFolder!!)
            if (!file.exists()) {
                file.mkdirs()
            }
        }
        return cacheFolder
    }

    fun getImageCacheFolder(context: Context): String? {
        if (imageCacheFolder == null) {
            imageCacheFolder = "${getAppFolder(context)}imageCache${File.separator}"
            val file = File(imageCacheFolder!!)
            if (!file.exists()) {
                file.mkdirs()
            }
        }
        return imageCacheFolder!!
    }

    /**
     * 获取文件的 MIME TYPE
     * @param filePath 文件路径
     * @return MIME TYPE
     */
    fun getMimeType(filePath: String): String? {
        val index = filePath.lastIndexOf(File.separator)
        var fileName = filePath
        var extension: String? = ""

        //通过文件名称获取 拓展名，防止文件路径里面有 .
        if (index != -1) {
            fileName = filePath.substring(index)
        }
        val extensionIndex = fileName.lastIndexOf(".")
        if (extensionIndex != -1 && extensionIndex + 1 < fileName.length) {
            extension = fileName.substring(extensionIndex + 1)
        }
        if (!StringUtil.isEmpty(extension)) {
            val map = MimeTypeMap.getSingleton()
            return map.getMimeTypeFromExtension(extension)
        }
        return ""
    }

    /**
     * 获取储存Image的目录
     *
     * @return
     */
    fun getStorageDirectory(): String? {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator.toString() + "mwdmall" + "/ImageCache"
    }

    /**
     * 获取储存apk的目录
     *
     * @return
     */
    fun getDownLoadDirectory(): String? {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator.toString() + "xinjiang" + File.separator
    }


    /**
     * 通过 mimeType 获取文件扩展名称
     * @param mimeType 文件的mimeType
     * @return 文件拓展名
     */
    fun getFileExtensionFromMimeType(mimeType: String): String? {
        val map = MimeTypeMap.getSingleton()
        var extension = map.getExtensionFromMimeType(mimeType)
        if (extension == null) {
            extension = ""
        } else {
            val pointIndex = extension.indexOf(".")
            if (pointIndex < 0) {
                extension = ".$extension"
            }
        }
        return extension
    }

    /**
     * 为文件路径添加拓展名
     * @param filePath 文件路径
     * @param extension 拓展名
     * @param replace 如果已存在拓展名，是否替换
     * @return 新的文件路径
     */
    fun appendFileExtension(
        filePath: String,
        extension: String,
        replace: Boolean
    ): String? {
        if (!TextUtils.isEmpty(extension)) {
            val index = filePath.lastIndexOf("/")
            //通过文件名称获取 拓展名，防止文件路径里面有 .
            if (index != -1) {
                val fileName = filePath.substring(index)
                val extensionIndex = fileName.lastIndexOf(".")
                if (extensionIndex != -1) {
                    if (replace) {
                        return filePath.substring(0, index + extensionIndex) + extension
                    }
                } else {
                    return filePath + extension
                }
            } else {
                val extensionIndex = filePath.lastIndexOf(".")
                if (extensionIndex != -1) {
                    if (replace) {
                        return filePath.substring(0, extensionIndex) + extension
                    }
                } else {
                    return filePath + extension
                }
            }
        }
        return filePath
    }

    fun moveFile(filePath: String, destFilePath: String): Boolean {
        return moveFile(File(filePath), File(destFilePath))
    }

    fun moveFile(file: File, destFilePath: String): Boolean {
        return moveFile(file, File(destFilePath))
    }

    fun moveFile(filePath: String, destFile: File): Boolean {
        return moveFile(File(filePath), destFile)
    }

    /**
     * 移动文件
     * @param file 要移动的文件
     * @param destFile 目的地
     * @return 是否成功
     */
    fun moveFile(file: File, destFile: File): Boolean {
        return if (!file.exists() || !file.isFile() || destFile.isDirectory()) false else try {
            createDirectoryIfNotExist(destFile)
            file.renameTo(destFile)
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    fun deleteFile(filePath: String): Boolean {
        return deleteFile(File(filePath))
    }

    /**
     * 删除文件
     * @param file 要删除的文件
     * @return 是否成功
     */
    fun deleteFile(file: File): Boolean {
        return if (!file.exists()) false else try {
            file.delete()
        } catch (e: SecurityException) {
            e.printStackTrace()
            false
        }
    }

    fun readFile(filePath: String): ByteArray? {
        return readFile(File(filePath))
    }

    /**
     * 读取文件
     * @param file 要读取的文件
     * @return 文件数据
     */
    fun readFile(file: File): ByteArray? {
        if (file.exists()) {
            try {
                val inputStream = BufferedInputStream(FileInputStream(file))
                val outputStream = ByteArrayOutputStream()
                val bytes = ByteArray(1024 * 256)
                var len = 0
                while (inputStream.read(bytes).also({ len = it }) != -1) {
                    outputStream.write(bytes, 0, len)
                }
                return outputStream.toByteArray()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    /**
     * 获取一个独一无二的文件名称
     * @return 文件名称
     */
    fun getUniqueFileName(): String {
        return System.currentTimeMillis() + StringUtil.getRandomNumber(10)
    }

    /**
     * 获取临时文件夹
     * @param context 上下文
     * @return 临时文件夹
     */
    fun getTemporaryDirectory(context: Context): String {
        var file: File = context.getExternalCacheDir()
        if (file == null) {
            file = context.getCacheDir()
        }
        return file.getAbsolutePath().toString() + "/temp"
    }


    /**
     * 获取一个临时文件名称
     * @param context 上下文
     * @param extension 文件类型 包含 .
     * @return 临时文件
     */
    fun getTemporaryFilePath(context: Context, extension: String): String {
        var path = getTemporaryDirectory(context) + "/" + getUniqueFileName()
        if (!TextUtils.isEmpty(extension)) {
            path += extension
        }
        return path
    }

    /**
     * 创建一个临时文件
     * @param @param context 上下文
     * @param extension 文件类型 包含 .
     * @return 临时文件
     */
    @Throws(IOException::class)
    fun createTemporaryFile(context: Context, extension: String): File? {
        return createNewFileIfNotExist(getTemporaryFilePath(context, extension))
    }

    @Throws(IOException::class)
    fun createNewFileIfNotExist(filePath: String): File? {
        val file = File(filePath)
        createNewFileIfNotExist(file)
        return file
    }

    /**
     * 创建一个文件，如果文件的上级目录不存在，会创建，防止创建文件失败
     * @param file 文件路径
     * @return 是否成功， 如果文件存在，也返回成功
     */
    @Throws(IOException::class)
    fun createNewFileIfNotExist(file: File): Boolean {
        if (file.exists() && file.isFile()) return true
        val parent: String = file.getParent()
        //创建文件夹
        val directory = File(parent)
        if (!directory.exists() || !directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false
            }
        }
        return file.createNewFile()
    }

    fun createDirectoryIfNotExist(filePath: String): Boolean {
        return createDirectoryIfNotExist(File(filePath))
    }

    /**
     * 创建文件路径 中的文件夹
     * @param file 必须是一个文件路径 而不是文件夹路径
     * @return 是否成功 如果文件夹存在，也返回成功
     */
    fun createDirectoryIfNotExist(file: File): Boolean {
        val parent: String = file.getParent()
        //创建文件夹
        val directory = File(parent)
        return if (!directory.exists() || !directory.isDirectory()) {
            directory.mkdirs()
        } else true
    }

    /**
     * 通过uri 获取文件路径
     * @param uri uri
     * @return 成功则返回文件路径，否则返回null
     */
    fun filePathFromUri(context: Context, uri: Uri): String? {
        val cursor: Cursor = context.getContentResolver().query(
            uri, arrayOf(MediaStore.Video.VideoColumns.DATA),
            null, null, null
        )
        val scheme: String = uri.getScheme()
        if (scheme == null) {
            return uri.getPath()
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            return uri.getPath()
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Video.VideoColumns.DATA)
                    if (index > -1) {
                        return cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return null
    }


    /**
     * 获取文件大小
     * @param file 文件或文件夹
     * @return 字节
     */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            if (file.isDirectory()) {
                val files: Array<File> = file.listFiles()
                if (files != null) {
                    for (f in files) {
                        size += getFileSize(f)
                    }
                }
            } else {
                size += file.length()
            }
        }
        return size
    }

    /**
     * 删除所有文件
     * @param file 要删除的文件或文件夹
     */
    fun deleteAllFiles(file: File) {
        if (file.exists() && file.isDirectory()) {
            val files: Array<File> = file.listFiles()
            if (files != null) {
                for (f in files) {
                    deleteAllFiles(f)
                }
            }
        } else {
            deleteFile(file)
        }
    }
}