package com.codebyzebru.myapplication.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.codebyzebru.myapplication.R
import com.codebyzebru.myapplication.activities.HomeActivity
import com.codebyzebru.myapplication.adapters.SettingListAdapter
import com.codebyzebru.myapplication.dataclasses.SettingTitleDataClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_setting.*

class SettingFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        auth = FirebaseAuth.getInstance()
        database = Firebase.database.reference

        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("Users").child(userID).get()
            .addOnSuccessListener {
                settingFrag_user_name.text = it.child("fullName").value.toString()
                settingFrag_user_email.text = it.child("email").value.toString()
            }
            .addOnFailureListener {
                Toast.makeText(context, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        /*
                when the fragment come in picture, respected navigation `menu item` must be highlighted
                and `title` of the activity must be sync with fragment.
        */
        (activity as HomeActivity).naviView.menu.findItem(R.id.drawer_item_setting).isChecked = true
        (activity as HomeActivity).title = "Setting"

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val settingList = arrayListOf<SettingTitleDataClass>()
        settingList.add(SettingTitleDataClass(R.drawable.baseline_person_4_24, "User Profile"))
        settingList.add(SettingTitleDataClass(R.drawable.baseline_info_24, "About Us"))
        settingList.add(SettingTitleDataClass(R.drawable.baseline_logout_24, "Logout"))

        settingFrag_recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SettingListAdapter(context, settingList)
        }
    }

}