package be.marche.apptravaux.avaloir.home

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.api.ConnectivityLiveData
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirHomeBinding
import be.marche.apptravaux.geofence.GeofenceManager
import be.marche.apptravaux.permission.PermissionUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private val RECORD_REQUEST_CODE = 1
    lateinit var permissionUtil: PermissionUtil
    private val avaloirModel: AvaloirViewModel by viewModel()
    private var _binding: FragmentAvaloirHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        permissionUtil = PermissionUtil(requireContext())
        setupPermissions()

        refreshDataBase()

        binding.goBtn.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_listFragment)
        }
        binding.btnSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

    private fun refreshDataBase() {
        activity?.application?.let {
            ConnectivityLiveData(it).observe(viewLifecycleOwner, Observer { connected ->
                when (connected) {
                    true -> {
                        binding.errorTextView.text = getString(R.string.message_ok_connectivity)
                        binding.errorTextView.visibility = View.INVISIBLE
                        binding.btnSearch.isEnabled = true
                        //          syncContent()
                    }
                    false -> {
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.btnSearch.isEnabled = false
                        binding.errorTextView.text = getString(R.string.message_no_connectivity)
                    }
                }
            })
        }
    }

    private fun syncContent() {
        avaloirModel.getAllAvaloirsFromServer().observe(viewLifecycleOwner, Observer { avaloirs ->
            Timber.w("zeze sync all avaloirs size: " + avaloirs.size)
            avaloirModel.insertAvaloirs(avaloirs)
        })
        avaloirModel.getDatesFromServer().observe(viewLifecycleOwner, Observer { dates ->
            Timber.w("zeze sync all dates size: " + dates.size)
            avaloirModel.insertDates(dates)
        })
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Timber.w("zeze Permission has been denied by user")
                } else {
                    Timber.w("zeze Permission has been granted by user")
                }
            }
        }
    }

    private fun setupPermissions() {
        val message =
            "La localisation est requise pour gérer les avaloirs."
        permissionUtil.requestPermissionsWithExplanation(
            this,
            message,
            "",
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            RECORD_REQUEST_CODE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}