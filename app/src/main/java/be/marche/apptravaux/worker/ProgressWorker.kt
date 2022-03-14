package be.marche.apptravaux.worker

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.work.*
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import timber.log.Timber

//https://developer.android.com/topic/libraries/architecture/workmanager/how-to/intermediate-progress
class ProgressWorker(context: Context, parameters: WorkerParameters) :
    CoroutineWorker(context, parameters) {

    companion object {
        const val Progress = "Progress"
        private const val delayDuration = 5L
        private const val WORK_NAME = "UploadPhotoWorker"

        fun run2(context: Context): LiveData<WorkInfo> {
            val work = OneTimeWorkRequestBuilder<ProgressWorker>()
                .build()

            val manager = WorkManager.getInstance(context)
            manager.enqueueUniqueWork(WORK_NAME, ExistingWorkPolicy.REPLACE, work)
            return manager.getWorkInfoByIdLiveData(work.id)
        }
    }

    override suspend fun doWork(): Result /*= withContext(Dispatchers.IO)*/ {
        return t()
    }

    suspend fun t(): Result {
        val firstUpdate = workDataOf(Progress to 0)
        val lastUpdate1 = workDataOf(Progress to 30)
        val lastUpdate2 = workDataOf(Progress to 60)
        val lastUpdate = workDataOf(Progress to 100)
        setProgress(firstUpdate)
        delay(delayDuration)
        setProgress(lastUpdate1)
        delay(delayDuration)
        setProgress(lastUpdate2)
        delay(delayDuration)
        setProgress(lastUpdate)
        return Result.success()
    }

    suspend fun x(): Result {
        for (x in 1..100) {
            val progressData = workDataOf(Progress to x)
            setProgress(progressData)

            // do something
            try {
                delay(4000)
                Timber.d("Cancelled")

            } catch (e: CancellationException) {
                Timber.d("Cancelled")
            }


            if (isStopped) {
                Timber.d("isStopped")
                return Result.success(workDataOf(Progress to x))
            }
        }
        return Result.success(workDataOf(Progress to 100))
    }

}