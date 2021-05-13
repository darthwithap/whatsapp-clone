package me.darthwithap.whatsappclone.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sign_up.*
import me.darthwithap.whatsappclone.BaseActivity
import me.darthwithap.whatsappclone.R
import me.darthwithap.whatsappclone.models.User


class SignUpActivity : BaseActivity() {
    private lateinit var exitSnackbar: Snackbar
    private var rationaleCount: Int = 0
    private var profileImageUri: Uri? = null
    private lateinit var sharedPref: SharedPreferences
    private var profileImgDownloadUrl = ""
    private lateinit var phNo: String

    private val firestoreDatabase by lazy {
        FirebaseFirestore.getInstance().collection("cookie-chat-users")
    }
    private val firebaseStorage by lazy {
        FirebaseStorage.getInstance().getReference("cookie-chat")
    }
    private val firebaseAuth by lazy {
        FirebaseAuth.getInstance()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_sign_up)

        sharedPref = getPreferences(Context.MODE_PRIVATE)
        rationaleCount = sharedPref.getInt(RATIONALE_SHARED_PREF, 0)
        saveRationaleToSharedPref(rationaleCount)
        phNo = intent.getStringExtra(PHONE_NUMBER).toString()
        initViews()
    }

    private fun saveRationaleToSharedPref(rationale: Int) {
        with(sharedPref.edit()) {
            putInt(RATIONALE_SHARED_PREF, rationale)
            apply()
        }
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let {
                profileImageUri = it
                ivAddImageIcon.visibility = View.GONE
                ivAddImageIconBackground.visibility = View.GONE
                ivProfileImage.setImageURI(it)
            }
        }
    }

    private fun initViews() {
        exitSnackbar =
            Snackbar.make(llParentSignUp, "Please press back again to exit.", Snackbar.LENGTH_SHORT)

        ivProfileImage.setOnClickListener {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                openImageChooser()
            } else {
                requestPermissions(
                    arrayOf(
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ),
                    STORAGE_REQUEST_CODE
                )
            }
        }

        btnFinish.setOnClickListener {
            if (!etName.text.isNullOrBlank()) {
                startUploadToFirebase()
            } else Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startUploadToFirebase() {
        pbUploadingToFirebase.visibility = View.VISIBLE
        if (profileImageUri != null) uploadImageToFirebase()
        else {
            //THE LAST ARGUMENT NEEDS TO BE OF A THUMBNAIL URL OF THE DOWNLOAD IMAGE, THIS IS TO BE DONE
            val user = firebaseAuth.uid?.let { User(it, etName.text.toString(), phNo) }
            if (user != null) {
                firestoreDatabase.document(firebaseAuth.uid!!).set(user)
                    .addOnCompleteListener {
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }.addOnFailureListener {
                        Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                        pbUploadingToFirebase.visibility = View.GONE
                    }
            }
        }
    }

    private fun uploadImageToFirebase() {
        val profileImageRef = firebaseStorage.child("uploads/profile-img/${firebaseAuth.uid}")
        val imageUploadTask = profileImageUri?.let { profileImageRef.putFile(it) }
        imageUploadTask?.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation profileImageRef.downloadUrl
        })?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                profileImgDownloadUrl = task.result.toString()
                //THE LAST ARGUMENT NEEDS TO BE OF A THUMBNAIL URL OF THE DOWNLOAD IMAGE, THIS IS TO BE DONE
                val user = firebaseAuth.uid?.let {
                    User(
                        it,
                        etName.text.toString(),
                        phNo,
                        profileImgDownloadUrl,
                        profileImgDownloadUrl //NEEDS TO BE THUMBNAIL URL
                    )
                }
                if (user != null) {
                    firestoreDatabase.document(firebaseAuth.uid!!).set(user)
                        .addOnCompleteListener {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, it.localizedMessage, Toast.LENGTH_SHORT).show()
                            Log.d(TAG, "uploadImageToFirebase: ${it.localizedMessage}")
                            pbUploadingToFirebase.visibility = View.GONE
                            profileImageRef.delete()
                        }
                }
            } else {
                pbUploadingToFirebase.visibility = View.GONE
                btnFinish.isEnabled = true
                Toast.makeText(this, "Some error occurred. Please try again", Toast.LENGTH_SHORT)
                    .show()
            }
        }?.addOnFailureListener {
            pbUploadingToFirebase.visibility = View.GONE
            Toast.makeText(this, "Some error occurred. Please try again", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (grantResults.isEmpty()) return
        val permissionsGranted: Boolean
        if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            permissionsGranted = false
        } else {
            openImageChooser()
            return
        }
        if (!permissionsGranted) {
            var foreverDenied = false
            if (shouldShowRequestPermissionRationale(permissions[0])) {
                //permission was initially denied
            } else {
                foreverDenied = true
                rationaleCount++
                saveRationaleToSharedPref(rationaleCount)
            }
            if (foreverDenied && rationaleCount > 1) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permission Denied")
                    .setMessage(
                        """You have denied the permission to read storage. """ +
                                """Please open settings and allow the permission for you to pick a profile image"""
                    )
                    .setPositiveButton("Settings") { _, _ ->
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        )
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false)
                    .create()
                dialog.show()
            }
        }
    }

    override fun onBackPressed() {
        if (exitSnackbar.isShown) {
            super.onBackPressed()
            return
        }
        exitSnackbar.show()
    }

    companion object {
        private const val STORAGE_REQUEST_CODE = 1000
        private const val PICK_IMAGE_REQUEST_CODE = 1001
        private const val RATIONALE_SHARED_PREF = "rationale"
        private const val PHONE_NUMBER = "phoneNumber"
        private const val TAG = "SignUpActivity"
    }
}