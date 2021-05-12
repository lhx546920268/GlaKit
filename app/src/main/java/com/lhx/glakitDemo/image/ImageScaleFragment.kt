package com.lhx.glakitDemo.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.lhx.glakit.base.fragment.BaseFragment
import com.lhx.glakit.base.widget.BaseContainer
import com.lhx.glakit.utils.AppUtils
import com.lhx.glakit.utils.FileUtils
import com.lhx.glakit.utils.ImageUtils
import com.lhx.glakitDemo.R
import com.lhx.glakitDemo.databinding.ImageScaleFragmentBinding
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class ImageScaleFragment: BaseFragment() {

    override fun initialize(
        inflater: LayoutInflater,
        container: BaseContainer,
        saveInstanceState: Bundle?
    ) {
        setContainerContentView(R.layout.image_scale_fragment)
        val binding = ImageScaleFragmentBinding.bind(getContainerContentView()!!)

        val options = BitmapFactory.Options()
        options.inSampleSize = 1
        options.inScaled = false

        val inputStream = requireContext().resources.assets.open("image_5.jpg")

        var bitmap = BitmapFactory.decodeStream(inputStream)
        writeToFile(bitmap)

        Log.d("src", "width = ${bitmap.width}, height = ${bitmap.height}")
        var size = ImageUtils.fitSize(bitmap.width, bitmap.height, 300, 0)
        Log.d("resize", "width = ${size.width}, height = ${size.height}")
        var result = Bitmap.createScaledBitmap(bitmap, size.width, size.height, true)
        writeToFile(result)
        Log.d("scale", "width = ${result.width}, height = ${result.height}")
        binding.image1.setImageBitmap(result)
        Log.d("deviceId", AppUtils.deviceId)
    }

    fun writeToFile(bitmap: Bitmap) {
        val file = FileUtils.createTemporaryFile(requireContext(), "jpeg")

        val outputStream = ByteArrayOutputStream()
        val fos = FileOutputStream(file)
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            fos.write(outputStream.toByteArray())
            fos.flush()
        }catch (e: Exception){
            e.printStackTrace()
        }finally {
            outputStream.close()
            fos.close()
        }

        Log.d("size", "size = ${file.length()}")
    }

//    // Scale image to reduce it
//    Bitmap reducedImage = reduceImage(tempPhotoPath);
//
//// Decrease photo quality
//    FileOutputStream fos = new FileOutputStream(tempPhotoFile);
//    reducedImage.compress(CompressFormat.JPEG, 55, fos);
//    fos.flush();
//    fos.close();
//
//// Check and fix rotation issues
//    Bitmap fixed = fixRotation(tempPhotoPath);
//    if(fixed!=null)
//    {
//        FileOutputStream fos2 = new FileOutputStream(tempPhotoFile);
//        fixed.compress(CompressFormat.JPEG, 100, fos2);
//        fos2.flush();
//        fos2.close();
//    }
//
//    public Bitmap reduceImage(String originalPath)
//    {
//        // Decode image size
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//        o.inPurgeable = true;
//        o.inInputShareable = true;
//        BitmapFactory.decodeFile(originalPath, o);
//
//        // The new size we want to scale to
//        final int REQUIRED_SIZE = 320;
//
//        // Find the correct scale value. It should be the power of 2.
//        int width_tmp = o.outWidth, height_tmp = o.outHeight;
//        int scale = 1;
//        while (true) {
//            if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE) {
//                break;
//            }
//            width_tmp /= 2;
//            height_tmp /= 2;
//            scale *= 2;
//        }
//
//        // Decode with inSampleSize
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inPurgeable = true;
//        o2.inInputShareable = true;
//        o2.inSampleSize = scale;
//        Bitmap bitmapScaled = null;
//        bitmapScaled = BitmapFactory.decodeFile(originalPath, o2);
//
//        return bitmapScaled;
//    }
//
//    public Bitmap fixRotation(String path)
//    {
//        Bitmap b = null;
//        try
//        {
//            //Find if the picture is rotated
//            ExifInterface exif = new ExifInterface(path);
//            int degrees = 0;
//            if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6"))
//                degrees = 90;
//            else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8"))
//                degrees = 270;
//            else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3"))
//                degrees = 180;
//
//            if(degrees > 0)
//            {
//                BitmapFactory.Options o = new BitmapFactory.Options();
//                o.inPurgeable = true;
//                o.inInputShareable = true;
//                Bitmap bitmap = BitmapFactory.decodeFile(path, o);
//
//                int w = bitmap.getWidth();
//                int h = bitmap.getHeight();
//
//                Matrix mtx = new Matrix();
//                mtx.postRotate(degrees);
//
//                b = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
//            }
//        }
//        catch(Exception e){e.printStackTrace();}
//
//        return b;
//    }
}