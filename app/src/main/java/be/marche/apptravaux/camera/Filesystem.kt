package be.marche.apptravaux.camera

import android.os.Environment
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import timber.log.Timber

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
