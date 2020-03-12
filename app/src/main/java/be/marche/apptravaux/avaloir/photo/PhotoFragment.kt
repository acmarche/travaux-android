package be.marche.apptravaux.avaloir.photo

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
import be.marche.apptravaux.avaloir.entity.Avaloir
import be.marche.apptravaux.avaloir.model.AvaloirViewModel
import be.marche.apptravaux.camera.CameraViewModel
import be.marche.apptravaux.databinding.FragmentCameraBinding
import be.marche.apptravaux.permission.PermissionUtil
import be.marche.apptravaux.utils.FileHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class PhotoFragment : Fragment(), LifecycleOwner {

    companion object {
        fun newInstance() = PhotoFragment()
    }

    lateinit var currentPhotoPath: String
    private val REQUEST_PERMISSION_CAMERA = 1
    val REQUEST_IMAGE_CAPTURE = 2
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    lateinit var permissionUtil: PermissionUtil
    private val cameraViewModel: CameraViewModel by viewModel()
    private val avaloirModel: AvaloirViewModel by sharedViewModel()
    private lateinit var avaloir: Avaloir

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        permissionUtil = PermissionUtil(requireContext())
        bindCameraUseCases()

        avaloirModel.avaloir.observe(viewLifecycleOwner, androidx.lifecycle.Observer { avaloir ->
            this.avaloir = avaloir
        })

        binding.captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        Timber.w("zeze intent capture")
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

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File =
            requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Timber.w("zeze activity result")
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data != null) {
                val imgFile = File(currentPhotoPath);
                if (imgFile.exists()) {

                    binding.viewFinder.setImageURI(Uri.fromFile(imgFile))
                    this.avaloir.imageUrl = currentPhotoPath
                    avaloirModel.insertAvaloir(avaloir)

                    val fileHelper = FileHelper()
                    val requestBody = fileHelper.createRequestBody(imgFile)
                    val part = fileHelper.createPart(imgFile, requestBody)
                    avaloirModel.uploadImage(avaloir, part, requestBody)

                }
            }
        }
    }

    private fun bindCameraUseCases() {
        if (!permissionUtil.checkSelfPermissions(Manifest.permission.CAMERA)) {
            permissionUtil.requestPermissionWithExplanation(
                this,
                "",
                "La caméra est nécessaire pour photographier les avaloirs",
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_PERMISSION_CAMERA
            )
            return
        }
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
}