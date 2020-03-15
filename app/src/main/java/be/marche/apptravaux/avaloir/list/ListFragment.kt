package be.marche.apptravaux.avaloir.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirListBinding
import be.marche.apptravaux.permission.PermissionUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class ListFragment : Fragment(), AvaloirListAdapter.AvaloirListAdapterListener {

    private var _binding: FragmentAvaloirListBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    lateinit var permissionUtil: PermissionUtil
    private var listener: AvaloirListAdapter.AvaloirListAdapterListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        permissionUtil = PermissionUtil(requireContext())

        listener = this

        val recyclerView = binding.recyclerViewAvaloirs
        val adapter = context?.let { AvaloirListAdapter(listener) }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        avaloirModel.getAll().observe(viewLifecycleOwner, Observer { avaloirs ->
            avaloirs?.let { adapter?.setAvaloirs(avaloirs) }
        })
    }

    override fun onAvaloirSelected(avaloir: Avaloir) {
        avaloirModel.changeValueCurrentAvaloir(avaloir)
        findNavController().navigate(R.id.action_listFragment_to_showFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}