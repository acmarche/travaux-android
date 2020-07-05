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

class ProduitListFragment : Fragment(), ProduitListAdapter.ProduitListAdapterListener {

    val produitViewModel: ProduitViewModel by viewModel()
    val categorieViewModel: CategorieViewModel by sharedViewModel()
    var categorie: Categorie? = null
    private var _binding: ProduitListFragmentBinding? = null
    private val binding get() = _binding!!

    private var listener: ProduitListAdapter.ProduitListAdapterListener? = null
    private lateinit var produitListAdapter: ProduitListAdapter

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
                    notifyAdapter(produits)
                })
        } else {
            produitViewModel.produits.observe(
                viewLifecycleOwner,
                Observer<List<Produit>> { produits ->
                    produits?.let { notifyAdapter(it) }
                })
        }

        checkInternet()
    }

    private fun notifyAdapter(newproduits: List<Produit>) {
        produitListAdapter.setProduits(newproduits)
    }

    override fun onProduitSelected(produit: Produit) {
        Toast.makeText(getActivity(), getString(R.string.help_text), Toast.LENGTH_SHORT).show()
    }

    override fun onBtnLessSelected(produit: Produit) {
        changeQuantite(produit, 1)
    }

    override fun onBtnPlusSelected(produit: Produit) {
        changeQuantite(produit, 2)
    }

    private fun changeQuantite(produit: Produit, action: Int) {
        when (action) {
            1 -> if (produit.quantite > 0) {
                produitViewModel.changeQuantite(produit, produit.quantite - 1)
                produitViewModel.saveAsync(produit, produit.quantite)
            }
            2 -> {
                produitViewModel.changeQuantite(produit, produit.quantite + 1)
                produitViewModel.saveAsync(produit, produit.quantite)
            }
        }
    }

    private fun checkInternet() {
        ConnectivityLiveData(requireActivity().application).observe(
            viewLifecycleOwner,
            Observer { connected ->
                when (connected) {
                    true -> {
                        binding.messageView.visibility = View.INVISIBLE
                        binding.recyclerViewProduitList.visibility = View.VISIBLE
                    }
                    false -> {
                        binding.messageView.visibility = View.VISIBLE
                        binding.recyclerViewProduitList.visibility = View.INVISIBLE
                    }
                }
            })
    }
}