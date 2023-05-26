@file:Suppress("DEPRECATION", "PrivatePropertyName")

package com.codebyzebru.myapplication.activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.codebyzebru.myapplication.broadcastreceiver.ConnectivityReceiver
import com.codebyzebru.myapplication.databinding.ActivityProfileBinding
import com.codebyzebru.myapplication.dataclasses.ProfileDataClass
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File
import com.codebyzebru.myapplication.R
import java.util.regex.Pattern

class ProfileActivity : AppCompatActivity(), ConnectivityReceiver.ConnectivityReceiverListener {

    private lateinit var binding: ActivityProfileBinding

    private var isConnected: Boolean = true
    private var snackBar : Snackbar? = null

    private lateinit var userID: String
    private lateinit var email: String
    private lateinit var phoneNum: String
    private var gender: RadioButton? = null
    private lateinit var password: String

    private lateinit var databaseReference: DatabaseReference

    private lateinit var byteArray: ByteArray
    private lateinit var imgURI: Uri
    private lateinit var imgURL: String

    private val EMAIL_ADDRESS_PATTERN = Pattern.compile(
        "[a-zA-Z\\d+._%\\-]{1,256}" +            //  \\d == 0 to 9
                "@" +
                "[a-zA-Z\\d][a-zA-Z\\d\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z\\d][a-zA-Z\\d\\-]{0,25}" +
                ")+"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userID = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = Firebase.database.reference.child("Users/$userID")
        imgURI = Uri.EMPTY
        byteArray = ByteArrayOutputStream().toByteArray()

        email = intent.getStringExtra("email").toString()
        phoneNum = intent.getStringExtra("phoneNum").toString()
        password = intent.getStringExtra("password").toString()

        if(email.isNotEmpty() && email != "null") {
            binding.newProfileEdtxtEmail.setText(email)
        }
        if(phoneNum.isNotEmpty() && phoneNum != "null") {
            binding.newProfileEdtxtPhone.setText(phoneNum)
        }

        FirebaseDatabase.getInstance().getReference("Users/$userID")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(ProfileDataClass::class.java)
                        userData!!.key = snapshot.key.toString()

                        binding.newProfileEdtxtName.setText(userData.fullName)
                        binding.newProfileEdtxtOrg.setText(userData.companyName)
                        binding.newProfileEdtxtEmail.setText(userData.email)
                        binding.newProfileEdtxtAddress.setText(userData.address)

                        if (userData.gender == "Male") {
                            binding.newProfileRbMale.isChecked = true
                        } else {
                            binding.newProfileRbFemale.isChecked = true
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Failed to load user details", error.message)
                }
            })

        val localFile = File.createTempFile("tempFile", "jpeg")
        FirebaseStorage.getInstance().getReference("Profile Images/$userID").getFile(localFile)
            .addOnSuccessListener {
                val bitmap = BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(applicationContext).load(bitmap).into(binding.newProfileImg)
            }
            .addOnFailureListener {
                Log.d("Failed to load profile image", it.message.toString())
            }

        FirebaseStorage.getInstance().getReference("Profile Images/$userID").downloadUrl
            .addOnSuccessListener {
                Log.d("imgURI", it.toString())
                imgURL = it.toString()

                Glide.with(applicationContext).asFile().load(imgURL).into(object : CustomTarget<File>() {
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        // Image downloaded successfully
                        // Continue with the upload process
                        imgURI = Uri.fromFile(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
            }

        binding.newProfileImg.setOnClickListener {
            val bottomSheet = BottomSheetDialog(this)
            bottomSheet.setContentView(R.layout.bottomsheet_profile_image)
            bottomSheet.show()

            bottomSheet.findViewById<ConstraintLayout>(R.id.bs_layoutCam)
                ?.setOnClickListener {
                    Toast.makeText(this, "Camera", Toast.LENGTH_SHORT).show()
                    bottomSheet.dismiss()
                    openCameraForImage()
                }

            bottomSheet.findViewById<ConstraintLayout>(R.id.bs_layoutGallery)
                ?.setOnClickListener {
                    Toast.makeText(this, "Gallery", Toast.LENGTH_SHORT).show()
                    bottomSheet.dismiss()
                    openGalleryForImage()
                }
        }

        binding.newProfileBtnSave.setOnClickListener {
            addNewUser()
        }
    }

    private fun openCameraForImage() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        imgFromCam.launch(cameraIntent)
    }

    private val imgFromCam: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //  get image and load it into `imageview`
            val image = it.data!!.extras!!["data"] as Bitmap
            binding.newProfileImg.setImageBitmap(image)

            //  we have image in form of `bitmap`, to upload it on firebase-storage we need to convert `Bitmap` in `ByteArray`
            val bitmap =(binding.newProfileImg.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            byteArray = baos.toByteArray()
        }

    private fun openGalleryForImage() {
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        imgFromGallery.launch(galleryIntent)
    }

    private val imgFromGallery: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //  get image and load it into `imageview`
            imgURI = it.data!!.data!!
            Log.d("imgURI", imgURI.toString())
            binding.newProfileImg.setImageURI(imgURI)
        }

    private fun addNewUser() {
        startProgressbar()

        val radioGroup = binding.newProfileRgGender.checkedRadioButtonId
        gender = findViewById(radioGroup)

        if (binding.newProfileEdtxtName.text.toString().trim() == "") {
            stopProgressbar()
            binding.til1.helperText = "Required*"
        }
        else if (binding.newProfileEdtxtEmail.text.toString().trim() == "") {
            stopProgressbar()
            binding.til2.helperText = "Required*"
        }
        else if(!isValidString(binding.newProfileEdtxtEmail.text.toString())) {
            stopProgressbar()
            binding.til2.helperText = "Enter valid Email"
        }
        else if (binding.newProfileEdtxtOrg.text.toString().trim() == "") {
            stopProgressbar()
            binding.til3.helperText = "Required*"
        }
        else if (binding.newProfileEdtxtPhone.text.toString().trim() == "") {
            stopProgressbar()
            binding.til4.helperText = "Required*"
        }

        if (binding.newProfileEdtxtName.text.toString().trim() != "" && binding.newProfileEdtxtOrg.text.toString().trim() != "" &&
            binding.newProfileEdtxtEmail.text.toString().trim() != "" && binding.newProfileEdtxtPhone.text.toString().trim() != "") {
            if (byteArray.isNotEmpty()) {
                uploadProfileData()
                //  uploading image on firebase-storage
                FirebaseStorage.getInstance().getReference("Profile Images/$userID").putBytes(byteArray)
                    .addOnSuccessListener {
                        startProgressbar()
                        uploadProfileData()
                        updateUI()
                        greenToast()
                    }
                    .addOnFailureListener {
                        redToast(it.message.toString())
                        stopProgressbar()
                    }
            }
            else if (imgURI.toString().isNotEmpty()) {
                uploadProfileData()
                //  uploading image on firebase-storage
                FirebaseStorage.getInstance().getReference("Profile Images/$userID").putFile(imgURI)
                    .addOnSuccessListener {
                        startProgressbar()
                        uploadProfileData()
                        updateUI()
                        greenToast()
                    }
                    .addOnFailureListener {
                        redToast(it.message.toString())
                        stopProgressbar()
                    }
            }
            else {
                redToast("Please select your profile image!!")
                stopProgressbar()
            }
        }
    }

    //  CHECKING ENTERED EMAIL VALIDITY
    private fun isValidString(str: String): Boolean{
        return EMAIL_ADDRESS_PATTERN.matcher(str).matches()
    }

    private fun redToast(message: String) {
        val toast: Toast = Toast.makeText(this, message, Toast.LENGTH_SHORT)
        val view = toast.view

        //  Gets the actual oval background of the Toast then sets the colour filter
        view!!.background.setColorFilter(resources.getColor(R.color.color5), PorterDuff.Mode.SRC_IN)

        //  Gets the TextView from the Toast so it can be edited
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(resources.getColor(R.color.white))

        toast.show()
    }

    private fun greenToast() {
        val toast: Toast = Toast.makeText(this, "Profile created successfully!!", Toast.LENGTH_SHORT)
        val view = toast.view

        //  Gets the actual oval background of the Toast then sets the colour filter
        view!!.background.setColorFilter(resources.getColor(R.color.color5), PorterDuff.Mode.SRC_IN)

        //  Gets the TextView from the Toast so it can be edited
        val text = view.findViewById<TextView>(android.R.id.message)
        text.setTextColor(resources.getColor(R.color.white))

        toast.show()
    }

    private fun startProgressbar() {
        binding.newProfileBtnSave.visibility = View.GONE
        binding.newProfileProgressbar.visibility = View.VISIBLE
    }

    private fun stopProgressbar() {
        binding.newProfileBtnSave.visibility = View.VISIBLE
        binding.newProfileProgressbar.visibility = View.GONE
    }

    private fun uploadProfileData() {
        databaseReference.child("fullName").setValue(binding.newProfileEdtxtName.text.toString().trim())
        databaseReference.child("companyName").setValue(binding.newProfileEdtxtOrg.text.toString().trim())
        databaseReference.child("email").setValue(binding.newProfileEdtxtEmail.text.toString().trim())
        databaseReference.child("contact").setValue(binding.newProfileEdtxtPhone.text.toString().trim())
        databaseReference.child("address").setValue(binding.newProfileEdtxtAddress.text.toString().trim())
        databaseReference.child("gender").setValue(gender?.text.toString())
        databaseReference.child("password").setValue(password.trim())
    }

    private fun updateUI() {
        startActivity(
            Intent(this, HomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        )
    }

    /*
           CHECKING FOR ACTIVE INTERNET CONNECTION
   */
    private fun noInternet() {
        isConnected = false
    }

    private fun internetConnected() {
        isConnected = true
    }

    override fun onNetworkConnectionChange(isConnected: Boolean) {
        showNetworkMessage(isConnected)
    }

    override fun onResume() {
        super.onResume()
        ConnectivityReceiver.connectivityReceiverListener = this
    }

    private fun showNetworkMessage(isConnected: Boolean) {
        if (!isConnected) {
            noInternet()
            snackBar = Snackbar.make(findViewById(R.id.layout_profileActivity), "Connection loss", Snackbar.LENGTH_LONG)

            val view = snackBar?.view
            snackBar?.duration = BaseTransientBottomBar.LENGTH_INDEFINITE
            snackBar?.setBackgroundTint(resources.getColor(R.color.color4))
            val params = view?.layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            snackBar?.show()
        }
        else {
            internetConnected()
            snackBar?.dismiss()
        }
    }

}