package com.lhx.glakit.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.webkit.MimeTypeMap
import java.io.*
import java.nio.channels.FileChannel


/**
 * 文件工具类
 */
object FileUtils {

    /**
     * 获取文件的 MIME TYPE
     * @param filePath 文件路径
     * @return MIME TYPE
     */
    fun getMimeType(filePath: String): String {
        val index = filePath.lastIndexOf(File.separator)
        var fileName = filePath
        var extension = ""

        //通过文件名称获取 拓展名，防止文件路径里面有 .
        if (index != -1) {
            fileName = filePath.substring(index)
        }
        val extensionIndex = fileName.lastIndexOf(".")
        if (extensionIndex != -1 && extensionIndex + 1 < fileName.length) {
            extension = fileName.substring(extensionIndex + 1)
        }
        if (!StringUtils.isEmpty(extension)) {
            val map = MimeTypeMap.getSingleton()
            return map.getMimeTypeFromExtension(extension) ?: ""
        }
        return ""
    }


    /**
     * 通过 mimeType 获取文件扩展名称
     * @param mimeType 文件的mimeType
     * @return 文件拓展名
     */
    fun getFileExtensionFromMimeType(mimeType: String): String {
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
    fun appendFileExtension(filePath: String, extension: String, replace: Boolean): String {
        if (!TextUtils.isEmpty(extension)) {
            val index = filePath.lastIndexOf(File.separator)

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
        return if (!file.exists() || !file.isFile || destFile.isDirectory) false else try {
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
            var fileInputStream: FileInputStream? = null
            var bufferedInputStream: BufferedInputStream? = null
            var outputStream: ByteArrayOutputStream ? = null
            try {
                fileInputStream = FileInputStream(file)
                bufferedInputStream = BufferedInputStream(fileInputStream)
                outputStream = ByteArrayOutputStream()
                val bytes = ByteArray(1024 * 256)
                var len: Int
                while (bufferedInputStream.read(bytes).also{ len = it } != -1) {
                    outputStream.write(bytes, 0, len)
                }
                return outputStream.toByteArray()
            } finally {
                fileInputStream?.close()
                bufferedInputStream?.close()
                outputStream?.close()
            }
        }
        return null
    }

    /**
     * 获取一个独一无二的文件名称
     * @return 文件名称
     */
    fun getUniqueFileName(): String {
        return "${System.currentTimeMillis()}${StringUtils.getRandomNumber(10)}"
    }

    /**
     * 获取临时文件夹
     * @param context 上下文
     * @return 临时文件夹
     */
    fun getTemporaryDirectory(context: Context): String {
        var file = context.externalCacheDir
        if (file == null) {
            file = context.cacheDir
        }
        return "${file!!.absolutePath}/temp"
    }


    /**
     * 获取一个临时文件名称
     * @param context 上下文
     * @param extension 文件类型 包含 .
     * @return 临时文件
     */
    fun getTemporaryFilePath(context: Context, extension: String): String {
        var path = "${getTemporaryDirectory(context)}${File.separator}${getUniqueFileName()}"
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
    fun createTemporaryFile(context: Context, extension: String): File {
        return createNewFileIfNotExist(getTemporaryFilePath(context, extension))
    }

    @Throws(IOException::class)
    fun createNewFileIfNotExist(filePath: String): File {
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
        if (file.exists() && file.isFile) return true
        val parent = file.parent

        if(parent != null){
            //创建文件夹
            val directory = File(parent)
            if (!directory.exists() || !directory.isDirectory) {
                if (!directory.mkdirs()) {
                    return false
                }
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
        val parent = file.parent

        //创建文件夹
        if(parent != null){
            val directory = File(parent)
            return if (!directory.exists() || !directory.isDirectory) {
                directory.mkdirs()
            } else true
        }

        return true
    }

    /**
     * 通过uri 获取文件路径
     * @param uri uri
     * @return 成功则返回文件路径，否则返回null
     */
    fun filePathFromUri(context: Context, uri: Uri): String? {
        val scheme = uri.scheme
        if (scheme == null) {
            return uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            return uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {

            val cursor = context.contentResolver.query(uri, arrayOf(MediaStore.Video.VideoColumns._ID), null, null, null)
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    val index: Int = cursor.getColumnIndex(MediaStore.Video.VideoColumns._ID)
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
     * 复制文件
     */
    fun copyFile(pathFrom: String, pathTo: String) {
        if (pathFrom.equals(pathTo, ignoreCase = true)) {
            return
        }
        var outputChannel: FileChannel? = null
        var inputChannel: FileChannel? = null
        try {
            inputChannel = FileInputStream(File(pathFrom)).channel
            outputChannel = FileOutputStream(File(pathTo)).channel
            inputChannel.transferTo(0, inputChannel.size(), outputChannel)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputChannel?.close()
            outputChannel?.close()
        }
    }

    /**
     * 复制文件到outputStream
     * @return 写的长度
     */
    fun copy(file: File, out: OutputStream): Long {
        var nread = 0L
        if (file.exists()) {
            var inputStream: FileInputStream? = null
            try {
                inputStream = FileInputStream(file)
                val buf = ByteArray(8192)
                var n: Int
                while (inputStream.read(buf).also { n = it } > 0) {
                    out.write(buf, 0, n)
                    nread += n.toLong()
                }
            } finally {
                inputStream?.close()
            }
        }
        return nread
    }


    /**
     * 获取文件大小
     * @param file 文件或文件夹
     * @return 字节
     */
    fun getFileSize(file: File): Long {
        var size: Long = 0
        if (file.exists()) {
            if (file.isDirectory) {
                val files = file.listFiles()
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
        if (file.exists() && file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    deleteAllFiles(f)
                }
            }
        } else {
            deleteFile(file)
        }
    }

    /**
     * 生成base64字符串
     */
    fun getBase64(path: String): String? {
        var inputStream: FileInputStream? = null
        var outputStream: ByteArrayOutputStream? = null
        return try {
            inputStream = FileInputStream(path)
            outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            var count: Int
            while (inputStream.read(buffer).also { count = it } >= 0) {
                outputStream.write(buffer, 0, count)
            }
            String(Base64.encode(outputStream.toByteArray(), Base64.NO_WRAP)) //进行Base64
        } catch (_: Exception) {
            null
        } finally {
            inputStream?.close()
            outputStream?.close()
        }
    }

}