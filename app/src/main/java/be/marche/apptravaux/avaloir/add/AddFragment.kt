package be.marche.apptravaux.avaloir.add

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import be.marche.apptravaux.R
import be.marche.apptravaux.api.ConnectivityLiveData
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.camera.CameraViewModel
import be.marche.apptravaux.databinding.FragmentAvaloirAddBinding
import be.marche.apptravaux.permission.PermissionUtil
import be.marche.apptravaux.utils.FileHelper
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddFragment : Fragment(), LifecycleOwner {

    companion object {
        fun newInstance() = AddFragment()
    }

    private val permissionUtil: PermissionUtil by inject()
    lateinit var currentPhotoPath: String
    private val REQUEST_PERMISSION_CAMERA = 1
    val REQUEST_IMAGE_CAPTURE = 2
    private var _binding: FragmentAvaloirAddBinding? = null
    private val binding get() = _binding!!
    private val cameraViewModel: CameraViewModel by viewModel()
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
        bindCameraUseCases()
        checkInternet()

        binding.btnCancel.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
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
                    createImageFile()
                } catch (ex: IOException) {
                    Timber.w("zeze create fail img " + ex.message)
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        requireActivity().applicationContext,
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
            if (data != null) {
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
        Timber.w("zeze request code " + requestCode)
        when (requestCode) {
            REQUEST_PERMISSION_CAMERA -> {
                if (grantResults.size != 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                    cameraViewModel.errorPermissionDenied()
                    return
                }
                return bindCameraUseCases()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val format = SimpleDateFormat(
            "yyyy-MM-dd HH:mm:ss",
            Locale.getDefault()
        )
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${format}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun bindCameraUseCases() {
        if (!permissionUtil.checkSelfPermissions(Manifest.permission.CAMERA)) {
            permissionUtil.requestPermissionsWithExplanation(
                this,
                "",
                "La caméra est nécessaire pour photographier les avaloirs",
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CAMERA
            )
            return
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