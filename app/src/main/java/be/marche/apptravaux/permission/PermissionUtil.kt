package be.marche.apptravaux.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState

class PermissionUtil(val context: Context) {

    companion object {
        val listOfPermissions =
            if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                listOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.NEARBY_WIFI_DEVICES,
                )
            } else {
                listOf(
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                )
            }

        @OptIn(ExperimentalPermissionsApi::class)
        fun getPermissionsText(permissions: List<PermissionState>): String {
            val revokedPermissionsSize = permissions.size
            if (revokedPermissionsSize == 0) return ""

            val textToShow = StringBuilder().apply {
                append("Le ")
            }

            for (i in permissions.indices) {
                textToShow.append(permissions[i].permission)
                when {
                    revokedPermissionsSize > 1 && i == revokedPermissionsSize - 2 -> {
                        textToShow.append(", et ")
                    }
                    i == revokedPermissionsSize - 1 -> {
                        textToShow.append(" ")
                    }
                    else -> {
                        textToShow.append(", ")
                    }
                }
            }
            textToShow.append(if (revokedPermissionsSize == 1) "permission est" else "permissions sont")
            return textToShow.toString()
        }
    }

    fun checkSelfPermissions(permission: String): Boolean {
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    fun requestPermissions(fragment: Fragment, permission: String, code: Int) {
        fragment.requestPermissions(arrayOf(permission), code)
    }

    fun requestPermissionsWithExplanation(
        fragment: Fragment,
        title: String = "Permission required",
        message: String,
        permissions: Array<String>,
        code: Int
    ) {
        for (permission in permissions) {
            if (fragment.shouldShowRequestPermissionRationale(permission)) {

                val builder = AlertDialog.Builder(context)
                builder.setMessage(message)
                    .setTitle(title)
                builder.setPositiveButton(
                    "OK"
                ) { dialog, id ->
                    this.requestPermissions(fragment, permission, code)
                }
                val dialog = builder.create()
                dialog.show()
            } else {
                this.requestPermissions(fragment, permission, code)
            }

        }
    }
}