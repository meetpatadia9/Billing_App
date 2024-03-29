package com.codebyzebru.myapplication.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.databinding.FragmentProfileBinding
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.databinding.ToastErrorBinding
import com.codebyzebru.myapplication.databinding.ToastSuccessBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.File

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding

    private lateinit var userID: String
    private lateinit var databaseReference: DatabaseReference

    private lateinit var byteArray: ByteArray
    private lateinit var imgURI: Uri
    private lateinit var imgURL: String
    private var gender: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        (activity as HomeActivity).title = "User Profile"

        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as HomeActivity).supportActionBar?.hide()

        imgURI = Uri.EMPTY
        byteArray = ByteArrayOutputStream().toByteArray()
        userID = this.arguments?.getString("userID").toString()
        databaseReference = Firebase.database.reference.child("Users/$userID")

        val localFile = File.createTempFile("tempFile", "jpeg")
        FirebaseStorage.getInstance().getReference("Profile Images/$userID").getFile(localFile)
            .addOnSuccessListener {
                val bitmapFactory =BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(requireContext().applicationContext)
                    .load(bitmapFactory)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.profileImg)
            }
            .addOnFailureListener {
                Log.e("____________________", "ProfileFragment")
                Log.e("Failed to load image", it.message.toString())
            }

        FirebaseStorage.getInstance().getReference("Profile Images/$userID").downloadUrl
            .addOnSuccessListener {
                Log.d("imgURI", it.toString())
                imgURL = it.toString()

                Glide.with(requireContext().applicationContext).asFile().load(imgURL).into(object : CustomTarget<File>() {
                    override fun onResourceReady(resource: File, transition: Transition<in File>?) {
                        // Image downloaded successfully
                        // Continue with the upload process
                        imgURI = Uri.fromFile(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {

                    }
                })
            }

        databaseReference.get()
            .addOnSuccessListener {
                binding.profileEdtxtName.setText(it.child("fullName").value.toString())
                binding.profileEdtxtEmail.setText(it.child("email").value.toString())
                binding.profileEdtxtContact.setText(it.child("contact").value.toString())
                if (it.child("companyName").value.toString().isNotEmpty()) {
                    binding.profileEdtxtCompanyName.setText(it.child("companyName").value.toString())
                }
                if (it.child("address").value.toString().isNotEmpty()) {
                    binding.profileEdtxtAddress.setText(it.child("address").value.toString())
                }
                if (it.child("gender").value.toString() == "Male") {
                    binding.profileRbMale.isChecked = true
                }
                else if (it.child("gender").value.toString() == "Female") {
                    binding.profileRbFemale.isChecked = true
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }

        binding.profileImg.setOnClickListener {
            val bottomSheet = BottomSheetDialog(requireContext())
            bottomSheet.setContentView(R.layout.bottomsheet_profile_image)
            bottomSheet.show()

            bottomSheet.findViewById<ConstraintLayout>(R.id.bs_layoutCam)
                ?.setOnClickListener {
                    Toast.makeText(requireContext(), "Camera", Toast.LENGTH_SHORT).show()
                    bottomSheet.dismiss()
                    openCameraForImage()
                }

            bottomSheet.findViewById<ConstraintLayout>(R.id.bs_layoutGallery)
                ?.setOnClickListener {
                    Toast.makeText(requireContext(), "Gallery", Toast.LENGTH_SHORT).show()
                    bottomSheet.dismiss()
                    openGalleryForImage()
                }
        }

        binding.imgVBack.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }

        binding.btnSaveProfile.setOnClickListener {
            if (binding.profileEdtxtName.text.toString().trim() == "") {
                stopProgressbar()
                binding.profileTIL1.helperText = "Required*"
            }
            else if (binding.profileEdtxtContact.text.toString().trim() == "") {
                stopProgressbar()
                binding.profileTIL5.helperText = "Required*"
            }
            else {
                saveProfile()
            }
        }
    }

    private fun openCameraForImage() {
        val cameraIntent = Intent()
        cameraIntent.action = MediaStore.ACTION_IMAGE_CAPTURE
        imgFromCam.launch(cameraIntent)
    }

    private val imgFromCam: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != AppCompatActivity.RESULT_CANCELED) {
                //  get image and load it into `imageview`
                val image = it.data!!.extras!!["data"] as Bitmap
                binding.profileImg.setImageBitmap(image)

                //  we have image in form of `bitmap`, to upload it on firebase-storage we need to convert `Bitmap` in `ByteArray`
                val bitmap =(binding.profileImg.drawable as BitmapDrawable).bitmap
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                byteArray = baos.toByteArray()
            }
        }

    private fun openGalleryForImage() {
        val galleryIntent = Intent()
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.type = "image/*"
        imgFromGallery.launch(galleryIntent)
    }

    private val imgFromGallery: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode != AppCompatActivity.RESULT_CANCELED) {
                //  get image and load it into `imageview`
                imgURI = it.data!!.data!!
                binding.profileImg.setImageURI(imgURI)
            }
        }

    private fun saveProfile() {
        startProgressbar()

        /*val radioGroup = binding.profileGenderRg.checkedRadioButtonId
        val gender = requireView().findViewById<RadioButton>(radioGroup).text.toString()*/

        val radioGroup = binding.profileGenderRg.checkedRadioButtonId
        gender = requireView().findViewById(radioGroup)
        
        if (binding.profileEdtxtName.text.toString().trim().isNotEmpty()) {
            databaseReference.child("fullName").setValue(binding.profileEdtxtName.text.toString().trim())
        }
        if (binding.profileEdtxtCompanyName.text?.toString()?.trim()?.isNotEmpty() == true) {
            databaseReference.child("companyName").setValue(binding.profileEdtxtCompanyName.text.toString().trim())
        }
        if (binding.profileEdtxtEmail.text?.toString()?.trim()?.isNotEmpty() == true) {
            databaseReference.child("email").setValue(binding.profileEdtxtEmail.text.toString().trim())
        }
        if (binding.profileEdtxtContact.text.toString().trim().isNotEmpty()) {
            databaseReference.child("contact").setValue(binding.profileEdtxtContact.text.toString().trim())
        }
        if (gender?.text.toString().isNotEmpty()) {
            databaseReference.child("gender").setValue(gender)
        }
        else {
            databaseReference.child("gender").setValue("")
        }
        if (binding.profileEdtxtAddress.text?.toString()?.trim()?.isNotEmpty() == true) {
            databaseReference.child("address").setValue(binding.profileEdtxtAddress.text.toString().trim())
        }

        if (byteArray.isNotEmpty()) {
            FirebaseStorage.getInstance().getReference("Profile Images/$userID").putBytes(byteArray)
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.popBackStack()
                    stopProgressbar()
                    greenToast()
                }
                .addOnFailureListener {
                    stopProgressbar()
                    redToast()
                }
        }
        else if (imgURI.toString().isNotEmpty()) {
            FirebaseStorage.getInstance().getReference("Profile Images/$userID").putFile(imgURI)
                .addOnSuccessListener {
                    requireActivity().supportFragmentManager.popBackStack()
                    stopProgressbar()
                    greenToast()
                }
                .addOnFailureListener {
                    stopProgressbar()
                    redToast()
                }
        }
        else {
            requireActivity().supportFragmentManager.popBackStack()
            stopProgressbar()
            greenToast()
        }
    }

    private fun greenToast() {
        val toastBinding = ToastSuccessBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = getText(R.string.txt_profile_updated)
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun redToast() {
        val toastBinding = ToastErrorBinding.inflate(LayoutInflater.from(requireContext()))
        val toast = Toast(requireContext())
        toastBinding.txtToastMessage.text = getText(R.string.txt_something_wrong)
        toast.view = toastBinding.root
        toast.duration = Toast.LENGTH_LONG
        toast.show()
    }

    private fun startProgressbar() {
        binding.btnSaveProfile.visibility = View.GONE
        binding.profileProgressbar.visibility = View.VISIBLE
    }

    private fun stopProgressbar() {
        binding.btnSaveProfile.visibility = View.VISIBLE
        binding.profileProgressbar.visibility = View.GONE
    }
}