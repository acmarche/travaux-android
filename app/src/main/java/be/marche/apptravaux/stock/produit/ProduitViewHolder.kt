package be.marche.apptravaux.stock.produit

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R

class ProduitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val cardView = itemView.findViewById<CardView>(R.id.cardViewProduit)!!
    val produitNomView = itemView.findViewById<TextView>(R.id.produitNomView)!!
    //val produitPhotoView = itemView.findViewById<ImageView>(R.id.produitPhotoView)
    val produitQuantiteView = itemView.findViewById<TextView>(R.id.produitQuantite)
    val btnLessView = itemView.findViewById<ImageView>(R.id.btnLess)
    val btnPlusView = itemView.findViewById<ImageView>(R.id.btnPlus)
}