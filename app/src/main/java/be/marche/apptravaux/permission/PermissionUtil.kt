package be.marche.apptravaux.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import timber.log.Timber

class PermissionUtil(val context: Context) {

    val RECORD_REQUEST_CODE = 1

    fun checkSelfPermissions(permission: String): Boolean {

        val permission = ContextCompat.checkSelfPermission(
            context,
            permission
        )

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Timber.w("zeze Permission to record denied")
            return false
        }
        return true
    }


    fun requestPermissions(fragment: Fragment, permission: String) {
        Timber.w("zeze rquest")
        fragment.requestPermissions(arrayOf(permission),RECORD_REQUEST_CODE)
    }

    fun requestPermissionWithExplanation(fragment: Fragment, message: String, title: String = "Permission required", permission: String) {
        if (this.checkSelfPermissions(permission) == false) {
            if (fragment.shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                val builder = AlertDialog.Builder(context)
                builder.setMessage(message)
                    .setTitle(title)

                builder.setPositiveButton(
                    "OK"
                ) { dialog, id ->

                    this.requestPermissions(fragment, permission)
                }

                val dialog = builder.create()
                dialog.show()
            } else {
                this.requestPermissions(fragment, permission)
            }
        }
    }

}