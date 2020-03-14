package be.marche.apptravaux.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import timber.log.Timber
import android.os.Environment
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
        val MEDIA_TYPE_IMAGE: MediaType = MediaType.parse("image/*")!!
        return RequestBody.create(MEDIA_TYPE_IMAGE, file)
    }

    fun createPart(file: File, requestBody: RequestBody): MultipartBody.Part {
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }
    /**
     * Files you save in the directories provided by getExternalFilesDir() or getFilesDir() are deleted when the user uninstalls your app.
     */
    fun galleryDir() : File {
        val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        return File(storageDir, "StrangerCam")
    }

    fun createPictureFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "strangerpic_${timeStamp}_"
        Timber.w("zeze filename, jpg" + galleryDir())
        return File.createTempFile(filename, ".jpg", galleryDir())
    }

    fun getJpegFilesFromDir(dir: File) : List<File> {
        return dir.listFiles().filter { f -> f.extension == "jpg" }
    }

}