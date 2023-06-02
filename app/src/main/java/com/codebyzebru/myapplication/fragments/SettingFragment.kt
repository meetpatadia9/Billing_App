package com.codebyzebru.myapplication.fragments

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.SettingListAdapter
import com.codebyzebru.myapplication.databinding.FragmentSettingBinding
import com.codebyzebru.myapplication.dataclasses.ProfileDataClass
import com.codebyzebru.myapplication.dataclasses.SettingTitleDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.File

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding

    private lateinit var userID: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        userID = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference = Firebase.database.reference
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_setting).isChecked = true
        (activity as HomeActivity).title = "Setting"

        // Inflate the layout for this fragment with view binding
        binding = FragmentSettingBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val localFile = File.createTempFile("tempFile", "jpeg")
        FirebaseStorage.getInstance().getReference("Profile Images/$userID").getFile(localFile)
            .addOnSuccessListener {
                val bitmapFactory = BitmapFactory.decodeFile(localFile.absolutePath)
                Glide.with(requireActivity()).load(bitmapFactory).into(binding.profileImage)
            }
            .addOnFailureListener {
                Log.d("Failed to load profile image", it.message.toString())
            }

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        databaseReference.child("Users").child(userID)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userData = snapshot.getValue(ProfileDataClass::class.java)
                        binding.settingFragUserName.text = userData?.fullName
                        binding.settingFragUserEmail.text = userData?.email
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Failed to load user data", error.message)
                }
            })

        val settingList = arrayListOf<SettingTitleDataClass>()
        settingList.add(SettingTitleDataClass(R.drawable.baseline_person_4_24, "User Profile"))
        settingList.add(SettingTitleDataClass(R.drawable.baseline_info_24, "About Us"))
        settingList.add(SettingTitleDataClass(R.drawable.baseline_logout_24, "Logout"))

        binding.settingFragRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SettingListAdapter(context, settingList,
            object : SettingListAdapter.SettingInterface {
                override fun onCLick(userID: String) {
                    val bundle = Bundle()
                    bundle.putString("userID", userID)

                    val fragment = ProfileFragment()
                    fragment.arguments = bundle

                    activity!!.supportFragmentManager.beginTransaction()
                        .replace(R.id.layout_home, fragment, "PROFILE")
                        .addToBackStack(null)
                        .commit()
                }
            })
        }
    }

}