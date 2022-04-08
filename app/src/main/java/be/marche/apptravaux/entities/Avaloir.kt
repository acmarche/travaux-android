package be.marche.apptravaux.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

//createdAt: 2022-04-07T15:34:41+02:00
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
    val descriptif: String? = null,
    val createdAt: Date
)

@Entity
data class Commentaire(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val idReferent: Int,
    val avaloirId: Int,
    val content: String,
    val createdAt: Date
)

@Entity
data class DateNettoyage(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val idReferent: Int,
    val avaloirId: Int,
    val createdAt: Date
)

@Entity
data class Sync(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val createdAt: Date,
    val content: String,
    val result: Boolean
)