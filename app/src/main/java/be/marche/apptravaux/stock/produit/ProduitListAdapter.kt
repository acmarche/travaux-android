package be.marche.apptravaux.stock.produit

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import be.marche.apptravaux.R
import be.marche.apptravaux.stock.entity.Produit
import android.text.Editable
import timber.log.Timber
import java.util.*

class ProduitListAdapter internal constructor(
    private val listener: ProduitListAdapterListener?
) : RecyclerView.Adapter<ProduitViewHolder>(), View.OnClickListener {

    interface ProduitListAdapterListener {
        fun onProduitSelected(produit: Produit)
        fun onQuantiteChanged(produit: Produit, quantite: Int)
        fun onBtnLessSelected(produit: Produit)
        fun onBtnPlusSelected(produit: Produit)
    }

    private var produits = emptyList<Produit>() // Cached copy

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
            produitQuantiteView.setOnClickListener(this@ProduitListAdapter)

            cardView.tag = produit
            btnLessView.tag = produit
            btnPlusView.tag = produit
            produitNomView.text = produit.nom
            produitQuantiteView.text = produit.quantite.toString()
            produitQuantiteView.tag = produit

            produitQuantiteView.setOnFocusChangeListener(object : View.OnFocusChangeListener {
                override fun onFocusChange(view: View?, blur: Boolean) {
                    if (view != null) {

                        produitQuantiteView.addTextChangedListener(object : TextWatcher {

                            private var timer: Timer = Timer()
                            private val DELAY: Long = 750 // Millisecond
                            override fun beforeTextChanged(
                                s: CharSequence?,
                                start: Int,
                                count: Int,
                                after: Int
                            ) {
                                // nothing to do here
                            }

                            override fun onTextChanged(
                                newQuantite: CharSequence,
                                start: Int,
                                before: Int,
                                count: Int
                            ) {
                                // nothing to do here
                            }

                            override fun afterTextChanged(editable: Editable?) {
                                var quantite: Int
                                timer.cancel()
                                timer = Timer()
                                timer.schedule(
                                    object : TimerTask() {
                                        override fun run() {
                                            if (produitQuantiteView.text.isNotEmpty()) {
                                                quantite =
                                                    produitQuantiteView.text.toString().toInt()
                                                Timber.w("zeze emit " + quantite)
                                                listener?.onQuantiteChanged(
                                                    produitQuantiteView.tag as Produit,
                                                    quantite
                                                )
                                            }
                                        }
                                    },
                                    DELAY
                                )
                            }
                        })
                    }
                }
            })
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