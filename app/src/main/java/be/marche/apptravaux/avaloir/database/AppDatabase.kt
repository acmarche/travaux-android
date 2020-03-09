package be.marche.apptravaux.avaloir.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import be.marche.apptravaux.avaloir.entity.Avaloir

const val DATABASE_NAME = "apptravaux"

@Database(
    entities = [Avaloir::class],
    version = 3
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun avaloirDao(): AvaloirDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }
}