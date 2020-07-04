package be.marche.apptravaux.stock.categorie

import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R

class CategorieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cardView = itemView.findViewById<CardView>(R.id.cardViewCategorie)!!
    val categorieNom = itemView.findViewById<TextView>(R.id.categorieNom)
    val categorieDescription = itemView.findViewById<TextView>(R.id.categorieDescription)
    val categorieNbrProduit = itemView.findViewById<TextView>(R.id.nbrProduitsView)



}