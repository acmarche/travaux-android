package be.marche.apptravaux.stock.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.databinding.FragmentStockHomeBinding
import be.marche.apptravaux.stock.SyncViewModel
import be.marche.apptravaux.stock.categorie.CategorieViewModel
import be.marche.apptravaux.utils.NetworkUtils
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class StockHomeFragment : Fragment() {

    val syncViewModel: SyncViewModel by viewModel()
    val categorieViewModel: CategorieViewModel by sharedViewModel()
    private var _binding: FragmentStockHomeBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance() = StockHomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStockHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        refreshDataBase()
        categorieViewModel.categorie = null

        binding.btnCategorieView.setOnClickListener {
            findNavController().navigate(R.id.action_stockHomeFragment_to_categorieListFragment)
        }
        binding.btnProduitView.setOnClickListener {
            findNavController().navigate(R.id.action_stockHomeFragment_to_produitListFragment)
        }
    }

    private fun refreshDataBase() {
        NetworkUtils.getNetworkLiveData(requireActivity().application)
            .observe(requireActivity(), { connected ->
                when (connected) {
                    true -> {
                        binding.messageView.visibility = View.INVISIBLE
                        binding.btnProduitView.visibility = View.VISIBLE
                        binding.btnCategorieView.visibility = View.VISIBLE
                        syncViewModel.refreshData()
                    }
                    false -> {
                        binding.messageView.visibility = View.VISIBLE
                        binding.btnProduitView.visibility = View.INVISIBLE
                        binding.btnCategorieView.visibility = View.INVISIBLE
                        binding.messageView.text = getString(R.string.message_no_connectivity)
                    }
                }
            })
    }
}