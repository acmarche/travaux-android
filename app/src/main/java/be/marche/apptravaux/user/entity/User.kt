package be.marche.apptravaux.user.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nom: String,
    val prenom: String,
    val email: String,
    val token: String
) {
    override fun toString(): String {
        return email
    }
}
