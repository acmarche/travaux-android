package be.marche.apptravaux.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import be.marche.apptravaux.networking.AvaloirService
import be.marche.apptravaux.repository.AvaloirRepository
import javax.inject.Inject

class AvaloirWorkerFactory @Inject constructor(
    val avaloirRepository: AvaloirRepository,
    val avaloirService: AvaloirService,
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker = AvaloirSyncWorker(appContext, workerParameters, avaloirRepository, avaloirService)
}

