package be.marche.apptravaux.utils

import android.os.Environment
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Files you save in the directories provided by getExternalFilesDir() or getFilesDir() are deleted when the user uninstalls your app.
 */
fun galleryDir() : File {
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

    fun getJpegFilesFromDir(dir: File) : List<File> {
        return dir.listFiles().filter { f -> f.extension == "jpg" }
    }

}