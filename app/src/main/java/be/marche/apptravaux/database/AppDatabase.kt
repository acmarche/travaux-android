package be.marche.apptravaux.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import be.marche.apptravaux.entities.Avaloir
import be.marche.apptravaux.entities.AvaloirDraft
import be.marche.apptravaux.entities.Commentaire
import be.marche.apptravaux.entities.DateNettoyage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

const val DATABASE_NAME = "apptravaux"

@Database(
    entities = [Avaloir::class, AvaloirDraft::class, DateNettoyage::class, Commentaire::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun avaloirDao(): AvaloirDao

    companion object {

        /**
         * pour populate
         */
        private class WordDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch {
                        populateDatabase(database.avaloirDao())
                    }
                }
            }

            suspend fun populateDatabase(wordDao: AvaloirDao) {
                // Delete all content here.
                // wordDao.deleteAll()
                // Add sample words.
                var word = Avaloir(1, 1, 50.5, 5.5, "Hello")
                wordDao.insert(word)
            }
        }//endpopulate

        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): AppDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(WordDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
