package be.marche.apptravaux.camera

import androidx.camera.core.CameraSelector
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel

sealed class CameraViewModelState(
    val buttonsEnabled: Boolean = false,
    val switchCameraVisible: Boolean = false,
    val cameraLensDirection: Int = CameraSelector.LENS_FACING_BACK
) {
    class SetupCamera(
        switchCameraVisible: Boolean,
        cameraLensDirection: CameraSelector.LensFacing
    ) : CameraViewModelState(
        switchCameraVisible = switchCameraVisible,
        buttonsEnabled = false,
        cameraLensDirection = cameraLensDirection
    )

    class Error(
        val errorMessage: String,
        switchCameraVisible: Boolean
    ) : CameraViewModelState(
        switchCameraVisible = switchCameraVisible,
        buttonsEnabled = false
    )
}

class CameraViewModel : ViewModel() {

    private val isSwitchCameraEnabled: Boolean = true
    private val state = MutableLiveData<CameraViewModelState>()

    fun getState(): LiveData<CameraViewModelState> = state

    fun errorPermissionDenied() {
        state.value = CameraViewModelState.Error(
            errorMessage = "Camera denied cant not take photo",
            switchCameraVisible = isSwitchCameraEnabled
        )
    }

}