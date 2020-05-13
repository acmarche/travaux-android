package be.marche.apptravaux.avaloir.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Commentaire(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val avaloirId: Int,
    val content: String
)