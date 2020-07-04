package be.marche.apptravaux.stock.produit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.R
import be.marche.apptravaux.api.ConnectivityLiveData
import be.marche.apptravaux.databinding.ProduitListFragmentBinding
import be.marche.apptravaux.stock.categorie.CategorieViewModel
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.entity.Produit
import kotlinx.android.synthetic.main.produit_list_fragment.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProduitListFragment : Fragment(), ProduitListAdapter.ProduitListAdapterListener {

    val produitViewModel: ProduitViewModel by viewModel()
    val categorieViewModel: CategorieViewModel by sharedViewModel()
    var categorie: Categorie? = null
    private var _binding: ProduitListFragmentBinding? = null
    private val binding get() = _binding!!

    private var listener: ProduitListAdapter.ProduitListAdapterListener? = null
    private lateinit var produitListAdapter: ProduitListAdapter
    private lateinit var produits: MutableList<Produit>

    companion object {
        fun newInstance() = ProduitListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProduitListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (!::produits.isInitialized) {
            produits = mutableListOf<Produit>()
        }

        listener = this
        produitListAdapter = ProduitListAdapter(listener)

        recyclerViewProduitList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = produitListAdapter
        }

        categorie = categorieViewModel.categorie
        if (categorie != null) {
            activity?.title = categorie!!.nom
            produitViewModel.getProduitsByCategorie(categorie!!)
                .observe(viewLifecycleOwner, Observer { produits ->
                    UpdateUi(produits)
                })
        } else {
            produitViewModel.produits.observe(
                viewLifecycleOwner,
                Observer<List<Produit>> { UpdateUi(it) })
        }
    }

    private fun UpdateUi(newproduits: List<Produit>) {
        produits.clear()
        produits.addAll(newproduits)
        produitListAdapter.setProduits(produits)
    }

    override fun onProduitSelected(produit: Produit) {
        Toast.makeText(getActivity(), getString(R.string.help_text), Toast.LENGTH_SHORT).show()
    }

    override fun onBtnLessSelected(produit: Produit) {
        Timber.w("zeze moins")
        checkInternet(produit, 1)
    }

    override fun onBtnPlusSelected(produit: Produit) {
        Timber.w("zeze plus")
        checkInternet(produit, 2)
    }

    private fun checkInternet(produit: Produit, action: Int) {

        ConnectivityLiveData(activity?.application).observe(
            viewLifecycleOwner,
            Observer { connected ->

                when (connected) {
                    true -> {
                        when (action) {
                            1 -> if (produit.quantite > 0) {
                                Timber.w("zeze action " + action)
                                produitViewModel.saveAsync(produit, produit.quantite - 1)
                            }
                            2 -> {
                                produitViewModel.saveAsync(produit, produit.quantite + 1)
                                Timber.w("zeze action " + action)
                            }
                        }
                    }
                    false -> {
                        Toast.makeText(
                            getActivity(),
                            getString(R.string.message_no_connectivity),
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            })
    }
}