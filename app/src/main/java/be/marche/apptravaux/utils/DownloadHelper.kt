package be.marche.apptravaux.utils

import android.content.Context
import android.content.Intent
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import okhttp3.*
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import java.util.concurrent.TimeUnit

//https://developer.android.com/training/data-storage/app-specific?hl=fr
//https://johncodeos.com/how-to-download-image-from-the-web-in-android-using-kotlin
class DownloadHelper(val context: Context) {

    private val directory: File
    private var okHttpClient: OkHttpClient

    init {
        directory =
            File(context.filesDir.toString() + File.separator + Environment.DIRECTORY_PICTURES + File.separator + "avaloirs")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        okHttpClient = OkHttpClient()
        val okHttpBuilder = okHttpClient.newBuilder()
            .connectTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
            .readTimeout(HTTP_TIMEOUT.toLong(), TimeUnit.SECONDS)
        this.okHttpClient = okHttpBuilder.build()
    }

    companion object {
        private const val BUFFER_LENGTH_BYTES = 1024 * 8
        private const val HTTP_TIMEOUT = 30
    }

    fun directoryBase(): File {
        return directory
    }

    fun imageFullPath(avaloirId: Number): String {
        return "$directory" + File.separator + imageName(avaloirId)
    }

    fun imageName(avaloirId: Number): String {
        //astuce last url element: url.substring(url.lastIndexOf("/") + 1)
        return "aval-$avaloirId.jpg"
    }

    fun downloadImage(avaloirId: Number, url: String) {
        val file = File(imageFullPath(avaloirId))
        if (file.canRead()) {
            return
        }
        val request = Request.Builder().url(url).build()

        okHttpClient.newCall(request)
            .enqueue(
                object : Callback {
                    override fun onFailure(call: Call, e: IOException) {

                    }

                    override fun onResponse(call: Call, response: Response) {
                        val body = response.body
                        val responseCode = response.code
                        if (responseCode >= HttpURLConnection.HTTP_OK &&
                            responseCode < HttpURLConnection.HTTP_MULT_CHOICE &&
                            body != null
                        ) {
                            body.byteStream().apply {
                                file.outputStream().use { fileOut ->
                                    copyTo(fileOut, BUFFER_LENGTH_BYTES)
                                }
                            }
                        } else {
                            throw IllegalArgumentException("Error occurred when do http get $url")
                        }
                    }
                })
    }

    fun countFiles(): Int {
        return File(directoryBase().toString()).list()?.size ?: 0
    }

    fun listFiles() {
        Files.walk(Paths.get(directoryBase().toString()))
            .filter { Files.isRegularFile(it) }
            .forEach { Timber.e("zeze file: " + it) }
    }

    fun freeSpace(context: Context) {
        // App needs 700 MB within internal storage.
        val NUM_BYTES_NEEDED_FOR_MY_APP = 1024 * 1024 * 700L

        val storageManager = context.applicationContext.getSystemService<StorageManager>()!!
        val appSpecificInternalDirUuid: UUID = storageManager.getUuidForPath(context.filesDir)
        val availableBytes: Long =
            storageManager.getAllocatableBytes(appSpecificInternalDirUuid)
        if (availableBytes >= NUM_BYTES_NEEDED_FOR_MY_APP) {
            storageManager.allocateBytes(
                appSpecificInternalDirUuid, NUM_BYTES_NEEDED_FOR_MY_APP
            )
        } else {
            val storageIntent = Intent().apply {
                // To request that the user remove all app cache files instead, set
                // "action" to ACTION_CLEAR_APP_CACHE.
                action = ACTION_MANAGE_STORAGE
            }
        }
    }

    // Checks if a volume containing external storage is available
    // for read and write.
    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Checks if a volume containing external storage is available to at least read.
    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    /**
     * le premier élément du tableau renvoyé est considéré comme le volume de stockage externe principal.
     * Utilisez ce volume, sauf s'il est complet ou indisponible
     */
    fun findVolumes(context: Context) {
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(context.applicationContext, null)
        val primaryExternalStorage = externalStorageVolumes[0]
    }

}