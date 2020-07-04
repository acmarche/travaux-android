package be.marche.apptravaux.stock.categorie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.stock.entity.Categorie

class CategorieListAdapter(
    private val categories: List<Categorie>,
    private val listener: CategorieListAdapterListener?
) : RecyclerView.Adapter<CategorieViewHolder>(), View.OnClickListener {

    interface CategorieListAdapterListener {
        fun onCategorieSelected(categorie: Categorie)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategorieViewHolder {
        val viewItem = LayoutInflater.from(parent.context)
            .inflate(R.layout.categorie_item, parent, false)
        return CategorieViewHolder(viewItem)
    }

    override fun getItemCount(): Int = categories.size

    override fun onBindViewHolder(holder: CategorieViewHolder, position: Int) {
        val categorie = categories[position]

        val quantityString = holder.itemView.resources.getQuantityString(
            R.plurals.nbr_produits,
            categorie.nbproduits,
            categorie.nbproduits
        )

        with(holder) {
            cardView.setOnClickListener(this@CategorieListAdapter)
            cardView.tag = categorie
            categorieNom.text = categorie.nom
            categorieNbrProduit.text = quantityString
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.cardViewCategorie -> listener?.onCategorieSelected(view.tag as Categorie)
        }
    }

}