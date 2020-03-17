package be.marche.apptravaux.avaloir.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.BuildConfig
import be.marche.apptravaux.R
import be.marche.apptravaux.api.ConnectivityLiveData
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirAddBinding
import be.marche.apptravaux.permission.PermissionUtil
import be.marche.apptravaux.utils.FileHelper
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.io.File
import java.io.IOException

class AddFragment : Fragment(), LifecycleOwner {

    companion object {
        fun newInstance() = AddFragment()
    }

    private val permissionUtil: PermissionUtil by inject()
    private val fileHelper: FileHelper by inject()
    lateinit var currentPhotoPath: String
    private val REQUEST_PERMISSION_CAMERA = 1
    private val REQUEST_IMAGE_CAPTURE = 2
    private var _binding: FragmentAvaloirAddBinding? = null
    private val binding get() = _binding!!
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var avaloir: Avaloir

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAvaloirAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupPermissions()
        checkInternet()

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_homeFragment)
        }

        binding.btnAddPhoto.setOnClickListener {
            dispatchTakePictureIntent()
        }

        binding.AddDescriptionTextView.text = requireContext().getString(
            R.string.description_add_avaloir,
            avaloirModel.coordinates.latitude.toString(),
            avaloirModel.coordinates.longitude.toString()
        )
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {

                    val externalFilesDir =
                        requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!

                    fileHelper.createImageFile(externalFilesDir).apply {
                        currentPhotoPath = absolutePath
                    }

                } catch (ex: IOException) {
                    //Timber.w("zeze create fail img " + ex.message)
                    null
                }

                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity(),
                        requireActivity().application.packageName + ".fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val imgFile = File(currentPhotoPath);
            if (imgFile.exists()) {
                val fileHelper = FileHelper()
                val requestBody = fileHelper.createRequestBody(imgFile)
                val part = fileHelper.createPart(imgFile, requestBody)
                avaloirModel.insertAsync(avaloirModel.coordinates, part, requestBody)
                findNavController().navigate(R.id.action_addFragment_to_showFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> {
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
        val builder = AlertDialog.Builder(context)
        builder
            .setTitle("Appareil photo nécessaire")
            .setMessage("L'application ne peux pas fonctionner sans l'appareil photo. Merci de l'autoriser dans les paramètres")
        builder.setPositiveButton("OK") { dialog, id -> startIntentSettings() }
        builder.create().show()
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
        if (!permissionUtil.checkSelfPermissions(Manifest.permission.CAMERA)) {
            val message = "La caméra est requise pour ajouter un avaloir."
            permissionUtil.requestPermissionsWithExplanation(
                this,
                "La caméra est nécessaire pour photographier les avaloirs",
                message,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_PERMISSION_CAMERA
            )
        }
    }

    private fun checkInternet() {
        activity?.application?.let {
            ConnectivityLiveData(it).observe(viewLifecycleOwner, Observer { connected ->
                when (connected) {
                    true -> {
                        binding.errorTextView.text = getString(R.string.message_ok_connectivity)
                        binding.errorTextView.visibility = View.INVISIBLE
                        binding.btnAddPhoto.isEnabled = true
                        //          syncContent()
                    }
                    false -> {
                        binding.errorTextView.visibility = View.VISIBLE
                        binding.btnAddPhoto.isEnabled = false
                        binding.errorTextView.text = getString(R.string.message_no_connectivity)
                    }
                }
            })
        }
    }
}