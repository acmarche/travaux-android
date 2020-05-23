package be.marche.apptravaux.avaloir.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.entity.DateNettoyage
import be.marche.apptravaux.avaloir.entity.Commentaire

const val DATABASE_NAME = "apptravaux"

@Database(
    entities = [Avaloir::class, DateNettoyage::class, Commentaire::class],
    version = 23
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun avaloirDao(): AvaloirDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}