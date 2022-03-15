package be.marche.apptravaux.entities

import androidx.room.Entity
import androidx.room.ForeignKey
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


@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Categorie::class,
            parentColumns = ["id"],
            childColumns = ["categorie_id"]
        )
    ]
)
class Produit(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nom: String,
    val categorie_id: Int,
    var quantite: Int,
    val reference: String?,
    val image: String?,
    val description: String?
)