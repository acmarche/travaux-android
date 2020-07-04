package be.marche.apptravaux.stock.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Categorie(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nom: String,
    val description: String?
) {
    var nbproduits: Int = 0
}