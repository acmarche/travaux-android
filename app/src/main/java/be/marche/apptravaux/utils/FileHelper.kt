package be.marche.apptravaux.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Environment
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Files you save in the directories provided by getExternalFilesDir() or getFilesDir() are deleted when the user uninstalls your app.
 */
fun galleryDir(): File {
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File(storageDir, "StrangerCam")
}

class FileHelper {

    fun createRequestBody(file: File): RequestBody {
        val MEDIA_TYPE_IMAGE: MediaType = "image/*".toMediaTypeOrNull()!!
        return file.asRequestBody(MEDIA_TYPE_IMAGE)
    }

    fun createPart(file: File, requestBody: RequestBody): MultipartBody.Part {
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }

    @Throws(IOException::class)
    fun createImageFile(file: File): File {
        val format = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
        val storageDir: File =
            file
        return File.createTempFile(
            "JPEG_${format}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    fun getJpegFilesFromDir(dir: File): List<File> {
        return dir.listFiles().filter { f -> f.extension == "jpg" }
    }

    fun orientationImage(currentPhotoPath: String) {

        val ei = ExifInterface(currentPhotoPath)
        var orientation: Int = ei.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_UNDEFINED
        )
        orientation = 6
        Timber.w("zeze phooto ori " + orientation)

        var rotatedBitmap: Bitmap? = null

        val bitmap: Bitmap = BitmapFactory.decodeFile(currentPhotoPath)

        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotatedBitmap =
                rotateImage(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotatedBitmap =
                rotateImage(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotatedBitmap =
                rotateImage(bitmap, 270)
            ExifInterface.ORIENTATION_NORMAL -> rotatedBitmap =
                bitmap
            else -> {
                Timber.w("zeze phooto ici " + orientation)
            }
        }

        if (rotatedBitmap != null)
            saveBitmap(rotatedBitmap, currentPhotoPath)
    }

    fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun saveBitmap(image: Bitmap, filename: String) {
        try {
            FileOutputStream(filename).use({ out ->
                image.compress(Bitmap.CompressFormat.PNG, 95, out)
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}