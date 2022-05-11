package be.marche.apptravaux.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class ErrorLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int?,
    val nom: String,
    val description: String
) {

}
