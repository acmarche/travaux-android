package be.marche.apptravaux.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider.getUriForFile
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

fun galleryDir(): File {
    val fileName = "ss"
    val storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    return File(storageDir, fileName)
}

class FileHelper {

    fun createRequestBody(file: File): RequestBody {
        val MEDIA_TYPE_IMAGE: MediaType = "image/*".toMediaTypeOrNull()!!
        return file.asRequestBody(MEDIA_TYPE_IMAGE)
    }

    fun createPart(file: File, requestBody: RequestBody): MultipartBody.Part {
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
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

}