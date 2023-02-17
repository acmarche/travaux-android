package be.marche.apptravaux.viewModel

import android.annotation.SuppressLint
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.work.*
import be.marche.apptravaux.networking.CoroutineDispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

@HiltViewModel
@SuppressLint("StaticFieldLeak")
class WorkerViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val coroutineDispatcherProvider: CoroutineDispatcherProvider
) : ViewModel() {

    companion object {
        val STOCK_SYNC_WORK_REQUEST = "STOCK_SYNC_WORK_REQUEST"
        val AVALOIR_SYNC_WORK_REQUEST = "AVALOIR_SYNC_WORK_REQUEST"
        val SYNC_WORK_TAG = "tagSyncAppTravaux"
    }

    val workManager by lazy { WorkManager.getInstance(applicationContext) }

    internal fun cancelWork(unikWorkName: String) {
        workManager.cancelAllWorkByTag(unikWorkName)
        workManager.cancelUniqueWork(AVALOIR_SYNC_WORK_REQUEST)
        workManager.cancelUniqueWork(STOCK_SYNC_WORK_REQUEST)
    }

    fun createRequest(
        taskData: Data,
        classWorker: Class<out ListenableWorker>,
        tagName: String
    ): OneTimeWorkRequest {
        val powerConstraints = Constraints.Builder().setRequiresBatteryNotLow(true).build()
        val networkConstraints =
            Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()

        return OneTimeWorkRequest.Builder(classWorker)
            .setConstraints(powerConstraints)
            .setConstraints(networkConstraints)
            .setInputData(taskData)
            .addTag(tagName)
            .build()
    }

    internal fun enqueueWorkRequest(request: OneTimeWorkRequest, unikWorkName: String) {
        workManager.enqueueUniqueWork(
            unikWorkName,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

}