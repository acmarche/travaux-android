package be.marche.apptravaux.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Rational
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.core.impl.PreviewConfig
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import be.marche.apptravaux.databinding.FragmentCameraBinding
import be.marche.apptravaux.permission.PermissionUtil
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class CameraFragment : Fragment() {
    companion object {
        fun newInstance() = CameraFragment()
    }

    private lateinit var viewModelState: CameraViewModelState
    private val REQUEST_PERMISSION_CAMERA = 1
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
        cameraViewModel.getState().observe(viewLifecycleOwner, Observer {
            updateUi(it)
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
        bindCameraUseCases()
    }

    override fun onPause() {
        super.onPause()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

    private fun updateUi(state: CameraViewModelState) {
        Timber.w("zeze CallingUpdateUi()" + state::class.java)
        viewModelState = state
        val res = when (state) {
            is CameraViewModelState.SetupCamera -> handle()
            is CameraViewModelState.Error -> handleStateError(state)
        }
        with(state) {
            binding.btnCapture.isEnabled = buttonsEnabled
            binding.btnGallery.isEnabled = buttonsEnabled
        }
        val metrics = DisplayMetrics().apply {
            binding.previewTextureView.display.getRealMetrics(this)
        }

    }

    private fun handle(): Any {
        TODO("Not yet implemented")
    }

    private fun handleStateError(state: CameraViewModelState.Error) {
        Snackbar.make(
                binding.coordinatorLayout,
                "Error: {${state.errorMessage}}",
                Snackbar.LENGTH_LONG
            )
            .show()
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