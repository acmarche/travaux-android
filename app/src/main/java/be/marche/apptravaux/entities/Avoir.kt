package be.marche.apptravaux.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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
)

@Entity
data class AvaloirDraft(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    var latitude: Double,
    var longitude: Double,
    var imageUrl: String,
)

@Entity
data class Commentaire(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val avaloirId: Int,
    val content: String,
    val createdAt: Date
)

@Entity
data class DateNettoyage(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val avaloirId: Int,
    val date: Date
)

@Entity
data class Sync(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val createdAt: Date,
    val content: String,
    val result: Boolean
)