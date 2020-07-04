package be.marche.apptravaux.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.RedirectActivity
import be.marche.apptravaux.databinding.FragmentTravauxHomeBinding

class TravauxHomeFragment : Fragment() {
    private var _binding: FragmentTravauxHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTravauxHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.btnGoAvaloir.setOnClickListener {
            val intent = Intent(context, RedirectActivity::class.java)
            //  startActivity(intent)
            findNavController().navigate(R.id.action_travauxHomeFragment_to_avaloirHomeFragment)
        }
        binding.btnGoStock.setOnClickListener {
            findNavController().navigate(R.id.action_travauxHomeFragment_to_stockHomeFragment)
        }
    }
}