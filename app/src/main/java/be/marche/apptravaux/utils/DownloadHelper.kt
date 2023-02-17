package be.marche.apptravaux.utils

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.os.storage.StorageManager
import android.os.storage.StorageManager.ACTION_MANAGE_STORAGE
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import timber.log.Timber
import java.io.File
import java.util.*

//https://developer.android.com/training/data-storage/app-specific?hl=fr
//https://johncodeos.com/how-to-download-image-from-the-web-in-android-using-kotlin
class DownloadHelper(val context: Context) {
    private var msg: String? = ""
    private var lastMsg = "cool"
    private var directory = context.filesDir
    private var downloadManager: DownloadManager

    init {
        Timber.e("zeze dir " + directory)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    fun imagePath(avaloirId: Number): String {
        return "$directory" + File.separator + imageName(avaloirId)
    }

    fun imageName(avaloirId: Number): String {
        //astuce last url element: url.substring(url.lastIndexOf("/") + 1)
        return "aval-$avaloirId.jpg"
    }

    fun downloadImage(avaloirId: Number, url: String) {
        //val directory = File(Environment.DIRECTORY_PICTURES)

        val imagePath = imagePath(avaloirId)
        val imageName = imageName(avaloirId)
        Timber.e("zeze filename " + imagePath)

        val file = File(imagePath)

        if (file.exists()) {
            Timber.e("zeze $imagePath does exist.")
            return
        } else {
            Timber.e("zeze $imagePath does not exist.")
        }

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(imageName)
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    imageName
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)

        var downloading = true
        while (downloading) {
            val cursor: Cursor = downloadManager.query(query)
            cursor.moveToFirst()
            if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                downloading = false
            }
            val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            msg = statusMessage(url, directory, status)
            if (msg != lastMsg) {

                Timber.e("zeze message download" + msg)

                lastMsg = msg ?: ""
            }
            cursor.close()
        }
        Timber.e(
            "zeze Image downloaded successfully in " + directory.toString() +
                    imageName
        )
    }

    private fun statusMessage(url: String, directory: File, status: Int): String {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }

        Timber.e("zeze message ici" + msg)
        return msg
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