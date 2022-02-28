package be.marche.apptravaux.worker

import android.content.Context
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters

class AvaloirSyncWorker (context: Context, workerParameters: WorkerParameters): Worker(context, workerParameters) {
    override fun doWork(): Result {
        val taskData = inputData
        val outputData = Data.Builder().putString(WORK_RESULT, "Task Finished").build()

        return Result.success(outputData)
    }

    companion object {
        const val WORK_RESULT = "work_result"
    }
}