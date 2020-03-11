package be.marche.apptravaux.camera

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.Preview
import androidx.camera.core.impl.PreviewConfig
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import be.marche.apptravaux.permission.PermissionUtil
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import be.marche.apptravaux.databinding.FragmentCameraBinding
import android.util.Size
import android.graphics.Matrix
import android.net.Uri
import android.provider.MediaStore
import android.view.TextureView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException
import java.util.concurrent.TimeUnit
import android.os.Environment
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

// This is an arbitrary number we are using to keep track of the permission
// request. Where an app has multiple context for requesting permission,
// this can help differentiate the different contexts.
private const val REQUEST_CODE_PERMISSIONS = 10

// This is an array of all the permission specified in the manifest.
private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

class CameraFragment : Fragment(), LifecycleOwner {
    companion object {
        fun newInstance() = CameraFragment()
    }

    lateinit var currentPhotoPath: String
    private lateinit var viewModelState: CameraViewModelState
    private val REQUEST_PERMISSION_CAMERA = 1
    val REQUEST_IMAGE_CAPTURE = 2
    private var _binding: FragmentCameraBinding? = null
    private val binding get() = _binding!!
    lateinit var permissionUtil: PermissionUtil
    private val cameraViewModel: CameraViewModel by sharedViewModel()

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
        binding.captureButton.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent2() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(requireActivity().packageManager)?.also {
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File

                    Timber.w("zeze create img " + ex.message)
                    null
                }
                Timber.w("zeze iciii ")
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

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            if (data != null) {
                val imgFile = File(currentPhotoPath);
                if (imgFile.exists()) {
                    binding.viewFinder.setImageURI(Uri.fromFile(imgFile))
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