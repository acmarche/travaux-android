package be.marche.apptravaux

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import be.marche.apptravaux.database.AppDatabase
import be.marche.apptravaux.repository.WordRepository
import be.marche.apptravaux.worker.AvaloirWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class TravauxApplication : Application(), Configuration.Provider {

    companion object {
        val galleryDir by lazy {
            be.marche.apptravaux.utils.galleryDir()
        }
    }

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    // No need to cancel this scope as it'll be torn down with the process
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { WordRepository(database.avaloirDao()) }

    @Inject
    lateinit var workerFactory: AvaloirWorkerFactory
    //lateinit var workerFactory: HiltWorkerFactory ! va pas grrr !

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(android.util.Log.INFO)
            .setWorkerFactory(workerFactory)
            .build()

    //val avaloirRepository by lazy { AvaloirRepository(database.avaloirDao()) }
    override fun onCreate() {
        super.onCreate()

        if (!galleryDir.exists()) {
            galleryDir.mkdirs()
        }
    }
}
