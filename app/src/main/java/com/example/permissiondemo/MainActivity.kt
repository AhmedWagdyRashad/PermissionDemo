package com.example.permissiondemo

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        const val REQUEST_PERMISSION_SETTING = 101
        const val CAMERA_PERMISSION_REQUEST_CODE = 102
        const val TAG = "Permission"
    }

    // if you want to request single permission
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.i(TAG, "permission is granted")
            openCamera()
        } else {
            Log.i(TAG, "permission is denied")
         //   requestPermission()
           displayNeverAskAgainDialog()

        }
    }

    // if you want to request multiple permissions
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.i(TAG, "${it.key} = ${it.value}")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        button.setOnClickListener {
            // if you want to request single permission
            requestPermission()
            //requestPermissionLauncher.launch(Manifest.permission.CAMERA)

            // if you want to request multiple permissions
          /*  requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )*/
        }
    }

        private fun openCamera() {
            val intent = Intent("android.media.action.IMAGE_CAPTURE")
            startActivity(intent)
        }

        private fun requestPermission() {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted use the CAMERA
                    Log.i(TAG, "use the CAMERA")
                    openCamera()
                }
                ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.CAMERA
                ) -> {
                    // Additional rational should be displayed  permission denied
                    //Toast.makeText(this, "Additional rational", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Additional rational")
                   requestPermissionLauncher.launch(Manifest.permission.CAMERA)

                }
                else -> {
                    // Permission has not been asked yet
                    //Toast.makeText(this, "Permission has not been asked yet", Toast.LENGTH_LONG).show()
                    Log.i(TAG, "Permission has not been asked yet")
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }

        private fun openSetting() {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityForResult(intent, REQUEST_PERMISSION_SETTING)
        }

        private fun displayNeverAskAgainDialog() {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(
                """
                We need to access camera for performing necessary task
                Please permit the permission through Settings screen.
                Permissions -> Enable permission
                """.trimIndent()
            )
            builder.setCancelable(false)
            builder.setPositiveButton("Setting") { dialog, which ->
                dialog.dismiss()
                openSetting()
            }
            builder.setNegativeButton("Cancel", null)
            builder.show()
        }


}