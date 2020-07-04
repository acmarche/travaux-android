package be.marche.apptravaux.stock.produit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.stock.entity.Produit

class ProduitListAdapter internal constructor(
    private val listener: ProduitListAdapterListener?
) : RecyclerView.Adapter<ProduitViewHolder>(), View.OnClickListener {

    interface ProduitListAdapterListener {
        fun onProduitSelected(produit: Produit)

        fun onBtnLessSelected(produit: Produit)
        fun onBtnPlusSelected(produit: Produit)
    }

    private var produits = emptyList<Produit>() // Cached copy of words

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProduitViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.produit_item, parent, false)
        return ProduitViewHolder(viewItem)
    }

    override fun getItemCount(): Int = produits.size

    override fun onBindViewHolder(holder: ProduitViewHolder, position: Int) {
        val produit = produits[position]

        with(holder) {

            cardView.setOnClickListener(this@ProduitListAdapter)
            btnLessView.setOnClickListener(this@ProduitListAdapter)
            btnPlusView.setOnClickListener(this@ProduitListAdapter)

            cardView.tag = produit
            btnLessView.tag = produit
            btnPlusView.tag = produit
            produitNomView.text = produit.nom
            produitQuantiteView.text = produit.quantite.toString()

            /*   if (produit.image!!.isNotEmpty()) {
                   Picasso.get()
                       .load(produit.image)
                       .placeholder(R.drawable.ic_image_black_24dp)
                       .into(produitPhotoView)
               }*/
        }
    }

    internal fun setProduits(produits: List<Produit>) {
        this.produits = produits
        notifyDataSetChanged()
    }

    override fun onClick(view: View) {

        when (view.id) {
            R.id.cardViewProduit -> listener?.onProduitSelected(view.tag as Produit)
            R.id.btnLess -> listener?.onBtnLessSelected(view.tag as Produit)
            R.id.btnPlus -> listener?.onBtnPlusSelected(view.tag as Produit)
        }
    }
}