package be.marche.apptravaux.utils

import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class FileHelper {

    fun createRequestBody(file: File): RequestBody {
        val MEDIA_TYPE_IMAGE: MediaType = MediaType.parse("image/*")!!
        return RequestBody.create(MEDIA_TYPE_IMAGE, file)
    }

    fun createPart(file: File, requestBody: RequestBody): MultipartBody.Part {
        return MultipartBody.Part.createFormData("image", file.name, requestBody)
    }
}