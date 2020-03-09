package be.marche.apptravaux.avaloir.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Avaloir(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val idReferent: Int,
    var latitude: Double,
    var longitude: Double,
    val descriptif: String? = null
)