package be.marche.apptravaux.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import be.marche.apptravaux.R
import be.marche.apptravaux.TravauxApplication
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.repository.AvaloirRepository
import be.marche.apptravaux.screens.avaloir.AvaloirSyncScreen
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID.randomUUID
import javax.inject.Inject

@HiltWorker
class AvaloirSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParameters: WorkerParameters,
    private val avaloirRepository: AvaloirRepository,
    private val avaloirService: AvaloirService,
) : Worker(context, workerParameters) {
    override fun doWork(): Result {
        val taskData = inputData
        val taskDataString = taskData.getString(AvaloirSyncScreen.MESSAGE_STATUS)

        Log.d("ZEZE", "do work")
        showNotification("Make it Easy", taskDataString.toString())
        Log.d("ZEZE", "do work ${avaloirRepository.getAll()}")

        val outputData = Data.Builder().putString(WORK_RESULT, "Task Finished").build()

        return Result.success(outputData)
    }

    private fun showNotification(task: String, desc: String) {
        Log.d("ZEZE", "show notif")
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

    companion object {
        const val WORK_RESULT = "work_result"
        val WORK_UUID = randomUUID()
    }
}