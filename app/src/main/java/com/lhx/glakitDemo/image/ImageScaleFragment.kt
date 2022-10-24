package com.lhx.glakitDemo.image

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.core.app.NotificationCompat
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.extension.setOnSingleListener
import com.lhx.glakit.image.ImageData
import com.lhx.glakit.image.ImagePicker
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.ImageScaleFragmentBinding
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.net.HttpURLConnection

class ImageScaleFragment: BaseFragment() {

    private val imagePicker by lazy { ImagePicker() }

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {

        setContainerContentView(R.layout.image_scale_fragment)
        val binding = ImageScaleFragmentBinding.bind(getContainerContentView()!!)
        binding.camera.setOnSingleListener {
            imagePicker.pick(requireActivity(), 1) {
                uploadImage(it.first())
            }

//            container.postDelayed({
//                sendNotification(requireContext(), "一个通知标题", "一个通知内容")
//            }, 2000)
        }

        var bitmap = BitmapFactory.decodeResource(requireActivity().resources, R.drawable.tupian)

        val matrix = Matrix()
        matrix.postRotate(180f)
        matrix.postScale(0.5f, 0.5f)
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream)

        val inputStream = ByteArrayInputStream(stream.toByteArray())
        bitmap = BitmapFactory.decodeStream(inputStream)

        val imageView = findViewById<ImageView>(R.id.bottom_image)!!
        imageView.setImageBitmap(bitmap)
    }

    private fun uploadImage(data: ImageData) {
        val task = UploadFileTask(requireContext(), File(data.path), data.width, data.height)
        task.start()
    }

    fun sendNotification(
        context: Context,
        title: String?,
        body: String?,
    ) {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val nid = SystemClock.currentThreadTimeMillis().toInt()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timeMillis = SystemClock.currentThreadTimeMillis()
            @SuppressLint("WrongConstant") val Channel = NotificationChannel(
                timeMillis.toString(),
                "zegobird",
                NotificationManager.IMPORTANCE_HIGH
            )
            Channel.enableLights(true)
            Channel.lightColor = Color.GREEN
            Channel.setShowBadge(true)
            Channel.description = body
            Channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            manager.createNotificationChannel(Channel)
            val notification: Notification = NotificationCompat.Builder(context)
                .setChannelId(timeMillis.toString())
                .setContentTitle(context.getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .build()
            manager.notify(nid, notification)
        } else {
            val notification: Notification = NotificationCompat.Builder(context)
                .setContentTitle(context.getString(R.string.app_name))
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentText(body)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .build()
            manager.notify(nid, notification)
        }
    }
}