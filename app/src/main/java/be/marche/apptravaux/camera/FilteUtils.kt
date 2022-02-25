package be.marche.apptravaux.camera

import android.content.Context
import be.marche.apptravaux.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FilteUtils(val context: Context) {
    //Store the capture image

    //Store the capture image
    private fun getDirectory(): File {
        val mediaDir = context.externalMediaDirs.firstOrNull()?.let {
            File(it, context.resources.getString(R.string.app_name)).apply { mkdirs() }
        }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else context.filesDir
    }

    private fun tt(directory: File) {
        val photoFile = File(
            directory,
            SimpleDateFormat("yyyyMMDD-HHmmss", Locale.US)
                .format(System.currentTimeMillis()) + ".jpg"
        )
    }
}