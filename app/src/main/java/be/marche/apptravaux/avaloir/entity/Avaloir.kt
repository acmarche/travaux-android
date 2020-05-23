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
    val rue: String? = null,
    val numero: String? = null,
    val localite: String? = null,
    var imageUrl: String? = null,
    val descriptif: String? = null
)