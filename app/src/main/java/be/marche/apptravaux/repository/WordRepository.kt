package be.marche.apptravaux.repository

import androidx.annotation.WorkerThread
import be.marche.apptravaux.database.AvaloirDao
import be.marche.apptravaux.entities.Avaloir
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class WordRepository(private val wordDao: AvaloirDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allWords: Flow<List<Avaloir>> = wordDao.getFlowList()
    val allWords2: List<Avaloir> = wordDao.getAll()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(avaloir: Avaloir) {
        wordDao.insert(avaloir)
    }
}