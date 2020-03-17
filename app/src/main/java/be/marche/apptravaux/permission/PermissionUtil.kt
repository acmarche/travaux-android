package be.marche.apptravaux.permission

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import timber.log.Timber

class PermissionUtil(val context: Context) {

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
        Timber.w("zeze request permission " + permission)
        fragment.requestPermissions(arrayOf(permission), code)
    }

    fun requestPermissionsWithExplanation(
        fragment: Fragment,
        title: String = "Permission required",
        message: String,
        permissions: Array<String>,
        code: Int
    ) {Timber.w("zeze permission request ")
        for (permission in permissions) {
            Timber.w("zeze permission ask " + permission)
            Timber.w("zeze permission ask " + fragment)
                if (fragment.shouldShowRequestPermissionRationale(permission)) {

            Timber.w("zeze permission builder " )
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
            Timber.w("zeze permission not explication necessary ")
                    this.requestPermissions(fragment, permission, code)
                }

        }
    }

}