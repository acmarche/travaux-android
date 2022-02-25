package be.marche.apptravaux

import android.app.Application
import be.marche.apptravaux.database.AppDatabase
import be.marche.apptravaux.repository.WordRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@HiltAndroidApp
class TravauxApplication : Application() {
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
    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { WordRepository(database.avaloirDao()) }

    //val avaloirRepository by lazy { AvaloirRepository(database.avaloirDao()) }
    override fun onCreate() {
        super.onCreate()

        if (!galleryDir.exists()) {
            galleryDir.mkdirs()
        }
    }
}
