package be.marche.apptravaux.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider.getUriForFile
import androidx.exifinterface.media.ExifInterface
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

fun galleryDir(): File {
    val fileName = "ss"
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File(storageDir, fileName)
}

class FileHelper {

    fun galleryOut(file: File): FileOutputStream {
        return FileOutputStream(file)
    }

    fun createRequestBody(file: File): RequestBody {
        val MEDIA_TYPE_IMAGE: MediaType = "image/*".toMediaTypeOrNull()!!
        return file.asRequestBody(MEDIA_TYPE_IMAGE)
    }

    fun createPart(file: File, requestBody: RequestBody): MultipartBody.Part {
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }

    //  @Throws(IOException::class)
    fun createImageFile(file: File): File {
        val format = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
        val storageDir: File =
            file
        return File.createTempFile(
            "avaloir_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }

    fun createFileName(): String {
        val format = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
        return format.toLocalizedPattern()
    }

    //@Throws(IOException::class)
    fun bitmapToFile(image: Bitmap, file: File): File? {

        /*   val file = File(
               Environment.getExternalStorageDirectory().toString() + File.separator + "jf.jpg"
           )
           file.createNewFile()*/

        try {
            FileOutputStream(file).use { out ->
                image.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return try {


            //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            image.compress(Bitmap.CompressFormat.JPEG, 100, bos)
            val bitmapdata = bos.toByteArray()

            //write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
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

            }
        }

        //  if (rotatedBitmap != null)
        //    saveBitmap(rotatedBitmap, currentPhotoPath)
    }

    fun rotateImage(source: Bitmap, angle: Int): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle.toFloat())
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height,
            matrix, true
        )
    }

    fun saveBitmap(image: Bitmap, filename: File) {
        try {
            FileOutputStream(filename).use({ out ->
                image.compress(Bitmap.CompressFormat.JPEG, 95, out)
            })
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun createImageFile(context: Context): File {
        val fileName = "avaloir_" + System.currentTimeMillis() + ".jpg"
        val dirPath: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dirPath, fileName)

    }

    fun createUri(context: Context, file: File): Uri {
        return getUriForFile(
            context,
            context.packageName.toString() + ".fileprovider",
            file
        )
    }

    private fun xx(context: Context) {
        val dir = context.getApplicationInfo().dataDir

        val externalFilesDir =
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

        val file3 = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/Image",
            "temp-image.png"
        )

        File(
            context.getExternalCacheDir()!!
                .getAbsolutePath() + File.separator + "TemperoryFile_" + System.currentTimeMillis()
                    + ".png"
        )

    }
}