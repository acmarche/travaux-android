package be.marche.apptravaux.permission

import android.app.AlertDialog
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

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