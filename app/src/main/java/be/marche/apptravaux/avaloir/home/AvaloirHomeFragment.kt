package be.marche.apptravaux.avaloir.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.BuildConfig
import be.marche.apptravaux.R
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirHomeBinding
import be.marche.apptravaux.permission.PermissionUtil
import be.marche.apptravaux.utils.NetworkUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel

class AvaloirHomeFragment : Fragment() {

    private val LOCATION_REQUEST_CODE = 1
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.size > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        alertDialog()
                    }
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun alertDialog() {
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Localisation n??cessaire")
            .setMessage("L'application ne peux pas fonctionner sans la location. Merci de l'autoriser dans les param??tres")
            .setPositiveButton("OK") { dialog, id -> startIntentSettings() }
        dialog.show()
    }

    private fun startIntentSettings() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri: Uri = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        intent.data = uri
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    private fun setupPermissions() {
        if (!permissionUtil.checkSelfPermissions(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            val message =
                "La localisation est requise pour g??rer les avaloirs."
            permissionUtil.requestPermissionsWithExplanation(
                this,
                "Localiser precis",
                message,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                LOCATION_REQUEST_CODE
            )
        }
    }

    private fun refreshDataBase() {
        activity?.application?.let {
            NetworkUtils.getNetworkLiveData(it).observe(viewLifecycleOwner, { connected ->
                when (connected) {
                    true -> {
                        binding.errorTextView.text = getString(R.string.message_ok_connectivity)
                        binding.errorTextView.visibility = View.INVISIBLE
                        binding.btnSearch.isEnabled = true
                        syncContent()
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
            avaloirModel.insertAvaloirs(avaloirs)
        })
        avaloirModel.getDatesFromServer().observe(viewLifecycleOwner, Observer { dates ->
            avaloirModel.insertDates(dates)
        })
        avaloirModel.getCommentairesFromServer()
            .observe(viewLifecycleOwner, Observer { commentaires ->
                avaloirModel.insertCommentaires(commentaires)
            })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}