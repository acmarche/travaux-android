package be.marche.apptravaux.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.marche.apptravaux.entities.*

const val DATABASE_NAME = "apptravaux"

@Database(
    entities = [
        Avaloir::class,
        DateNettoyage::class,
        Commentaire::class,
        Sync::class,
        Categorie::class,
        Produit::class,
        QuantiteDraft::class,
        ErrorLog::class
    ],
    version = 46,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun avaloirDao(): AvaloirDao
    abstract fun stockDao(): StockDao
    abstract fun errorDao(): ErrorDao

    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(
            context: Context
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
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
