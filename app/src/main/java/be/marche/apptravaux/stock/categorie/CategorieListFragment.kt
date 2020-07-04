package be.marche.apptravaux.stock.categorie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.R
import be.marche.apptravaux.databinding.CategorieListFragmentBinding
import be.marche.apptravaux.databinding.FragmentStockHomeBinding
import be.marche.apptravaux.stock.entity.Categorie
import be.marche.apptravaux.stock.produit.ProduitViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CategorieListFragment : Fragment(), CategorieListAdapter.CategorieListAdapterListener {

    val categorieViewModel: CategorieViewModel by sharedViewModel()
    val produitViewModel: ProduitViewModel by viewModel()

    private var listener: CategorieListAdapter.CategorieListAdapterListener? = null
    private lateinit var categorieListAdapter: CategorieListAdapter
    private lateinit var categories: MutableList<Categorie>

    private var _binding: CategorieListFragmentBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = CategorieListFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = CategorieListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (!::categories.isInitialized) {
            categories = mutableListOf()
        }

        activity?.title = getString(R.string.app_name)
        listener = this
        categorieListAdapter = CategorieListAdapter(categories, listener)

        binding.recyclerViewCategorieList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = categorieListAdapter
        }

        categorieViewModel.getCategories().observe(viewLifecycleOwner, Observer {
            for (categorie in it) {
                produitViewModel.getProduitsByCategorie(categorie)
                    .observe(viewLifecycleOwner, Observer {
                        categorie.nbproduits = it.size
                    })
            }
            UpdateUi(it)
        })
    }

    private fun UpdateUi(newcategories: List<Categorie>) {
        categories.clear()
        categories.addAll(newcategories)
        categorieListAdapter.notifyDataSetChanged()
    }

    override fun onCategorieSelected(categorie: Categorie) {
        categorieViewModel.categorie = categorie
        findNavController().navigate(R.id.action_categorieListFragment_to_produitListFragment)
    }

}