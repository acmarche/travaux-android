package be.marche.apptravaux.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.SystemClock.sleep
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import be.marche.apptravaux.R
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.screens.avaloir.AvaloirSyncScreen
import be.marche.apptravaux.ui.entities.Coordinates
import be.marche.apptravaux.utils.FileHelper
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import timber.log.Timber
import java.io.File
import java.util.UUID.randomUUID

@HiltWorker
class AvaloirSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val avaloirRepository: AvaloirRepository,
    private val avaloirService: AvaloirService,
) : Worker(context, workerParameters) {

    companion object {
        const val WORK_RESULT = "work_result"
        val WORK_UUID = randomUUID()
    }

    override fun doWork(): Result {
        val taskData = inputData
        val taskDataString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)
        Timber.d("do work taskdata ${taskDataString}")
        val notificationString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)
        val outputData = Data.Builder().putString(WORK_RESULT, "Task Started")

        sleep(30)

        upload()

        if (syncContent(taskData)) {
            outputData.putString(WORK_RESULT, "Task Finished").build()
            showNotification("Make it Easy", taskDataString.toString())
            return Result.success(outputData.build())
        }

        outputData.putString(WORK_RESULT, "Task Fail").build()
        showNotification("Make it Easy", taskDataString.toString())
        return Result.success(outputData.build())
    }

    private fun showNotification(task: String, desc: String) {
        Timber.d("show notif")
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "message_channel"
        val channelName = "message_name"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(applicationContext, channelId)
            .setContentTitle(task)
            .setContentText(desc)
            .setSmallIcon(R.drawable.ic_outline_notifications_active_24)

        manager.notify(1, builder.build())
    }

    private fun syncContent(taskData: Data): Boolean {
        try {
            val response = avaloirService.fetchAllAvaloirsNotSuspend()
            val res = response.execute()
            if (res.isSuccessful) {
                Timber.d("work avaloirs ${res.body()}")
                res.body()?.let { avaloirRepository.insertAvaloirsNotSuspend(it) }
                return true
            } else {
                Firebase.crashlytics.log("error sync ${res.code()} ${res.body()}")
            }
            return false
        } catch (e: Exception) {
            Firebase.crashlytics.recordException(e)
            Timber.d("work err $e")
        }
        return false
    }

    private fun upload(): Boolean {

        val fileHelper = FileHelper()

        avaloirRepository.getAllDraftsList().forEach { avaloirDraft ->

            Timber.d("avaloir draft try ${avaloirDraft}")

            val imgFile = File(avaloirDraft.imageUrl)
            val requestBody = fileHelper.createRequestBody(imgFile)
            val part = fileHelper.createPart(imgFile, requestBody)

            val coordinates = Coordinates(avaloirDraft.latitude, avaloirDraft.longitude)
            val response = avaloirService.insertAvaloirNotSuspend(coordinates, part, requestBody)

            val res = response.execute()
            if (res.isSuccessful) {
                res.body()?.let { dataMessage ->
                    val avaloir = dataMessage.avaloir
                    Timber.d("avaloir added ${avaloir}")
                    avaloirRepository.deleteAvaloirDraft(avaloirDraft)
                }
            } else {
                Timber.d("avaloir failed ${response}")
                //{"type":"https:\/\/tools.ietf.org\/html\/rfc2616#section-10","title":"An error occurred","status":500,"detail":"Internal Server Error"}
            }

        }

        return true
    }
}