package be.marche.apptravaux.avaloir.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DateNettoyage(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val avaloirId: Int,
    val date: Date
)